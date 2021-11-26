package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rid.videosapp.adapter.VideosAdapter
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Video



import com.rid.videosapp.utils.Utils
import com.rid.videosapp.utils.isInternetError
import com.rid.videosapp.viewModel.MainViewModel
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.runBlocking
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.RecyclerView
import com.rid.videosapp.databinding.FragmentHomeBinding
import android.view.Gravity



class HomeFragment : Fragment() {
    private lateinit var bindView: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    val TAG = "HomeFragment"

    private lateinit var vidAdapter: VideosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindView = FragmentHomeBinding.inflate(inflater, container, false)
        initialization()
        callingAndObservingViewModel()
        onClickListeners()
        //setPagination()
        passDataToVideoAdapter(viewModel.myList)


        return bindView.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun callingAndObservingViewModel() {
      //  viewModel.myList.clear()
        Log.d(TAG, "observer called")
        viewModel.pixelVideoSearchLiveData.observe(viewLifecycleOwner, {
            it.peekContent().let { resource ->
                when (resource) {
                    is Resource.Error -> {
                        if (resource.error?.isInternetError() == true) {
                            Utils.openBoottomSheet(requireContext())
                        }
                    }
                    is Resource.Loading -> {
                        bindView.pbBarId.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bindView.pbBarId.visibility = View.INVISIBLE
                        bindView.recViewMainId.visibility = View.VISIBLE
                        viewModel.myList.addAll(resource.response)
                        Log.d(TAG, "pixal added added done")
                        Log.d(TAG, "list size after pixal vid ${viewModel.myList.size}")
                        vidAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
        viewModel.pixaVideosSearchData.observe(viewLifecycleOwner, Observer {
            it.peekContent().let { pixaRec ->
                when (pixaRec) {
                    is Resource.Error -> {
                        if (pixaRec.error?.isInternetError() == true) {
                            Utils.openBoottomSheet(requireContext())
                        }
                    }
                    is Resource.Success -> {
                        viewModel.myList.addAll(pixaRec.response)
                        vidAdapter.notifyDataSetChanged()
                        // passDataToVideoAdapter(pixaRec.response)
                        Log.d(TAG, "pixabay added added done")
                        Log.d(TAG, "list size after pixabay vid ${viewModel.myList.size}")
                    }
                }
            }
        })
        val callbackOnBack=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callbackOnBack)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"onAttach called")


    }
    private fun initialization() {

        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        bindView.recViewMainId.layoutManager = staggeredGridLayoutManager

        bindView.recViewMainId.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    bindView.idBtnShowMore.visibility=View.VISIBLE
                }else{
                    bindView.idBtnShowMore.visibility=View.INVISIBLE
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun passDataToVideoAdapter(list: ArrayList<Video>) {
        viewModel.myList.addAll(list)
        vidAdapter = VideosAdapter(requireContext(), viewModel.myList, this)
        bindView.recViewMainId.adapter = vidAdapter
        vidAdapter.notifyDataSetChanged()
    }

    private fun onClickListeners() {


        bindView.customTbId.searchViewTbId.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    viewModel.myList.clear()
                    viewModel.queryToSearch = query
                    viewModel.pageForQuery=1
                    callViewModel(viewModel.queryToSearch, viewModel.pageForQuery, Constants.PAER_PAGE)
                    vidAdapter.notifyDataSetChanged()
                } else {
                    Utils.showToast(requireContext(), "enter query to search")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "onQueryTextChange Called")
                return false
            }

        })
        bindView.idBtnShowMore.setOnClickListener {
            requestForNewData()
        }
    }

    private fun callViewModel(query: String, page: Int, per_page: Int) {
        viewModel.getPixelVideos(query, page, per_page)
      //  viewModel.getPixaVideos(query, page, per_page)
    }

    private fun setPagination() {
        bindView.nestedScroolViewId.viewTreeObserver
            .addOnScrollChangedListener {
                if (bindView.nestedScroolViewId.getChildAt(0).getBottom()
                    <= bindView.nestedScroolViewId.getHeight() + bindView.nestedScroolViewId.getScrollY()
                ) {
                    //    scroll view is at bottom
                    viewModel.myList.clear()
                    Log.d(TAG, "page number before scrool $viewModel.page")
                    bindView.pbBarId.visibility = View.VISIBLE
                    bindView.recViewMainId.visibility = View.INVISIBLE
                    callViewModel(viewModel.queryToSearch, ++viewModel.page, Constants.PAER_PAGE)
                    Log.d(TAG, "page number after scrool ${viewModel.page}")
                } else {
                    //scroll view is not at bottom
                    bindView.pbBarId.visibility = View.INVISIBLE
                    bindView.recViewMainId.visibility = View.VISIBLE

                }
            }
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "mList size is in pause ${viewModel.myList.size}")
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume Called${viewModel.myList.size}")
    }

    fun requestForNewData() {
        Log.d(TAG, "size after list clear ${viewModel.myList.size}")
        Log.d(TAG, "page number before scrool ${viewModel.page}")
        bindView.pbBarId.visibility = View.VISIBLE
        bindView.recViewMainId.visibility = View.INVISIBLE
        callViewModel(viewModel.queryToSearch, ++viewModel.page, Constants.PAER_PAGE)
        Log.d(TAG, "page number after scrool ${viewModel.page}")
    }
}



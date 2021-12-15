package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rid.videosapp.R
import com.rid.videosapp.adapter.SearchVidAdapter
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.databinding.FragmentSearchVideosBinding
import com.rid.videosapp.utils.Utils
import com.rid.videosapp.utils.isInternetError
import com.rid.videosapp.viewModel.SearchViewModel
import com.rid.videosapp.viewModel.ViewModelFactory
import dev.sagar.lifescience.utils.Resource

class SearchVideos : Fragment() {
    private lateinit var bindView: FragmentSearchVideosBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    val TAG = "SearchVideos"
    var queryToSend=""
    private lateinit var bundle: Bundle
    var myList = ArrayList<Video>()
    private lateinit var vidAdapter: SearchVidAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queryToSend = arguments?.get(Constants.QUERY_KEY).toString()
        viewModel = ViewModelProvider(this,ViewModelFactory(queryToSend)).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindView = FragmentSearchVideosBinding.inflate(layoutInflater, container, false)
        return bindView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       viewModel.queryToSearch =queryToSend
        bindView.customTbId.searchViewTbId.setQuery(viewModel.queryToSearch,false)
        initialization()
        callingAndObservingViewModel()
        onClickListeners()
        passDataToVideoAdapter()
        bindView.customTbId.searchViewTbId.setQuery(queryToSend,false)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun callingAndObservingViewModel() {
        viewModel.pixelVideoSearchLiveData.observe(viewLifecycleOwner, {
            it.peekContent().let { resource ->
                when (resource) {
                    is Resource.Error -> {
                        if (resource.error?.isInternetError() == true) {
                            openNetworkErrorBottomSheet(requireContext())
                        }
                    }
                    is Resource.Loading -> {
                        bindView.pbBarId.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bindView.pbHorizontalId.visibility=View.INVISIBLE
                        bindView.pbBarId.visibility = View.INVISIBLE
                        bindView.recViewMainId.visibility = View.VISIBLE
                        if (viewModel.isNewData) {
                            myList.addAll(resource.response)
                            vidAdapter.notifyDataSetChanged()
                            viewModel.isNewData = false
                        }
                    }
                }
            }
        })
    }
    private fun initialization() {
        bundle= Bundle()
        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        staggeredGridLayoutManager.isAutoMeasureEnabled=false
        bindView.recViewMainId.layoutManager = staggeredGridLayoutManager
        bindView.recViewMainId.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    bindView.idBtnShowMore.visibility = View.VISIBLE
                } else {
                    bindView.idBtnShowMore.visibility = View.INVISIBLE
                }
            }
        })
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun passDataToVideoAdapter() {
        vidAdapter = SearchVidAdapter(requireContext(), myList)
        bindView.recViewMainId.hasFixedSize()
        bindView.recViewMainId.adapter = vidAdapter
    }
    private fun onClickListeners() {
        bindView.customTbId.searchViewTbId.setOnSearchClickListener {
            bindView.customTbId.btnSearchId.visibility = View.INVISIBLE
        }
        bindView.customTbId.searchViewTbId.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                    bundle.putString(Constants.QUERY_KEY,query)
                    val searchVideos=SearchVideos()
                    searchVideos.arguments=bundle
                    val ft: FragmentTransaction = fm.beginTransaction()
                    ft.replace(R.id.fragment_container,searchVideos)
                    ft.addToBackStack(SearchVideos().TAG)
                    ft.commit()
                    bindView.customTbId.searchViewTbId.setQuery(queryToSend,false)
                    vidAdapter.notifyDataSetChanged()
                } else {
                    Utils.showToast(requireContext(), getString(R.string.search_view_empty))
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        bindView.idBtnShowMore.setOnClickListener {
            requestForNewData()
        }
    }
    private fun callViewModel(query: String, page: Int, per_page: Int) {
        viewModel.getPixelVideos(query, page, per_page)
        viewModel.page++
    }

    private fun requestForNewData() {
        bindView.pbHorizontalId.visibility=View.VISIBLE
        viewModel.isNewData = true
        callViewModel(viewModel.queryToSearch, viewModel.page, Constants.PAER_PAGE)
        viewModel.page++
    }

    private fun openNetworkErrorBottomSheet(context: Context) {
        val bottomView =
            LayoutInflater.from(context).inflate(R.layout.network_error_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomView)
        val button = bottomView.findViewById<Button>(R.id.btRetry)
        button.setOnClickListener {
            callViewModel(
                viewModel.queryToSearch,
                viewModel.page,
                Constants.PAER_PAGE
            )
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.behavior.isDraggable=false
        bottomSheetDialog.show()
    }

}
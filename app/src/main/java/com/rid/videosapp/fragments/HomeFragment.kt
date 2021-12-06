package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rid.videosapp.adapter.VideosAdapter
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.utils.Utils
import com.rid.videosapp.utils.isInternetError
import com.rid.videosapp.viewModel.MainViewModel
import dev.sagar.lifescience.utils.Resource
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rid.videosapp.R
import com.rid.videosapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var bindView: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    val TAG = "HomeFragment"
    var myList = ArrayList<Video>()
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
        passDataToVideoAdapter()
        bundle= Bundle()

        return bindView.root
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun callingAndObservingViewModel() {
        Log.d(TAG, "observer called")
        viewModel.pixelVideoSearchLiveData.observe(viewLifecycleOwner, {
            it.peekContent().let { resource ->
                when (resource) {
                    is Resource.Error -> {
                        if (resource.error?.isInternetError() == true) {
                            openBoottomSheet(requireContext())
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
                            Log.d(TAG, "isnewdata is ${viewModel.isNewData} ")
                            myList.addAll(resource.response)
                            vidAdapter.notifyDataSetChanged()
                            viewModel.isNewData = false

                        } else {
                            Log.d(TAG, "no new data old is ${myList.size} ")
                        }
                    }
                }
            }
        })
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
        vidAdapter = VideosAdapter(requireContext(), myList, this)
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
                    bindView.pbBarId.visibility=View.VISIBLE
                    viewModel.isNewData = true
                    myList.clear()
                    viewModel.queryToSearch = query
                    viewModel.page = 1
                    callViewModel(
                        viewModel.queryToSearch,
                        viewModel.page,
                        Constants.PAER_PAGE
                    )
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
        viewModel.page++
    }

    fun requestForNewData() {
        bindView.pbHorizontalId.visibility=View.VISIBLE
        Log.d(TAG,"pagenumber for req${viewModel.page}")
        viewModel.isNewData = true
        callViewModel(viewModel.queryToSearch, viewModel.page, Constants.PAER_PAGE)
        viewModel.page++
        Log.d(TAG,"pagenumber for new req ${viewModel.page}")
    }

    fun openBoottomSheet(context: Context) {
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



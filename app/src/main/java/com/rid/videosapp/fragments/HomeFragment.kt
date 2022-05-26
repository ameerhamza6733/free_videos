package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.myapplication.room.DataBaseModule
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.rid.videosapp.R
import com.rid.videosapp.adapter.CategoriesAdapter
import com.rid.videosapp.adapter.VideosAdapter
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.TopCategories
import com.rid.videosapp.dataClasses.TopCategoriesArray
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.databinding.FragmentHomeBinding
import com.rid.videosapp.repostroy.SearchRepo
import com.rid.videosapp.utils.CommonKeys
import com.rid.videosapp.utils.Utils
import com.rid.videosapp.utils.isInternetError
import com.rid.videosapp.viewModel.SearchViewModel
import dev.sagar.lifescience.utils.Resource


class HomeFragment : Fragment() {
    private lateinit var searchRepo: SearchRepo
    private lateinit var bundle: Bundle
    private lateinit var bindView: FragmentHomeBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var gridLayoutManager: GridLayoutManager
    val TAG = "HomeFragment"
    var myVideosList = ArrayList<Video>()
    private lateinit var vidAdapter: VideosAdapter
    private lateinit var topCategoryAdapter: CategoriesAdapter
    private var nextPageUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoFromNotification()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindView = FragmentHomeBinding.inflate(inflater, container, false)
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        requireActivity().window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
        //     )
        initialization()
        callingAndObservingViewModel()
        onClickListeners()
        passDataToVideoAdapter()

        bundle = Bundle()

        return bindView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun callingAndObservingViewModel() {
        searchViewModel.searchHistoryLastItem(searchRepo)
        searchViewModel.categoriesLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        topCategoryAdapter=CategoriesAdapter(requireActivity(),resource.response)
                        bindView.recViewCategories.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                        bindView.recViewCategories.adapter=topCategoryAdapter
                    }
                }
            }
        }
        searchViewModel.pixelVideoSearchLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Error -> {
                        bindView.pbBarId.visibility = View.INVISIBLE
                        bindView.pbHorizontalId.visibility = View.INVISIBLE

                        if (resource.error?.isInternetError() == true) {
                            openBoottomSheet(requireContext())
                        } else {
                            Toast.makeText(
                                activity,
                                "error ${resource?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    is Resource.Loading -> {
                        bindView.customTbId.searchViewTbId.setQuery(
                            searchViewModel.queryToSearch,
                            false
                        )
                        bindView.pbBarId.visibility = View.VISIBLE

                    }
                    is Resource.Success -> {
                        bindView.pbHorizontalId.visibility = View.INVISIBLE
                        bindView.pbBarId.visibility = View.INVISIBLE
                        bindView.recViewMainId.visibility = View.VISIBLE
                        searchViewModel.saveSearchQuery(searchRepo)
                        if (searchViewModel.isNewData) {
                            if (resource.response.nextPageUrl.isNullOrEmpty()) {
                                bindView.idBtnShowMore.visibility = View.INVISIBLE
                            } else {
                                bindView.idBtnShowMore.visibility = View.VISIBLE

                            }
                            val oldListCount=myVideosList.size
                            myVideosList.addAll(resource.response.videos)
                            vidAdapter.notifyDataSetChanged()
                            bindView.recViewMainId.smoothScrollToPosition(oldListCount)
                            searchViewModel.isNewData = false
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initialization() {

        searchRepo =
            SearchRepo(DataBaseModule.getDatabase(requireActivity().applicationContext).Dao())

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        gridLayoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        bindView.recViewMainId.layoutManager = gridLayoutManager


    }


    @SuppressLint("NotifyDataSetChanged")
    private fun passDataToVideoAdapter() {
        vidAdapter = VideosAdapter(requireContext(), myVideosList, this)
        bindView.recViewMainId.adapter = vidAdapter
        //        val layout =
//            LayoutInflater.from(context)
//                .inflate(R.layout.native_frame_layout, null) as FrameLayout
//        val layoutAd =
//            LayoutInflater.from(context).inflate(R.layout.ad_unified, null) as NativeAdView
//        MyNativeAds.showNativeAds(context, layoutAd, layout)

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

                    bundle.putString(
                        CommonKeys.LOG_EVENT,
                        "searched query from HomeFragment " + query
                    )
                    firebaseAnalytics.logEvent(CommonKeys.SEARCHED_CLICKED, bundle)
                    val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                    bundle.putString(Constants.QUERY_KEY, query)
                    val searchVideos = SearchVideos()
                    searchVideos.arguments = bundle
                    val ft: FragmentTransaction = fm.beginTransaction()
                    ft.add(R.id.fragment_container, searchVideos)
                    ft.addToBackStack(HomeFragment().TAG)
                    ft.commit()
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

    private fun callViewModel() {

        searchViewModel.getPixelVideos()

    }

    fun requestForNewData() {
        bindView.pbHorizontalId.visibility = View.VISIBLE
        searchViewModel.isNewData = true
        callViewModel()
    }

    fun openBoottomSheet(context: Context) {
        val bottomView =
            LayoutInflater.from(context).inflate(R.layout.network_error_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomView)
        val button = bottomView.findViewById<Button>(R.id.btRetry)
        button.setOnClickListener {
            callViewModel(
            )
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.behavior.isDraggable = false
        bottomSheetDialog.show()
    }

    private fun videoFromNotification() {
        val myUrl = arguments?.getString(CommonKeys.VID_URL)
        if (myUrl != null) {
            val bundle = Bundle()
//            bundle.putString(CommonKeys.LOG_EVENT,"notification clicked")
//            firebaseAnalytics.logEvent(CommonKeys.NOTIFICATON_CLICKED,bundle)
            val playVideo = PlayVideo()
            bundle.putString(CommonKeys.VID_URL, myUrl)
            playVideo.arguments = bundle
            val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            ft.add(R.id.fragment_container, playVideo)
            ft.addToBackStack(TAG)
            ft.commit()
        }
    }

}



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
import com.rid.videosapp.viewModel.MainViewModel
import dev.sagar.lifescience.utils.Resource
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.rid.videosapp.R
import com.rid.videosapp.adapter.CategoriesAdapter
import com.rid.videosapp.databinding.FragmentHomeBinding
import com.rid.videosapp.utils.*


class HomeFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var bindView: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var gridLayoutManager: GridLayoutManager
    val TAG = "HomeFragment"
    var myVideosList = ArrayList<Video>()
    private lateinit var vidAdapter: VideosAdapter
    private lateinit var topCategoryAdapter:CategoriesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
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
                        bindView.pbHorizontalId.visibility = View.INVISIBLE
                        bindView.pbBarId.visibility = View.INVISIBLE
                        bindView.recViewMainId.visibility = View.VISIBLE
                        if (viewModel.isNewData) {
                            myVideosList.addAll(resource.response)
                            vidAdapter.notifyDataSetChanged()
                            viewModel.isNewData = false
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initialization() {


        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        gridLayoutManager =
            GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false)
        bindView.recViewMainId.layoutManager = gridLayoutManager
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
        bindView.recViewCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        topCategoryAdapter= CategoriesAdapter(requireContext(),Utils.addTopCategories())
        bindView.recViewCategories.adapter=topCategoryAdapter
        topCategoryAdapter.notifyDataSetChanged()

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun passDataToVideoAdapter() {
        vidAdapter = VideosAdapter(requireContext(), myVideosList, this)
        bindView.recViewMainId.hasFixedSize()
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
                    bundle.putString(CommonKeys.LOG_EVENT,"searched query from HomeFragment "+query)
                    firebaseAnalytics.logEvent(CommonKeys.SEARCHED_CLICKED,bundle)
                    val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                    bundle.putString(Constants.QUERY_KEY, query)
                    val searchVideos = SearchVideos()
                    searchVideos.arguments = bundle
                    val ft: FragmentTransaction = fm.beginTransaction()
                    ft.replace(R.id.fragment_container, searchVideos)
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

    private fun callViewModel(query: String, page: Int, per_page: Int) {
        viewModel.getPixelVideos(query, page, per_page)
        viewModel.page++
    }

    fun requestForNewData() {
        bindView.pbHorizontalId.visibility = View.VISIBLE
        viewModel.isNewData = true
        callViewModel(viewModel.queryToSearch, viewModel.page, Constants.PAER_PAGE)
        viewModel.page++
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



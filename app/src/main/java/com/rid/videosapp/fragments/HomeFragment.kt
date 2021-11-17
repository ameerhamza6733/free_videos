package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.rid.videosapp.adapter.VideosAdapter
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.databinding.FragmentHomeBinding

import com.rid.videosapp.utils.Utils
import com.rid.videosapp.utils.isInternetError
import com.rid.videosapp.viewModel.MainViewModel
import dev.sagar.lifescience.utils.Resource

class HomeFragment : Fragment() {
    private lateinit var bindView: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    var queryToSearch = Constants.POPULAR_SEARCHES
    val TAG = "HomeFragment"
    var page:Int=1
    private lateinit var myList: ArrayList<Video>
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
        initObsers()
        callViewModel(Constants.POPULAR_SEARCHES, page, 15)
        onClickListeners()
        setPagination()
        return bindView.root
    }


    private fun  initObsers(){
        viewModel.pixelVideoSearchLiveData.observe(viewLifecycleOwner,{
            it.peekContent().let { resource ->
                when(resource){
                    is Resource.Error->{
                        if (resource.error?.isInternetError()==true){
                            Utils.openBoottomSheet(requireContext())
                        }
                    }
                    is Resource.Loading->{
                        bindView.pbBarId.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        bindView.pbBarId.visibility=View.INVISIBLE
                        bindView.recViewMainId.visibility=View.VISIBLE
                        Log.d(TAG,"data is ${resource.response}")
                        passDataToVideoAdapter(resource.response)
                    }
                }
            }
        })
    }

    private fun initialization() {
        myList = ArrayList()
        bindView.recViewMainId.layoutManager=GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)

        }
    @SuppressLint("NotifyDataSetChanged")
    private fun passDataToVideoAdapter(list: ArrayList<Video>) {
        myList.addAll(list)
        Log.d(TAG, "my list size is ${list.size}")
        vidAdapter = VideosAdapter(requireContext(),myList)
        bindView.recViewMainId.adapter = vidAdapter
        vidAdapter.notifyDataSetChanged()
    }

    private fun onClickListeners() {
        bindView.customTbId.searchViewTbId.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    myList.clear()
                    queryToSearch = query

                    callViewModel(queryToSearch, page, 30)
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
    }

    private fun callViewModel(query: String, page:Int, per_page: Int) {
        Log.d(TAG,"view modeld called")
        viewModel.getPixelVideos(query, page, per_page)
    }

    private fun setPagination(){
        bindView.nestedScroolViewId.viewTreeObserver
            .addOnScrollChangedListener {
                if (        bindView.nestedScroolViewId.getChildAt(0).getBottom()
                    <=         bindView.nestedScroolViewId.getHeight() + bindView.nestedScroolViewId.getScrollY()
                ) {
                    //scroll view is at bottom
                 //   clearList()
                  bindView.pbBarId.visibility=View.VISIBLE
                    bindView.recViewMainId.visibility=View.INVISIBLE
                    callViewModel(queryToSearch,++page,15)

                } else {
                    //scroll view is not at bottom
                    bindView.pbBarId.visibility=View.INVISIBLE
                    bindView.recViewMainId.visibility=View.VISIBLE
                }
            }
    }
}



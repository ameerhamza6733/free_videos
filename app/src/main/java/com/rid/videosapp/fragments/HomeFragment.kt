package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rid.videosapp.R
import com.rid.videosapp.adapter.VideosAdapter
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.pixelVideo.response.DataFiles
import com.rid.videosapp.databinding.FragmentHomeBinding

import com.rid.videosapp.utils.Utils
import com.rid.videosapp.utils.toast
import com.rid.videosapp.viewModel.MainViewModel
import dev.sagar.lifescience.utils.Resource

class HomeFragment : Fragment() {
    private lateinit var bindView: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel

    var queryToSearch = ""
    val TAG = "HomeFragment"
    private lateinit var myList: ArrayList<DataFiles>
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
        callViewModel(Constants.POPULAR_SEARCHES, 1, 15)
        onClickListeners()
        return bindView.root
    }


    private fun  initObsers(){
        viewModel.pixelVideoSearchLiveData.observe(viewLifecycleOwner,{
            it.peekContent().let { resource ->
                when(resource){
                    is Resource.Error->{
                        toast("error ${resource.message}")
                    }
                    is Resource.Loading->{
                        bindView.pbBarId.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        bindView.pbBarId.visibility=View.INVISIBLE
                        bindView.recViewMainId.visibility=View.VISIBLE
                        passDataToVideoAdapter(resource.response.videos)
                        openBoottomSheet()
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
    private fun passDataToVideoAdapter(list: List<DataFiles>) {
        myList.addAll(list)
        Log.d(TAG, "my list size is ${myList.size}")
        vidAdapter = VideosAdapter(requireContext(), myList)
        bindView.recViewMainId.adapter = vidAdapter
        vidAdapter.notifyDataSetChanged()
    }

    private fun onClickListeners() {
        bindView.customTbId.searchViewTbId.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    queryToSearch = query
                    myList.clear()
                    callViewModel(queryToSearch, 1, 30)
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

    private fun callViewModel(query: String, page: Int, per_page: Int) {

        viewModel.getPixelVideos(query, page, per_page)
    }

    private fun openBoottomSheet(){
        val bottomView =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet, null)

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomView)
        bottomSheetDialog.show()
    }
}
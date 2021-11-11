package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.rid.videosapp.adapter.VideosAdapter
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.DataFiles
import com.rid.videosapp.dataClasses.VideoDetail
import com.rid.videosapp.dataClasses.VideoMainClass
import com.rid.videosapp.databinding.FragmentHomeBinding
import com.rid.videosapp.utils.Utils
import com.rid.videosapp.viewModel.MainViewModel

class HomeFragment : Fragment() {
    private lateinit var bindView: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var videosObserver: Observer<VideoMainClass>
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
        callViewModel(Constants.POPULAR_SEARCHES, 1, 30)
        onClickListeners()
        return bindView.root
    }


    private fun initialization() {
        myList = ArrayList()
        bindView.recViewMainId.layoutManager=GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)

        videosObserver = Observer {
            if (it != null) {
              //  Log.d(TAG, "data is ${it.videos}")
                Log.d(TAG,"video files are  ${it.videos[0].video_files[0].link}")
                passDataToVideoAdapter(it.videos)
            }
        }

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

        viewModel.getData(query, page, per_page)?.observe(viewLifecycleOwner, videosObserver)

    }
}
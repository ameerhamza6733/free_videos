package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.rid.videosapp.R
import com.rid.videosapp.`interface`.CallBackInterface
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.databinding.FragmentPlayVideoBinding
import com.rid.videosapp.dialog.CustomDialog
import com.rid.videosapp.utils.Utils
import com.rid.videosapp.utils.toast
import java.io.File
import java.lang.Exception

class PlayVideo : Fragment() {
    val args: PlayVideoArgs by navArgs()
    var myUrl = ""
    var ownerName = ""
    var duration = 0
    var vidId=0

    val TAG = "PlayVideo"
    private lateinit var rootView: FragmentPlayVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = FragmentPlayVideoBinding.inflate(layoutInflater, container, false)
        myUrl = args.url
        ownerName = args.name
        duration = args.duration
        vidId=args.id
        initialization()
        onClickListneres()
        return rootView.root

    }

    override fun onPause() {
        super.onPause()
      //  stopMediaPlayer()
        rootView.exoplayerViewId.stopPlayback()
    }

    override fun onStop() {
        super.onStop()
      //  stopMediaPlayer()
        rootView.exoplayerViewId.stopPlayback()
    }

    private fun initialization() {
        rootView.exoplayerViewId.setVideoURI(Uri.parse(myUrl))
        rootView.exoplayerViewId.start()

        rootView.vidOwnerTagId.text = ownerName
        rootView.vidDurationId.text = duration.toString()

    }

    fun onClickListneres() {
        rootView.shareId.setOnClickListener {
            Utils.shareImg(requireContext(), myUrl)
        }
        rootView.downloadId.setOnClickListener {
          CustomDialog.showCustomDialog(requireContext(),ownerName,object :CallBackInterface{
              override fun onPositiveCallBack() {
                  downloadVideo(myUrl)
              }
          })
        }
        rootView.exoplayerViewId.setOnClickListener {

        }
        rootView.exoplayerViewId.setOnPreparedListener {
            it.isLooping=true
            startCountDown()
        }
        rootView.exoplayerViewId.setOnCompletionListener {
         startCountDown()
        }
    }

    private fun downloadVideo(url:String) {
        try {
            val downloadmanager =
                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.vid))
                .setDescription("Downloading $ownerName Video")
                .setAllowedOverMetered(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(
                    Uri.fromFile(
                        File(
                            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                .toString() + Constants.DESTINATION_FOLDER, "$ownerName$vidId.mp4"
                        )
                    )
                )
            downloadmanager.enqueue(request)
            toast(getString(R.string.downloading))

        } catch (e: Exception) {
            Log.d(TAG, "failed ${e.message}")
        }
    }

    fun startCountDown(){
        object : CountDownTimer((duration*1000).toLong(), 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                rootView.vidDurationId.setText(""+millisUntilFinished / 1000)
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                startCountDown()
            }
        }.start()
    }
}
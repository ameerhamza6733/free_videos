package com.rid.videosapp.fragments

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var videoPlayer: SimpleExoPlayer
    val args: PlayVideoArgs by navArgs()
    var myUrl = ""
    var ownerName = ""
    var duration = 0
    var vidId=0
    val TAG = "PlayVideo"
    private lateinit var rootView: FragmentPlayVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSourceFactory =
            DefaultDataSourceFactory(requireContext(), getString(R.string.exo))
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
        stopMediaPlayer()
    }

    override fun onStop() {
        super.onStop()
        stopMediaPlayer()
    }

    private fun initialization() {
        videoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        videoPlayer.playWhenReady = true
        rootView.exoplayerViewId.player = videoPlayer
        preparePlayer(myUrl, Constants.TYPE)
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
    }

    private fun buildMediaSource(uri: Uri, type: String): MediaSource {
        return if (type == getString(R.string.dash)) {
            DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        }
    }

    private fun preparePlayer(videoUrl: String, type: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri, type)
        videoPlayer.prepare(mediaSource)
    }

    private fun stopMediaPlayer() {
        if (videoPlayer.isPlaying) {
            videoPlayer.stop()
            videoPlayer.release()
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
}
package com.rid.videosapp.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.rid.videosapp.databinding.FragmentPlayVideoBinding
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.rid.videosapp.R

class PlayVideo : Fragment() {
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var videoPlayer: SimpleExoPlayer
    val args: PlayVideoArgs by navArgs()
    var myUrl=""
    val TAG="PlayVideo"
    private lateinit var rootView:FragmentPlayVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSourceFactory =
            DefaultDataSourceFactory(requireContext(), getString(R.string.exo))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView= FragmentPlayVideoBinding.inflate(layoutInflater,container,false)
        myUrl=args.url
        initialization()
        Log.d(TAG,"videos link is $myUrl")
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

    private fun initialization(){
        videoPlayer= SimpleExoPlayer.Builder(requireContext()).build()
        videoPlayer.playWhenReady = true
        rootView.exoplayerViewId.player=videoPlayer
       preparePlayer(myUrl,".mp4")

//        val uri=Uri.parse(myUrl)
//        rootView.videoViewId.setVideoURI(uri)
//        rootView.videoViewId.start()

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
    private fun stopMediaPlayer(){
        if (videoPlayer.isPlaying){
            videoPlayer.stop()
            videoPlayer.release()
        }
    }
}
package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import com.rid.videosapp.R
import com.rid.videosapp.constants.Constants
import java.io.File
import java.lang.Exception
import android.media.MediaPlayer.OnPreparedListener
import android.view.*
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.rid.videosapp.customeDialog.DownloadDialog
import com.rid.videosapp.databinding.FragmentPlayVideoBinding
import com.rid.videosapp.utils.*

class PlayVideo : Fragment() {
    private lateinit var bundle: Bundle
    var myUrl = ""
    var ownerName = ""
    var duration = 0
    var vidId = 0
    val TAG = "PlayVideo"
    var vidDuration = 0
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var dialog: Dialog
    private lateinit var rootView: FragmentPlayVideoBinding
    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            //Fetching the download id received with the broadcast

            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val  mDownloadManager = context!!
                .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val mostRecentDownload: Uri =
                mDownloadManager.getUriForDownloadedFile(id)
            Log.d(TAG,"wallaper downlaod ${mostRecentDownload.path} ${id}")
            //Checking if the received broadcast is for our enqueued download by matching download id
            DownloadUtils.mVideoWallpaper.setToWallPaper(context,mostRecentDownload,false)

        }


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity(). registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = FragmentPlayVideoBinding.inflate(layoutInflater, container, false)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        MyRewardedAds.loadRewardedAd(requireContext())
        return rootView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gettingDataFromArguments()
        initialization()
        onClickListneres()

    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        super.onPause()
        rootView.exoplayerViewId.stopPlayback()
    }

    override fun onStop() {
        super.onStop()
        rootView.exoplayerViewId.stopPlayback()
    }

    private fun gettingDataFromArguments() {

        myUrl = arguments?.getString(CommonKeys.VID_URL).toString()
        ownerName = arguments?.getString(CommonKeys.OWNER).toString()
        if (arguments != null) {
            duration = requireArguments().getInt(CommonKeys.DURATION, 0)
            vidId = requireArguments().getInt(CommonKeys.VID_ID, 0)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initialization() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        dialog = Dialog(requireContext())
        showProgressDialog(ownerName, duration.toString())
        rootView.exoplayerViewId.setVideoURI(Uri.parse(myUrl))
        rootView.vidOwnerTagId.text = ownerName
        rootView.vidDurationId.text = duration.toString() + getString(R.string.sec)
    }


    override fun onDestroy() {
        requireActivity(). unregisterReceiver(onDownloadComplete);
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    fun onClickListneres() {
        rootView.downloadId.setOnClickListener {
            DownloadDialog(myUrl,ownerName).show(parentFragmentManager,TAG)
        }

        rootView.shareId.setOnClickListener {
            Utils.shareImg(requireContext(), myUrl)
        }

        rootView.exoplayerViewId.setOnPreparedListener {
            it.isLooping = true
            dialog.dismiss()
            bundle.putString(CommonKeys.LOG_EVENT, "video play successfully")
            firebaseAnalytics.logEvent(CommonKeys.VID_PLAYING, bundle)
            rootView.exoplayerViewId.start()
        }
        rootView.exoplayerViewId.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
                openBottomSheetForError(requireContext(), getString(R.string.error))
                bundle.putString(CommonKeys.LOG_EVENT, "internet error while playing video")
                firebaseAnalytics.logEvent(CommonKeys.INTERNET_ERROR, bundle)
                return false
            }
        })
        rootView.exoplayerViewId.setOnPreparedListener(OnPreparedListener { mp ->
            mp.start()
            mp.setOnVideoSizeChangedListener { mp, arg1, arg2 -> // TODO Auto-generated method stub
                mp.start()
                mp.isLooping = true
                dialog.dismiss()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showProgressDialog(ownerName: String, duration: String) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custome_dialog)
        val tvDialogOwner = dialog.findViewById<TextView>(R.id.tv_owner_name_id)
        val tvDialogDuration = dialog.findViewById<TextView>(R.id.tv_duraton)
        val btnBackDialog = dialog.findViewById<ImageView>(R.id.iv_btn_back_dialog)
        btnBackDialog.setOnClickListener {
            goBackToMain()
        }
        tvDialogOwner.text = ownerName
        tvDialogDuration.text = duration + getString(R.string.sec)
        dialog.create()
        dialog.show()
    }

    fun openBottomSheetForError(context: Context, errorMessage: String) {
        val bottomView =
            LayoutInflater.from(context).inflate(R.layout.bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomView)

        val tvMsg = bottomView.findViewById<TextView>(R.id.text_msg)
        tvMsg.text = errorMessage
        val btnReload = bottomView.findViewById<TextView>(R.id.btn_reload)
        val btnGoBack = bottomView.findViewById<TextView>(R.id.id_go_back)
        btnGoBack.setOnClickListener {
            goBackToMain()
            bottomSheetDialog.dismiss()
        }
        btnReload.setOnClickListener {

            bundle.putString(CommonKeys.LOG_EVENT, "retry for video")
            firebaseAnalytics.logEvent(CommonKeys.RETRY_CLICKED, bundle)

            rootView.exoplayerViewId.stopPlayback()
            rootView.exoplayerViewId.setVideoURI(Uri.parse(myUrl))
            rootView.exoplayerViewId.start()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.behavior.isDraggable = false
        bottomSheetDialog.show()

    }

    private fun goBackToMain() {
        if (rootView.exoplayerViewId.isPlaying) {
            rootView.exoplayerViewId.stopPlayback()
        }
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        requireActivity().onBackPressed()
    }
}
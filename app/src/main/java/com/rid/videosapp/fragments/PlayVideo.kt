package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.*
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.rid.videosapp.R
import com.rid.videosapp.customeDialog.DownloadDialog
import com.rid.videosapp.dataClasses.MostDownloaded
import com.rid.videosapp.databinding.FragmentPlayVideoBinding
import com.rid.videosapp.servieces.VideoWallpaper
import com.rid.videosapp.sharedViewModel.SharedViewModel
import com.rid.videosapp.utils.CommonKeys
import com.rid.videosapp.utils.DownloadUtils
import com.rid.videosapp.utils.MyRewardedAds
import com.rid.videosapp.utils.Utils
import com.rid.videosapp.viewModel.FileUtilViewModel
import dev.sagar.lifescience.utils.Resource

class PlayVideo : Fragment() {
    private  var bundle= Bundle()
    private val myRewardedAds=MyRewardedAds()
    private var mediaPlayer:MediaPlayer?=null
    private val shareViewMode by activityViewModels<SharedViewModel> ()
    private val fileViewModel by viewModels<FileUtilViewModel> ()
    var myUrl = ""
    var ownerName = ""
    var duration = 0
    var vidId = 0
    val TAG = "PlayVideo"
    var vidDuration = 0
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private  var dialog: Dialog?=null
    private lateinit var rootView: FragmentPlayVideoBinding
    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val  mDownloadManager = context!!
                .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val mostRecentDownload: Uri = mDownloadManager.getUriForDownloadedFile(id)
            fileViewModel.renameFile("wallpaper.mp4","currentWallpaper.mp4")
            rootView.downloadId.isEnabled=true
            rootView.progressDownload.visibility=View.INVISIBLE
            if (dialog?.isShowing==true){
                dialog?.dismiss()
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = FragmentPlayVideoBinding.inflate(layoutInflater, container, false)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        return rootView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gettingDataFromArguments()
        initialization()
        onClickListneres()
        initObserver()

    }

    fun setToWallPaper(context: Context) {

        val setWallaperIntent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        setWallaperIntent.putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(
                context,
                VideoWallpaper::class.java
            )
        )
        setWallaperIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(setWallaperIntent)


    }


    private fun initObserver(){
        fileViewModel.liveDataReanmeFile.observe(viewLifecycleOwner){
            when(it){
              is Resource.Success->{
                 setToWallPaper(requireContext())
              }
                is Resource.Error->{
                    Toast.makeText(requireActivity(),"Error ${it.error}",Toast.LENGTH_SHORT).show()
                }
            }
        }
        shareViewMode.liveDataShowRewardedVideoAd.observe(viewLifecycleOwner) {

            if (it) {
                myRewardedAds.setFullScreenContnt(
                    requireActivity(),
                    getString(R.string.admob_video_ad)
                )
                if (myRewardedAds.mRewardedAd == null) {
                    dowloadWallaper()
                }
                myRewardedAds.mRewardedAd?.show(requireActivity()) {
                    dowloadWallaper()
                }

            }
        }
    }

    private fun dowloadWallaper(){
        rootView.progressDownload.visibility=View.VISIBLE
        rootView.downloadId.isEnabled=false
        fileViewModel.downloadFile(myUrl,ownerName)

    }

    override fun onDetach() {
        super.onDetach()
        if (dialog?.isShowing==true){
            dialog?.dismiss()
        }
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        requireActivity().applicationContext. unregisterReceiver(onDownloadComplete);
        if (mediaPlayer?.isPlaying==true){
            mediaPlayer?.pause()

        }
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
       requireActivity().applicationContext. registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        try {
           mediaPlayer?.start()
       }catch (e:Exception){
           e.printStackTrace()
       }

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




    @SuppressLint("SetTextI18n")
    fun onClickListneres() {
        rootView.downloadId.setOnClickListener {

            dowloadWallaper()
        }

        rootView.shareId.setOnClickListener {
            Utils.shareImg(requireContext(), myUrl)
        }

        rootView.exoplayerViewId.setOnPreparedListener {
            it.isLooping = true
            dialog?.dismiss()
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
            mediaPlayer=mp
            mediaPlayer?.start()
            mediaPlayer?.setOnVideoSizeChangedListener { mp, arg1, arg2 -> // TODO Auto-generated method stub
                mediaPlayer?.start()
                mediaPlayer?.isLooping = true
                dialog?.dismiss()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showProgressDialog(ownerName: String, duration: String) {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.custome_dialog)
        val tvDialogOwner = dialog?.findViewById<TextView>(R.id.tv_owner_name_id)
        val tvDialogDuration = dialog?.findViewById<TextView>(R.id.tv_duraton)
        val btnBackDialog = dialog?.findViewById<ImageView>(R.id.iv_btn_back_dialog)
        btnBackDialog?.setOnClickListener {
            goBackToMain()
        }
        tvDialogOwner?.text = ownerName
        tvDialogDuration?.text = duration + getString(R.string.sec)
       if (dialog?.isShowing==true){
           dialog?.dismiss()

       }
        dialog?.create()
        dialog?.show()
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
        if (dialog?.isShowing==true) {
            dialog?.dismiss()
        }
        mediaPlayer?.pause()
        mediaPlayer?.release()

        requireActivity().onBackPressed()
    }
}
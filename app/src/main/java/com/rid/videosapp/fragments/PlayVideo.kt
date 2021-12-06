package com.rid.videosapp.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import com.rid.videosapp.R
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.utils.Utils
import com.rid.videosapp.utils.toast
import java.io.File
import java.lang.Exception
import android.media.MediaPlayer.OnPreparedListener
import android.view.*
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rid.videosapp.databinding.FragmentPlayVideoBinding


class PlayVideo : Fragment() {
    var myUrl = ""
    var ownerName = ""
    var duration = 0
    var vidId = 0
    val TAG = "PlayVideo"
    private lateinit var dialog: Dialog
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

        myUrl = arguments?.getString("myUrl").toString()
        ownerName = arguments?.getString("ownerName").toString()
        duration = arguments?.getInt("duration", 0)!!
        vidId = arguments?.getInt("vidId", 0)!!
        initialization()
        onClickListneres()
        return rootView.root
    }

    override fun onPause() {
        super.onPause()
        rootView.exoplayerViewId.stopPlayback()
    }

    override fun onStop() {
        super.onStop()
        rootView.exoplayerViewId.stopPlayback()
    }

    private fun initialization() {
        dialog = Dialog(requireContext())
        ShowDialog(ownerName, duration.toString())
        rootView.exoplayerViewId.setVideoURI(Uri.parse(myUrl))
        rootView.exoplayerViewId.start()
        rootView.vidOwnerTagId.text = ownerName
        rootView.vidDurationId.text = duration.toString()
    }

    fun onClickListneres() {
        rootView.shareId.setOnClickListener {
            Utils.shareImg(requireContext(), myUrl)
        }

        rootView.exoplayerViewId.setOnPreparedListener {
            it.isLooping = true
            startCountDown()
        }
        rootView.exoplayerViewId.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
                openBoottomSheet(requireContext(), getString(R.string.error))
                Log.d(TAG, "P1 is $p1 P2 is $p2")
                return false
            }

        })

        rootView.exoplayerViewId.setOnClickListener {

        }
        rootView.exoplayerViewId.setOnPreparedListener(OnPreparedListener { mp ->
            mp.start()
            mp.setOnVideoSizeChangedListener { mp, arg1, arg2 -> // TODO Auto-generated method stub
                Log.e(TAG, "Changed")
                mp.start()
                mp.isLooping = true
                dialog.dismiss()
                startCountDown()
            }
        })
    }

    private fun downloadVideo(url: String) {
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

    fun startCountDown() {
        object : CountDownTimer((duration * 1000).toLong(), 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                rootView.vidDurationId.setText("" + millisUntilFinished / 1000)
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                startCountDown()
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun ShowDialog(ownerName: String, duration: String) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custome_dialog);
        val tvDialogOwner = dialog.findViewById<TextView>(R.id.tv_owner_name_id)
        val tvDialogDuration = dialog.findViewById<TextView>(R.id.tv_duraton)
        val btnBackDialog=dialog.findViewById<ImageView>(R.id.iv_btn_back_dialog)
        btnBackDialog.setOnClickListener {
            goBackToMain()
        }
        tvDialogOwner.text = ownerName
        tvDialogDuration.text = "$duration Sec"
        dialog.create()
        dialog.show()
    }

    fun openBoottomSheet(context: Context, errorMessage: String) {
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
            rootView.exoplayerViewId.stopPlayback()
            rootView.exoplayerViewId.setVideoURI(Uri.parse(myUrl))
            rootView.exoplayerViewId.start()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.behavior.isDraggable=false
        bottomSheetDialog.show()

    }
    private fun goBackToMain(){
        rootView.exoplayerViewId.stopPlayback()
        dialog.dismiss()
        requireActivity().onBackPressed()
    }
}
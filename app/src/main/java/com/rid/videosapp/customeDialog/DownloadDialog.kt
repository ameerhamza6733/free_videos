package com.rid.videosapp.customeDialog
import android.app.DownloadManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rid.videosapp.dataClasses.MostDownloaded
import com.rid.videosapp.databinding.DownlaodDialogBinding
import com.rid.videosapp.utils.DownloadUtils

import com.rid.videosapp.utils.MyRewardedAds
import java.util.*
import android.widget.Toast

import com.rid.videosapp.MainActivity

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.rid.videosapp.sharedViewModel.SharedViewModel


class DownloadDialog() : DialogFragment() {
    private lateinit var viewBinding: DownlaodDialogBinding
    private val sharedViewModel by activityViewModels<SharedViewModel> ()

    private val db = Firebase.firestore
    val TAG = "DownloadDialog"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Material_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DownlaodDialogBinding.inflate(layoutInflater, container, false)

        return viewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    override fun onStart() {
        super.onStart()
        if (getDialog() != null && getDialog()?.getWindow() != null) {
            getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }
    }




    private fun setOnClickListeners() {
        dialog?.setCancelable(false)
        viewBinding.tvWatchVideo.setOnClickListener {

            sharedViewModel.liveDataShowRewardedVideoAd.value=true

            dialog?.dismiss()
        }
        viewBinding.tvDialogCancel.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun insertDownloadedDataToFb(data: MostDownloaded,uid:String) {
        db.collection("MostDownloadedList")
            .document(uid)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG,"data inserted")
            }
            .addOnFailureListener {
                Log.d(TAG,"Error ${it.message}")
            }
    }


}

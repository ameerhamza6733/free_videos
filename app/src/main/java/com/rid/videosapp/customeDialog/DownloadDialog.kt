package com.rid.videosapp.customeDialog
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

class DownloadDialog(val url: String, val ownerName: String) : DialogFragment() {
    private lateinit var viewBinding: DownlaodDialogBinding
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
        MyRewardedAds.loadRewardedAd(requireContext())
        return viewBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOnClickListeners() {
        dialog?.setCancelable(false)
        viewBinding.tvWatchVideo.setOnClickListener {
            val myUid=125005
            val encodedString: String = Base64.getEncoder().encodeToString(myUid.toString().toByteArray())
            Log.d(TAG, "encoded value is $encodedString against $ownerName")
            MyRewardedAds.showRewardedVideo(requireActivity(), requireContext())
            val mostDownloaded=MostDownloaded(url,ownerName)
            insertDownloadedDataToFb(mostDownloaded,encodedString)

            DownloadUtils.downloadFile(
                url,
                DownloadUtils.RootDirectoryFB,
                requireContext(),
                ownerName + System.currentTimeMillis() + ".mp4"
            )
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

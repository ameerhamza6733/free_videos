package dev.sagar.lifescience.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rid.videosapp.R
import com.rid.videosapp.databinding.NetworkErrorBottomSheetBinding
import com.rid.videosapp.sharedViewModel.SharedViewModel


class NetworkErrorBottomSheetFragment : BottomSheetDialogFragment(){
    private val binding by viewBinding(NetworkErrorBottomSheetBinding::bind)
    private val sharedViewModel by activityViewModels<SharedViewModel> ()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.network_error_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btRetry.setOnClickListener {
                sharedViewModel.message.value=true
                dismiss()
            }
        }
    }

}
package com.blogappdemo.ui.auth

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blogappdemo.R
import com.blogappdemo.core.Result
import com.blogappdemo.databinding.FragmentSetupProfileBinding
import com.blogappdemo.presentation.auth.AuthViewModel
import com.blogappdemo.utils.Constants.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupProfileFragment : Fragment(R.layout.fragment_setup_profile) {

    private lateinit var binding: FragmentSetupProfileBinding
    private var bitmap: Bitmap? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>
    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupProfileBinding.bind(view)

        setupActivityResult()
        setupCreateProfile()
        setupTakePictureIntent()

    }

    private fun setupActivityResult() {
        //solucion al onActivityResult @deprecated
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = it.data
                    val imageBitmap = data?.extras?.get(DATA) as Bitmap
                    binding.ivProfileImage.setImageBitmap(imageBitmap)
                    bitmap = imageBitmap
                }
            }
    }

    private fun setupCreateProfile() {
        binding.btnCreateProfile.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val alertDialog =
                AlertDialog.Builder(requireContext()).setTitle(R.string.uploading_photo).create()
            bitmap?.let {
                if (username.isNotEmpty()) {
                    viewModel.updateUserProfile(imageBitmap = it, username = username)
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    alertDialog.show()
                                }
                                is Result.Success -> {
                                    alertDialog.dismiss()
                                    findNavController().navigate(R.id.action_setupProfileFragment_to_homeScreenFragment)
                                }
                                is Result.Failure -> {
                                    alertDialog.dismiss()
                                }
                            }
                        }
                }
            }
        }
    }

    private fun setupTakePictureIntent() {
        val btnProfileImage = binding.ivProfileImage
        btnProfileImage.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    //abrir la camara
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), (R.string.camera_app_not_found), Toast.LENGTH_SHORT).show()
        }
    }
}
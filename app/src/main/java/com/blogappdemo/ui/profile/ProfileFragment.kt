package com.blogappdemo.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blogappdemo.R
import com.blogappdemo.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        val user = FirebaseAuth.getInstance().currentUser
        Glide.with(this).load(user?.photoUrl).centerCrop().into(binding.imgProfile)
        binding.imgProfile
        binding.txtProfileNickname.text = user?.displayName
        binding.txtProfileEmail.text = user?.email
        Log.d("Usuario:", "fotourl: ${user?.photoUrl} , nombre: ${user?.displayName} ")
    }
}
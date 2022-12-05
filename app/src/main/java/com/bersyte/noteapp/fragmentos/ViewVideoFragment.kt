package com.bersyte.noteapp.fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bersyte.noteapp.databinding.FragmentViewVideoBinding

class ViewVideoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewVideoBinding.inflate(layoutInflater)

        binding.viewVideo.setVideoURI(arguments?.getString("path").toString().toUri())
        binding.viewVideo.start()
        binding.descriptionVV.setText(arguments?.getString("description"))
        binding.descriptionVV.isEnabled = false

        return binding.root
    }

}
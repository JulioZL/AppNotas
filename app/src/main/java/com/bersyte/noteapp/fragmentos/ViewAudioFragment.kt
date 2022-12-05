package com.bersyte.noteapp.fragmentos

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bersyte.noteapp.databinding.FragmentViewAudioBinding
import java.io.IOException

//private const val LOG_TAG = "AudioRecordTest"
class ViewAudioFragment : Fragment() {
    private var player: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewAudioBinding.inflate(layoutInflater)

        binding.descriptionAudio.setText(arguments?.getString("description"))
        binding.descriptionAudio.isEnabled = false

        //Play audio
        player = MediaPlayer().apply {
            try {
                setDataSource(arguments?.getString("path"))
                prepare()
                start()
            } catch (e: IOException) {
                //Log.e(LOG_TAG, "prepare() failed")
            }
        }

        return binding.root
    }
}
package com.creativeoffice.Share


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.creativeoffice.instakotlin2.R
import com.otaliastudios.cameraview.CameraView
//import com.otaliastudios.cameraview.controls.Mode
import kotlinx.android.synthetic.main.fragment_share_video.*
import kotlinx.android.synthetic.main.fragment_share_video.view.*

/**
 * A simple [Fragment] subclass.
 */
class ShareVideoFragment : Fragment() {

    lateinit var videoView1:CameraView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_share_video, container, false)

        videoView1 = view.videoViewFoto
      //  videoView1.mode = Mode.VIDEO
    //    videoView1.setLifecycleOwner(viewLifecycleOwner)
        return view
    }

    override fun onResume() {

        super.onResume()
        Log.e("kamera","video Kamera calıstı")
        videoView1.start()

    }

    override fun onPause() {
        super.onPause()
        Log.e("kamera"," video Kamera durdu")

       // videoView1.close()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("kamera"," video Kamera öldü")

        videoView1.destroy()
    }
}

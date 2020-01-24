package com.creativeoffice.Share


import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.EventbusDataEvents
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.fragment_share_camera.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A simple [Fragment] subclass.
 */
class ShareCameraFragment : Fragment() {
    lateinit var cameraView: CameraView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view = inflater.inflate(R.layout.fragment_share_camera, container, false)

        cameraView = view.FotoCameraView
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM) //zoom ozeligi actık
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS) //focusozeligi actık
        // cameraView.setLifecycleOwner(viewLifecycleOwner)

        view.imgFotoCek.setOnClickListener {
            cameraView.capturePicture()
            Log.e("kamera", "foto cekildi")
        }

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)
                var cekilenFotoAdi = System.currentTimeMillis()

                var cekilenFotoYolu = File((Environment.getExternalStorageDirectory()).absolutePath + "/DCIM/Camera/" + cekilenFotoAdi + ".jpg")

                var dosyaOlustur = FileOutputStream(cekilenFotoYolu)

                dosyaOlustur.write(jpeg)
                dosyaOlustur.close()

                view.constraintLayoutRoot.visibility = View.GONE
                view.frameLayoutContainer.visibility = View.VISIBLE
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                ////////////////////// EventBus yayın yaptık.
                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(cekilenFotoYolu.absolutePath.toString(), true))
                //////////////////////
                transaction.addToBackStack("ShareNexteklendi")
                transaction.replace(R.id.frameLayoutContainer, ShareNextFragment())
                transaction.commit()

                Log.e("kamera", "cekilne resım kaydedıldı" + cekilenFotoYolu.absoluteFile.absolutePath.toString())
            }
        })

        view.imgGeri.setOnClickListener {
            activity!!.onBackPressed()
        }
        return view
    }

    override fun onResume() {

        super.onResume()
        Log.e("kamera", "foto Kamera calıstı")
        //cameraView.start()

    }

    override fun onPause() {
        super.onPause()
        Log.e("kamera", "foto Kamera durdu")

        cameraView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("kamera", "foto Kamera öldü")
        if (cameraView != null) {
            cameraView.destroy()
        }

    }

}

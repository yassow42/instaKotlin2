package com.creativeoffice.Home

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.creativeoffice.Share.ShareNextFragment
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.DosyaIslemleri
import com.creativeoffice.utils.EventbusDataEvents
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import kotlinx.android.synthetic.main.fragment_camera.view.imgFotoCek
import kotlinx.android.synthetic.main.fragment_share_camera.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.FileOutputStream

class CameraFragment : Fragment() {
    lateinit var cameraView: CameraView
    var izinBilgisi: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { //saveInstance ise uygulama yan dondugunde hersey sıl bastan yapılır bunu engeller verileri korur. ınflater java kodlarını xml e cevırır. 
        var view = inflater.inflate(R.layout.fragment_camera, container, false) //biz fragmenti nereye koyarsak container orasıdır.

        cameraView = view.FotoCameraViewHome
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM) //zoom ozeligi actık
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS) //focusozeligi actık
        // cameraView.setLifecycleOwner(viewLifecycleOwner)

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)
                var cekilenFotoAdi = System.currentTimeMillis()
                var cekilenFotoYolu = File((Environment.getExternalStorageDirectory()).absolutePath + "/DCIM/Camera/" + cekilenFotoAdi + ".jpg")
                var dosyaOlustur = FileOutputStream(cekilenFotoYolu)

                dosyaOlustur.write(jpeg)
                dosyaOlustur.close()


                Log.e("kameraHome","cekilen resim Kaydedildi $cekilenFotoYolu ")


            }
        })


        view.imgFotoCek.setOnClickListener {

            if (cameraView!!.facing == Facing.BACK) {
                cameraView.capturePicture()
            } else {
                cameraView.captureSnapshot()
            }

           // Log.e("kameraHome", "foto cekildi")
        }

        view.imgCameraSwitch.setOnClickListener {
            if (cameraView!!.facing == Facing.BACK) {
                cameraView!!.facing = Facing.FRONT
            } else if (cameraView!!.facing == Facing.FRONT) {
                cameraView!!.facing = Facing.BACK
            }
        }





        return view
    }


    override fun onResume() {

        super.onResume()

        if (izinBilgisi == true) {
            Log.e("kameraHome", "foto Kamera calıstı")
          //  cameraView.start()
        }else if (izinBilgisi==false)  {
            Log.e("kameraHome", "foto Kamera izni verilmemiş")

        }


    }

    override fun onPause() {
        super.onPause()
        Log.e("kameraHome", "foto Kamera durdu")

        cameraView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("kameraHome", "foto Kamera Destroy")
      //  if (cameraView != null) {
            cameraView.destroy()
      //  }

    }

    //////////////////////eventbuss//////////////////////////
    @Subscribe(sticky = true)

    internal fun onDosyaEvent(izinVerildigiSorgusu: EventbusDataEvents.KameraIzinBilgisiGonder) {
        izinBilgisi = izinVerildigiSorgusu.IzinVerildiMi!!
    }

    override fun onAttach(context: Context) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }


}
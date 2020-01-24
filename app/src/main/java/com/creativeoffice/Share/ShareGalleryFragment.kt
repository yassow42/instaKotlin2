package com.creativeoffice.Share

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager

import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.*
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ShareGalleryFragment : Fragment() {

    var secilenDosyaYolu: String? = null

    var dosyaTuruResimMi: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_share_gallery, container, false)


        var klasorPaths = ArrayList<String>()
        var klasorAdlari = ArrayList<String>()

        var root = Environment.getExternalStorageDirectory().path


        var kameraResimleri = root + "/DCIM/Camera"
        var indirilenResimler = root + "/Download"
        var whatsappResimleri = root + "/WhatsApp/Media/WhatsApp Images"
        var videolar = root + "/DCIM/Video"
        var test = root + "/DCIM/TestKlasor"

        Log.e("Hata", test)


        klasorPaths.add(kameraResimleri)
        klasorPaths.add(indirilenResimler)
        klasorPaths.add(whatsappResimleri)
        klasorPaths.add(videolar)


        klasorAdlari.add("Kamera")
        klasorAdlari.add("İndirilenler")
        klasorAdlari.add("WhatsApp")
        klasorAdlari.add("Videolar")


        var spinnerArrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, klasorAdlari)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        view.spnKlasorAdlari.adapter = spinnerArrayAdapter
        view.spnKlasorAdlari.setSelection(0)
        //ilk acıldıgında ilk  dosya gosterilir.

        view.spnKlasorAdlari.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setupRecyclerView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position)))
            }

        }

        activity!!.anaLayout.visibility = View.VISIBLE
        activity!!.fragmentContainerLayout.visibility = View.GONE

        view.tvİleriButton.setOnClickListener {
            activity!!.anaLayout.visibility = View.GONE
            activity!!.fragmentContainerLayout.visibility = View.VISIBLE
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            ////////////////////// EventBus yayın yaptık.
            EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(secilenDosyaYolu, dosyaTuruResimMi))
            //////////////////////
            transaction.addToBackStack("ShareNexteklendi")
            transaction.replace(R.id.fragmentContainerLayout, ShareNextFragment())
            transaction.commit()

            videoViewGaleri.stopPlayback()

        }
        view.imgGeri.setOnClickListener {
            activity!!.onBackPressed()

        }




        return view
    }


    fun setupRecyclerView(secilenKlasordekiDosyalar: ArrayList<String>) {

        var recyclerViewAdapter = ShareGalleryRecyclerAdapter(activity!!, secilenKlasordekiDosyalar)
        recyclerViewDosyalar.adapter = recyclerViewAdapter
        //grid view seklinde gösterim yaptık.
        var layoutManagerr = GridLayoutManager(activity!!, 3)
        recyclerViewDosyalar.layoutManager = layoutManagerr
        recyclerViewDosyalar.setHasFixedSize(true)


        if (secilenKlasordekiDosyalar.size > 0) {
            var secilenResimYolu = secilenKlasordekiDosyalar.get(0)
            resimVeyaVideoGoster(secilenResimYolu)

        } else {
            videoViewGaleri.visibility = View.INVISIBLE
            imgCropView.visibility = View.INVISIBLE

        }


    }

    fun resimVeyaVideoGoster(dosyaYolu: String) {

        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))

        if (dosyaTuru != null) {

            if (dosyaTuru.equals(".mp4")) {
                videoViewGaleri.visibility = View.VISIBLE
                imgCropView.visibility = View.INVISIBLE
                videoViewGaleri.setVideoURI(Uri.parse("file://" + dosyaYolu))
                videoViewGaleri.start()
                pbImgBuyukResim.visibility = View.GONE

                dosyaTuruResimMi = false

            } else {
                videoViewGaleri.visibility = View.INVISIBLE
                imgCropView.visibility = View.VISIBLE

                UniversalImageLoader.setImage("file:/" + dosyaYolu, imgCropView, pbImgBuyukResim)
                dosyaTuruResimMi = true
            }

        }


    }

    //////////////////////eventbuss//////////////////////////
    @Subscribe

    internal fun onClickResimEvent(secilenDosya: EventbusDataEvents.GaleriDosyaYolunuGonder) {
        secilenDosyaYolu = secilenDosya.dosyaYolu

        resimVeyaVideoGoster(secilenDosyaYolu!!)
        // Log.e("gelenResimyolu", gelenResimYolu)
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

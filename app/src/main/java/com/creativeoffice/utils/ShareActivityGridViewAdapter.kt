package com.creativeoffice.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.creativeoffice.instakotlin2.R
import kotlinx.android.synthetic.main.tek_satir_grid_resim.view.*
import java.lang.Exception
import kotlin.math.min

class ShareActivityGridViewAdapter(context: Context, resource: Int, var klasordekiDosyalar: ArrayList<out String>) : ArrayAdapter<String>(context, resource, klasordekiDosyalar) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var tek_sutun_resim = convertView // listViewda  "3" adlı inflater resim sayısı kadar cevrılıyor eger 500 resım varsa 500ude cevrıldıgı ıcın sıstemı zorlar. Bizde convertView kullanarak her
        // kaydırısta 6 veya 10 resim glemesini saglıyoruz.
        var inflater = LayoutInflater.from(context) //"3"

        if (tek_sutun_resim == null) {
            tek_sutun_resim = inflater.inflate(R.layout.tek_satir_grid_resim, parent, false)
        }

        var imgView = tek_sutun_resim!!.imgTekSutunImage
        var progressBar = tek_sutun_resim.pbTekSutunImage
        var tvSure = tek_sutun_resim.tvSure
        var imgURL = klasordekiDosyalar.get(position)


        var dosyaYolu = klasordekiDosyalar.get(position)
        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))

        if (dosyaTuru != null) {

            if (dosyaTuru.equals(".mp4")) {

                tvSure.visibility = View.VISIBLE
                var retriver = MediaMetadataRetriever()
                retriver.setDataSource(context, Uri.parse("file://" + dosyaYolu))
                var videoSuresi = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) //milisaniye cınsıdnen veriyor.
                var videoSuresiLong = videoSuresi.toLong()

                tvSure.text = convertDuration(videoSuresiLong)

                UniversalImageLoader.setImage("file:/" + klasordekiDosyalar.get(position), imgView, progressBar)


            } else {
                tvSure.visibility = View.GONE

                UniversalImageLoader.setImage("file:/" + imgURL, imgView, progressBar)
            }

        }


        return tek_sutun_resim


    }


    fun convertDuration(duration: Long): String {
        var out: String? = null
        var hours: Long = 0
        try {
            hours = duration / 3600000
            Log.e("duration", duration.toString())
            Log.e("Saat", hours.toString())

        } catch (e: Exception) {
            Log.e("Hata", e.toString())

            return out!!
        }

        val remaining_minute = (duration - hours * 3600000) / 60000
        var minutes = remaining_minute.toString()
        Log.e("Dakika", minutes)
        if (minutes.equals("0")) {
            minutes = "00"
        }


        val remainin_seconds = duration - hours * 3600000 - remaining_minute * 60000
        var seconds = remainin_seconds.toString()
        Log.e("Saniye", seconds)

        if (seconds.length < 2) {
            seconds = "00"
        } else {
            seconds = seconds.substring(0, 2)
        }


        if (hours > 0) {
            out = hours.toString() + ":" + minutes + ":" + seconds
        } else {
            out = minutes + ":" + seconds
        }
        Log.e("Sure", out)
        return out
    }

}
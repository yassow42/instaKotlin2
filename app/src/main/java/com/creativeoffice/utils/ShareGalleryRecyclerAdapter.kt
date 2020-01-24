package com.creativeoffice.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.instakotlin2.R
import kotlinx.android.synthetic.main.tek_satir_grid_resim.view.*
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class ShareGalleryRecyclerAdapter(var myContext: Context, var klasordekiDosyalar: ArrayList<String>) : RecyclerView.Adapter<ShareGalleryRecyclerAdapter.MyViewHolder>() {

    var inflater: LayoutInflater

    init {//inflater ilk olarak bruada calısır
        inflater = LayoutInflater.from(myContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //inflate edip xml cagırır.
        var tekSutunDosya = inflater.inflate(R.layout.tek_satir_grid_resim, parent, false)

        return MyViewHolder(tekSutunDosya)
    }

    override fun getItemCount(): Int {
        //itemin sayısını
        return klasordekiDosyalar.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        var dosyaYolu = klasordekiDosyalar.get(position)
        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))

        if (dosyaTuru != null) {

            if (dosyaTuru.equals(".mp4")) {

                holder.videoSure.visibility = View.VISIBLE
                var retriver = MediaMetadataRetriever()
                retriver.setDataSource(myContext, Uri.parse("file://" + dosyaYolu))
                var videoSuresi = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) //milisaniye cınsıdnen veriyor.
                var videoSuresiLong = videoSuresi.toLong()

                holder.videoSure.text = convertDuration(videoSuresiLong)
                UniversalImageLoader.setImage("file:/" + klasordekiDosyalar.get(position), holder.imgTekSutun, holder.progressBar)
            } else {
                holder.videoSure.visibility = View.GONE
                UniversalImageLoader.setImage("file:/" + dosyaYolu, holder.imgTekSutun, holder.progressBar)
            }
        }

        holder.imgTekSutun.setOnClickListener {
            EventBus.getDefault().post(EventbusDataEvents.GaleriDosyaYolunuGonder(dosyaYolu))
        }



    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgTekSutun = itemView.imgTekSutunImage
        var videoSure = itemView.tvSure
        var progressBar = itemView.pbTekSutunImage

    }

    fun convertDuration(duration: Long): String {
        var out: String? = null
        var hours: Long = 0
        try {
            hours = duration / 3600000


        } catch (e: Exception) {


            return out!!
        }

        val remaining_minute = (duration - hours * 3600000) / 60000
        var minutes = remaining_minute.toString()

        if (minutes.equals("0")) {
            minutes = "00"
        }


        val remainin_seconds = duration - hours * 3600000 - remaining_minute * 60000
        var seconds = remainin_seconds.toString()
       // Log.e("Saniye", seconds)

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
       // Log.e("Sure", out)
        return out
    }
}
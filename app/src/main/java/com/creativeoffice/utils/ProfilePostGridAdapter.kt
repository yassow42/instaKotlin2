package com.creativeoffice.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.Models.UserPost
import com.creativeoffice.instakotlin2.R
import kotlinx.android.synthetic.main.tek_satir_grid_resim_profile.view.*


class ProfilePostGridAdapter(var myContext: Context, var kullaniciPostlari: ArrayList<UserPost>) : RecyclerView.Adapter<ProfilePostGridAdapter.MyViewHolder>() {

    var inflater: LayoutInflater

    init {//inflater ilk olarak bruada calısır
        inflater = LayoutInflater.from(myContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //inflate edip xml cagırır.
        var tekSutunDosya = inflater.inflate(R.layout.tek_satir_grid_resim_profile, parent, false)

        return MyViewHolder(tekSutunDosya)
    }

    override fun getItemCount(): Int {
        //itemin sayısını
        return kullaniciPostlari.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        var dosyaYolu = kullaniciPostlari.get(position).postUrl
        var noktaninGectigiIndex= dosyaYolu!!.lastIndexOf(".")
        var dosyaTuru = dosyaYolu!!.substring(noktaninGectigiIndex, noktaninGectigiIndex+4)
        Log.e("dosyaturu",dosyaTuru)
        holder.videoIcon.visibility = View.GONE

        if (dosyaTuru.equals(".mp4")) {
            holder.videoIcon.visibility = View.VISIBLE
            var thumbnailFotosu = videodanThumbnailOlustur(dosyaYolu)

            holder.dosyaResim.setImageBitmap(thumbnailFotosu)
            holder.progressBar.visibility = View.GONE
            Log.e("dosyaturu1",dosyaTuru)
        }else{
            Log.e("dosyaturu2",dosyaTuru)
            holder.videoIcon.visibility=View.GONE
            holder.progressBar.visibility= View.VISIBLE
            UniversalImageLoader.setImage(dosyaYolu, holder.dosyaResim, holder.progressBar)


        }

    }


    @Throws(Throwable::class)
    fun videodanThumbnailOlustur(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 22) mediaMetadataRetriever.setDataSource(videoPath, HashMap()) else mediaMetadataRetriever.setDataSource(videoPath)
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable(
                "Exception in retriveVideoFrameFromVideo(String videoPath)"
                        + e.message
            )
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var dosyaResim = itemView.imgTekSutunImage
        var videoIcon = itemView.imgVideoİcon
        var progressBar = itemView.pbTekSutunImage

    }

}
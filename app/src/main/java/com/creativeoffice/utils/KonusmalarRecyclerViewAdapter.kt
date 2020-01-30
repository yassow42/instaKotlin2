package com.creativeoffice.utils

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.Models.Konusmalar
import com.creativeoffice.instakotlin2.R

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tek_satir_konusma_item.view.*

class KonusmalarRecyclerViewAdapter(var myContext: Context, var tumKonusmalar: ArrayList<Konusmalar>) : RecyclerView.Adapter<KonusmalarRecyclerViewAdapter.MyKonusmaHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyKonusmaHolder {
        var myView = LayoutInflater.from(myContext).inflate(R.layout.tek_satir_konusma_item, parent, false)


        return MyKonusmaHolder(myView)
    }


    override fun onBindViewHolder(holder: MyKonusmaHolder, position: Int) {
       holder.setData(tumKonusmalar.get(position),holder)



    }


    override fun getItemCount(): Int {

        return tumKonusmalar.size
    }

    class MyKonusmaHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {


        var tumLayout = itemView as ConstraintLayout
        var sohbetEdilenProfilePic = tumLayout.imgUserProfilePicture
        var sohbetEdilenUserName = tumLayout.tvUserName
        var enSonMesaj = tumLayout.tvSonMesaj
        var zaman = tumLayout.tvMesajZaman


        fun setData(oankiKonusma: Konusmalar, holder: MyKonusmaHolder) {

           enSonMesaj.text = oankiKonusma.son_mesaj.toString()


           zaman.text = TimeAgo.getTimeAgoComments(oankiKonusma.time!!.toLong())



            FirebaseDatabase.getInstance().reference.child("users").child(oankiKonusma.user_id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    holder.sohbetEdilenUserName.text =  p0.child("user_name").value.toString()

                    var imgUrl = p0.child("user_detail").child("profile_picture").value.toString()

                    UniversalImageLoader.setImage(imgUrl,holder.sohbetEdilenProfilePic,null)

                }

            })


        }

    }






}
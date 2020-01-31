package com.creativeoffice.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.graphics.Typeface

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.Home.ChatActivity
import com.creativeoffice.Models.Konusmalar
import com.creativeoffice.instakotlin2.R
import com.google.firebase.auth.FirebaseAuth

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
        holder.setData(tumKonusmalar.get(position), holder, myContext)


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
        var okunduBilgisi = tumLayout.imgOkunduBilgisi


        fun setData(oankiKonusma: Konusmalar, holder: MyKonusmaHolder, myContext: Context) {

            enSonMesaj.text = oankiKonusma.son_mesaj.toString()


            zaman.text = TimeAgo.getTimeAgoComments(oankiKonusma.time!!.toLong())
            if (oankiKonusma.goruldu == false) {
                okunduBilgisi.visibility = View.VISIBLE
                sohbetEdilenUserName.setTypeface(null, Typeface.BOLD)
                enSonMesaj.setTypeface(null, Typeface.BOLD)


            } else {
                okunduBilgisi.visibility = View.INVISIBLE
                sohbetEdilenUserName.setTypeface(null, Typeface.NORMAL)
            }


            tumLayout.setOnClickListener {

                val intent = Intent(myContext, ChatActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK).addFlags(FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("secilenUserID", oankiKonusma.user_id.toString())



                FirebaseDatabase.getInstance().reference.child("konusmalar")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(oankiKonusma.user_id.toString())
                    .child("goruldu").setValue(true).addOnCompleteListener {
                        myContext.startActivity(intent)
                    }
            }


            FirebaseDatabase.getInstance().reference.child("users").child(oankiKonusma.user_id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    holder.sohbetEdilenUserName.text = p0.child("user_name").value.toString()

                    var imgUrl = p0.child("user_detail").child("profile_picture").value.toString()

                    UniversalImageLoader.setImage(imgUrl, holder.sohbetEdilenProfilePic, null)

                }

            })


        }

    }


}
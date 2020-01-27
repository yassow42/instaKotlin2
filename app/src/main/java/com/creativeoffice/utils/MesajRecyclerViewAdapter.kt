package com.creativeoffice.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.Models.Mesaj
import com.creativeoffice.instakotlin2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tek_satir_mesaj_gonderen.view.*

class MesajRecyclerViewAdapter(var myContext: Context, var tumMesajlar: ArrayList<Mesaj>) : RecyclerView.Adapter<MesajRecyclerViewAdapter.MyMesajViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMesajViewHolder {

        var myView: View? = null

        if (viewType == 1) {
            myView = LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_gonderen, parent, false)
            return MyMesajViewHolder(myView)
        } else {
            myView = LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_alan, parent, false)

            return MyMesajViewHolder(myView)
        }






    }

    override fun getItemCount(): Int {
        return tumMesajlar.size
    }

    override fun onBindViewHolder(holder: MyMesajViewHolder, position: Int) {

        holder.setData(tumMesajlar.get(position))

    }

    override fun getItemViewType(position: Int): Int {
        if (tumMesajlar.get(position).user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
            return 1
        } else{
            return 2
        }

    }


    class MyMesajViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tumLayout = itemView as ConstraintLayout
        var mesajText = tumLayout.tvMesaj
        var profilePic = tumLayout.mesajGonderenProfilePic


        fun setData(oankiMesaj: Mesaj) {

            mesajText.text = oankiMesaj.mesaj

            FirebaseDatabase.getInstance().reference.child("users").child(oankiMesaj.user_id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var imgUrl = p0.child("user_detail").child("profile_picture").value.toString()
                    UniversalImageLoader.setImage(imgUrl,profilePic,null)
                }

            })


        }
    }
}
package com.creativeoffice.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.Models.Mesaj
import com.creativeoffice.instakotlin2.R

class MesajRecyclerViewAdapter(var myContext: Context, var tumMesajlar: ArrayList<Mesaj>) : RecyclerView.Adapter<MesajRecyclerViewAdapter.MyMesajViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesajRecyclerViewAdapter.MyMesajViewHolder {
        var view = LayoutInflater.from(myContext).inflate(R.layout.tek_satir_mesaj_gonderen, parent, false)


        return MyMesajViewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyMesajViewHolder, position: Int) {

    }


    class MyMesajViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {


    }
}
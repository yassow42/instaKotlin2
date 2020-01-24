package com.creativeoffice.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.Models.Posts
import com.creativeoffice.instakotlin2.R
import kotlinx.android.synthetic.main.tek_search_recycler_item.view.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class SearchActivityRecyclerAdapter(var myContext: Context, var tumGonderiler: ArrayList<Posts>) : RecyclerView.Adapter<SearchActivityRecyclerAdapter.MyViewHolder>() {


    override fun getItemCount(): Int {
        return tumGonderiler.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var viewHolder = LayoutInflater.from(myContext).inflate(R.layout.tek_search_recycler_item, parent, false)
        return MyViewHolder(viewHolder, myContext)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(position, tumGonderiler.get(position), myContext)

    }

    class MyViewHolder(itemView: View?, myContext: Context) : RecyclerView.ViewHolder(itemView!!) {


        var tumLayout = itemView as ConstraintLayout

        var img = tumLayout.searchTekImg


        fun setData(position: Int, oankiGonderi: Posts, myContext: Context) {

            UniversalImageLoader.setImage(oankiGonderi.file_url!!, img, null)

        }
    }
}

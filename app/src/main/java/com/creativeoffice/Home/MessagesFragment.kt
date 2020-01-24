package com.creativeoffice.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.creativeoffice.instakotlin2.R

class MessagesFragment:Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { //saveInstance ise uygulama yan dondugunde hersey sıl bastan yapılır bunu engeller verileri korur. ınflater java kodlarını xml e cevırır.
        var view = inflater.inflate(R.layout.fragment_messages,container,false) //biz fragmenti nereye koyarsak container orasıdır.



        return view
    }
}
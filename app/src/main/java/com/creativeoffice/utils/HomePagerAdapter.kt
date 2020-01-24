package com.creativeoffice.utils

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class HomePagerAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm) { //fm uygulamada kı butun fragmentlerı yoneten sınıftır.


    private var mFragmentList: ArrayList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment { // ılgılı ogeyı bulmak ıcın kulandıgmız yer

        return mFragmentList.get(position)
    }

    override fun getCount(): Int { // kac tane fragment oldugunu

        return mFragmentList.size
    }


    // kişisel fonksiyon tanımladık. fragment listemize gelen fragmentı ekleyecegız.
    fun addFragment (fragment: Fragment){

        mFragmentList.add(fragment)
    }


    fun secilenFragmentiViewPagerdanSil(viewGroup: ViewGroup, position: Int) {

        var silinecekFragment = this.instantiateItem(viewGroup, position)
        this.destroyItem(viewGroup, position, silinecekFragment)
    }

    fun secilenFragmentıViewPageraEkle(viewGroup: ViewGroup, position: Int) {

        this.instantiateItem(viewGroup, position)
    }

}
package com.creativeoffice.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.Models.Konusmalar
import com.creativeoffice.Models.Users
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.KonusmalarRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hendraanggrian.widget.Mention
import com.hendraanggrian.widget.MentionAdapter
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.android.synthetic.main.fragment_messages.view.*
import kotlinx.android.synthetic.main.fragment_messages.view.searchViewMesaj

class MessagesFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var myRecyclerView: RecyclerView
    lateinit var myLinearLayoutManager: LinearLayoutManager
    lateinit var myAdapter: KonusmalarRecyclerViewAdapter
    lateinit var myFragmentView: View
    lateinit var mRef: DatabaseReference


    var tumKonusmalar: ArrayList<Konusmalar> = ArrayList<Konusmalar>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { //saveInstance ise uygulama yan dondugunde hersey sıl bastan yapılır bunu engeller verileri korur. ınflater java kodlarını xml e cevırır.

        myFragmentView = inflater.inflate(R.layout.fragment_messages, container, false) //biz fragmenti nereye koyarsak container orasıdır.
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!

        mRef = FirebaseDatabase.getInstance().reference

        var mymentionAdapter = MentionAdapter(activity!!, R.drawable.ic_profile_logo)
        myFragmentView.searchViewMesaj.setMentionTextChangedListener { vieww, s ->

            FirebaseDatabase.getInstance().reference.child("users").orderByChild("user_name").startAt(s).endAt(s + "\uf8ff")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.value != null) {
                            for (user in p0.children) {
                                mymentionAdapter.clear()
                                var okunanKullanici = user.getValue(Users::class.java)
                                var username = okunanKullanici!!.user_name.toString()
                                var adiSoyadi = okunanKullanici.adi_soyadi.toString()
                                var photo = okunanKullanici.user_detail!!.profile_picture
                                if (!photo.isNullOrEmpty()) {
                                    mymentionAdapter.add(Mention(username, adiSoyadi, photo))

                                } else {
                                    mymentionAdapter.add(Mention(username, adiSoyadi, R.drawable.ic_profile_logo))

                                }
                            }


                        }
                    }

                })
        }

        myFragmentView.searchViewMesaj.mentionAdapter = mymentionAdapter

        myFragmentView.btnGit.setOnClickListener {

            if (searchViewMesaj.text.isNotEmpty()) {
                FirebaseDatabase.getInstance().reference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for (users in p0.children) {
                            var kullanicilar = users.getValue(Users::class.java)!!

                            var gelenKullaniciAdi = searchViewMesaj.text.toString().trim() //trim boslukları siler.
                            if (gelenKullaniciAdi.substring(0, 1) == "@") {
                                gelenKullaniciAdi = gelenKullaniciAdi.substring(1)
                            }

                            if (gelenKullaniciAdi.equals(kullanicilar.user_name.toString())) {


                                if (!kullanicilar.user_id.toString().equals(mAuth.currentUser!!.uid)) {
                                    var intent = Intent(activity, ChatActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    intent.putExtra("secilenUserID", kullanicilar.user_id.toString())
                                    startActivity(intent)
                                }
                            }
                        }
                    }

                })


            }

        }


        setupKonusmalarRecyclerView()


        return myFragmentView
    }

    private fun TumKonusmalariGetir() {

        mRef.child("konusmalar").child(mUser.uid).orderByChild("time").addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                var guncellenecekKonusma = p0.getValue(Konusmalar::class.java)!!
                guncellenecekKonusma.user_id = p0.key.toString()


            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var eklenecekKonusma = p0.getValue(Konusmalar::class.java)!!

                eklenecekKonusma.user_id = p0.key.toString()

                tumKonusmalar.add(0,eklenecekKonusma)

                myAdapter.notifyItemInserted(tumKonusmalar.size - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })



    }

    private var myListener: ChildEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            var eklenecekKonusma = p0.getValue(Konusmalar::class.java)!!

            eklenecekKonusma.user_id = p0.key.toString()

            tumKonusmalar.add(eklenecekKonusma)

            myAdapter.notifyItemInserted(tumKonusmalar.size - 1)
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }
    }


    private fun setupKonusmalarRecyclerView() {

        myRecyclerView = myFragmentView.recyclerKonusmalar

        myLinearLayoutManager = LinearLayoutManager(context!!.applicationContext, LinearLayoutManager.VERTICAL, false)

        myAdapter = KonusmalarRecyclerViewAdapter(context!!.applicationContext, tumKonusmalar)

        myRecyclerView.layoutManager = myLinearLayoutManager
        myRecyclerView.adapter = myAdapter

        TumKonusmalariGetir()
    }
}
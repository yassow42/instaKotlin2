package com.creativeoffice.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.creativeoffice.Models.Users
import com.creativeoffice.instakotlin2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hendraanggrian.widget.Mention
import com.hendraanggrian.widget.MentionAdapter
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.android.synthetic.main.fragment_messages.view.*
import kotlinx.android.synthetic.main.fragment_messages.view.searchViewMesaj

class MessagesFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { //saveInstance ise uygulama yan dondugunde hersey sıl bastan yapılır bunu engeller verileri korur. ınflater java kodlarını xml e cevırır.

        var view = inflater.inflate(R.layout.fragment_messages, container, false) //biz fragmenti nereye koyarsak container orasıdır.
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!

        var mymentionAdapter = MentionAdapter(activity!!, R.drawable.ic_profile_logo)
        view.searchViewMesaj.setMentionTextChangedListener { vieww, s ->

            FirebaseDatabase.getInstance().reference.child("users").orderByChild("user_name").startAt(s).endAt(s + "\uf8ff")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.value != null) {
                            for (user in p0.children) {
                                mymentionAdapter.clear()
                                var okunanKullanici = user.getValue(Users::class.java)
                                var username = okunanKullanici!!.user_name.toString()
                                var adiSoyadi = okunanKullanici!!.adi_soyadi.toString()
                                var photo = okunanKullanici!!.user_detail!!.profile_picture
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

        view.searchViewMesaj.mentionAdapter = mymentionAdapter

        view.btnGit.setOnClickListener {

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
                                Log.e("kişi uid3", kullanicilar.user_id.toString())

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

        return view
    }
}
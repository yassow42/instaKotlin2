package com.creativeoffice.Home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.Models.Mesaj
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.MesajRecyclerViewAdapter
import com.creativeoffice.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    var sohbetEdilecekUserID = ""
    var mesajGonderenUserID = ""
    var tumMesajlar: ArrayList<Mesaj> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        mUser = mAuth.currentUser!!
        mesajGonderenUserID = mUser.uid


        var gelenVeri = intent.extras
        sohbetEdilecekUserID = gelenVeri!!.getString("secilenUserID")!!
        sohbetEdilenKisiyiBul(sohbetEdilecekUserID)


        MesajlariGetir()
        tvMesajGonderBtn.setOnClickListener {

            var mesajText = etMesaj.text.toString()
            var mesajAtan = HashMap<String, Any>()
            mesajAtan.put("mesaj", mesajText)
            mesajAtan.put("goruldu", true)
            mesajAtan.put("time", ServerValue.TIMESTAMP)
            mesajAtan.put("type", "text")
            mesajAtan.put("user_id", mesajGonderenUserID)

            mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).push().setValue(mesajAtan)


            var mesajAlan = HashMap<String, Any>()
            mesajAlan.put("mesaj", mesajText)
            mesajAlan.put("goruldu", false)
            mesajAlan.put("time", ServerValue.TIMESTAMP)
            mesajAlan.put("type", "text")
            mesajAlan.put("user_id", mesajGonderenUserID)



            mRef.child("mesajlar").child(sohbetEdilecekUserID).child(mesajGonderenUserID).push().setValue(mesajAlan)

            var konusmaMesajAtan = HashMap<String, Any>()
            konusmaMesajAtan.put("time", ServerValue.TIMESTAMP)
            konusmaMesajAtan.put("goruldu", true)
            konusmaMesajAtan.put("son_mesaj", mesajText)
            mRef.child("konusmalar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).setValue(konusmaMesajAtan)

            var konusmaMesajAlan = HashMap<String, Any>()
            konusmaMesajAlan.put("time", ServerValue.TIMESTAMP)
            konusmaMesajAlan.put("goruldu", false)
            konusmaMesajAlan.put("son_mesaj", mesajText)

            mRef.child("konusmalar").child(sohbetEdilecekUserID).child(mesajGonderenUserID).setValue(konusmaMesajAlan)

            etMesaj.setText("")
        }
    }

    private fun MesajlariGetir() {

        tumMesajlar.clear()
        mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.value != null) {
                    for (mesaj in p0.children) {
                        var okunanMesaj = mesaj.getValue(Mesaj::class.java)
                        tumMesajlar.add(okunanMesaj!!)
                    }

                    setupMesajlarRecyclerView()

                }

            }
        })


    }

    private fun setupMesajlarRecyclerView() {
        var myLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        myLinearLayoutManager.stackFromEnd = true
        var myRecyclerView = rvSohbet
        myRecyclerView.layoutManager = myLinearLayoutManager

        var myAdapter = MesajRecyclerViewAdapter(this, tumMesajlar)

        myRecyclerView.adapter = myAdapter


    }

    private fun sohbetEdilenKisiyiBul(sohbetEdilecekUserID: String) {
        mRef.child("users").child(sohbetEdilecekUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var sohbetEdilecekUserName = p0.child("user_name").value.toString()
                tvSohbetEdilecekKullanici.text = sohbetEdilecekUserName
                var sohbetEdilecekprofilImg = p0.child("user_detail").child("profile_picture").value.toString()
                UniversalImageLoader.setImage(sohbetEdilecekprofilImg, mesajTitleCircleimg, null)


            }
        })

        mRef.child("users").child(mesajGonderenUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                var sohbetSahibiProfil = p0.child("user_detail").child("profile_picture").value.toString()
                UniversalImageLoader.setImage(sohbetSahibiProfil, mesajCircleimg, mesajCirclePB)
            }
        })

    }

}

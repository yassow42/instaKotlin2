package com.creativeoffice.Home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    lateinit var myRecyclerViewAdapter:MesajRecyclerViewAdapter
   lateinit var myRecyclerView:RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        mUser = mAuth.currentUser!!
        mesajGonderenUserID = mUser.uid
        myRecyclerView = rvSohbet


        var gelenVeri = intent.extras
        sohbetEdilecekUserID = gelenVeri!!.getString("secilenUserID")!!
        sohbetEdilenKisiyiBul(sohbetEdilecekUserID)

        setupMesajlarRecyclerView() // bunu burda cagırdık cunku kısıyı bulduktan sonra recyclerViewi olustursun
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

/*
       mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.value != null) {
                    for (mesaj in p0.children) {

                    }

                    setupMesajlarRecyclerView()

                }

            }
        })

*/
        mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var okunanMesaj = p0.getValue(Mesaj::class.java)
                tumMesajlar.add(okunanMesaj!!)

                myRecyclerViewAdapter.notifyItemInserted(tumMesajlar.size-1)
                myRecyclerView.scrollToPosition(tumMesajlar.size-1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })


    }

    private fun setupMesajlarRecyclerView() {
        var myLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        myLinearLayoutManager.stackFromEnd = true
        myRecyclerView = rvSohbet
        myRecyclerView.layoutManager = myLinearLayoutManager

        myRecyclerViewAdapter = MesajRecyclerViewAdapter(this, tumMesajlar)

        myRecyclerView.adapter = myRecyclerViewAdapter


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

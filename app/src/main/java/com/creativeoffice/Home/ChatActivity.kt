package com.creativeoffice.Home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.UniversalImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    var sohbetEdilecekUserID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var gelenVeri = intent.extras
        sohbetEdilecekUserID = gelenVeri!!.getString("secilenUserID")!!

        FirebaseDatabase.getInstance().reference.child("users").child(sohbetEdilecekUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var sohbetEdilecekUserName = p0.child("user_name").value.toString()
                tvSohbetEdilecekKullanici.text = sohbetEdilecekUserName
                var profilImg = p0.child("user_detail").child("profile_picture").value.toString()
                UniversalImageLoader.setImage(profilImg,mesajCircleimg,mesajCirclePB)


            }

        })


    }



}

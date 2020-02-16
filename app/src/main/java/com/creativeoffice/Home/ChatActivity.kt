package com.creativeoffice.Home


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.Models.Mesaj
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.MesajRecyclerViewAdapter
import com.creativeoffice.utils.UniversalImageLoader
import com.dinuscxj.refresh.RecyclerRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    var sohbetEdilecekUserId = ""
    var mesajGonderenUserId = ""
    var konusmaBilgisiGuncellenDimi = false

    var tumMesajlar: ArrayList<Mesaj> = ArrayList()
    lateinit var myRecyclerViewAdapter: MesajRecyclerViewAdapter
    lateinit var myRecyclerView: RecyclerView

    //sayfalama icin
    val SAYFA_BASI_GONDERI_SAYISI = 3
    // var sayfaNumarasi = 1

    var mesajPos = 0
    var dahaFazlaMesajPos = 0
    var ilkGetirilenMesajID = ""
    var zatenListedeOlanMesajID = ""

    lateinit var childEventListener: ChildEventListener
    lateinit var childListenerDahaFazla: ChildEventListener
    lateinit var yaziyorEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        mUser = mAuth.currentUser!!
        mesajGonderenUserId = mUser.uid
        myRecyclerView = rvSohbet


        var gelenVeri = intent.extras
        sohbetEdilecekUserId = gelenVeri!!.getString("secilenUserID")!!
        sohbetEdilenKisiyiBul(sohbetEdilecekUserId)

        setupMesajlarRecyclerView() //  recyclerViewi olustursun
        mesajlariGetir()
        refreshLayout.setOnRefreshListener(object : RecyclerRefreshLayout.OnRefreshListener {
            override fun onRefresh() {


                mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {


                        if (p0.childrenCount.toInt() != tumMesajlar.size) {
                            dahaFazlaMesajPos = 0


                            dahaFazlaMesajGetir()
                        } else {

                            refreshLayout.setRefreshing(false)
                            refreshLayout.setEnabled(false)
                        }
                    }


                })


            }

        })

        tvMesajGonderBtn.setOnClickListener {

            var mesajText = etMesaj.text.toString()

            if (!TextUtils.isEmpty(mesajText)) {
                var mesajAtan = HashMap<String, Any>()
                mesajAtan.put("mesaj", mesajText)
                mesajAtan.put("goruldu", true)
                mesajAtan.put("time", ServerValue.TIMESTAMP)
                mesajAtan.put("type", "text")
                mesajAtan.put("user_id", mesajGonderenUserId)


                var yeniMesajKey = mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).push().key
                mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).child(yeniMesajKey.toString()).setValue(mesajAtan)


                var mesajAlan = HashMap<String, Any>()
                mesajAlan.put("mesaj", mesajText)
                mesajAlan.put("goruldu", false)
                mesajAlan.put("time", ServerValue.TIMESTAMP)
                mesajAlan.put("type", "text")
                mesajAlan.put("user_id", mesajGonderenUserId)



                mRef.child("mesajlar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).child(yeniMesajKey.toString()).setValue(mesajAlan)

                var konusmaMesajAtan = HashMap<String, Any>()
                konusmaMesajAtan.put("time", ServerValue.TIMESTAMP)
                konusmaMesajAtan.put("goruldu", true)
                konusmaMesajAtan.put("son_mesaj", mesajText)
                konusmaMesajAtan.put("typing", false)

                mRef.child("konusmalar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).setValue(konusmaMesajAtan)
                var konusmaMesajAlan = HashMap<String, Any>()
                konusmaMesajAlan.put("time", ServerValue.TIMESTAMP)
                konusmaMesajAlan.put("goruldu", false)
                konusmaMesajAlan.put("son_mesaj", mesajText)
                konusmaMesajAlan.put("typing", false)

                mRef.child("konusmalar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).setValue(konusmaMesajAlan)

                etMesaj.setText("")

            }

        }

        etMesaj.addTextChangedListener(object : TextWatcher {
            var typing = false
            override fun afterTextChanged(p0: Editable?) {
                if (!TextUtils.isEmpty(p0.toString()) && p0!!.toString().trim().length == 1) {
                    typing = true
                    FirebaseDatabase.getInstance().reference.child("konusmalar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).child("typing").setValue(true)

                } else if (typing && p0!!.toString().toString().trim().length == 0) {
                    typing = false
                    FirebaseDatabase.getInstance().reference.child("konusmalar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).child("typing").setValue(false)
                    Log.e("kontrol", "Kullanıcı yazmaya bıraktı.")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

        })

      yaziyorEventListener=  FirebaseDatabase.getInstance().reference
          .child("konusmalar").child(sohbetEdilecekUserId).child(mesajGonderenUserId).child("typing")
          .addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.value==true){

                   yaziyorCons.visibility = View.VISIBLE
                   FirebaseDatabase.getInstance().reference.child("users").child(sohbetEdilecekUserId).child("user_detail").child("profile_picture")
                       .addListenerForSingleValueEvent(object :ValueEventListener{
                           override fun onCancelled(p0: DatabaseError) {

                           }

                           override fun onDataChange(p0: DataSnapshot) {
                               var imgUrl = p0.value.toString()
                               UniversalImageLoader.setImage(imgUrl,yaziyorCircleimg,null)
                           }

                       })


               }else if (p0.value==false){
                   yaziyorCons.visibility = View.GONE

               }
            }

        })

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun dahaFazlaMesajGetir() {


        childListenerDahaFazla = mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId)
            .orderByKey().endAt(ilkGetirilenMesajID).limitToLast(SAYFA_BASI_GONDERI_SAYISI)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                    //Log.e("KONTROL","p0 kaç tane veri geldi "+p0!!.childrenCount+" pos:"+dahaFazlaMesajPos)
                    var okunanMesaj = p0.getValue(Mesaj::class.java)

                    if (!zatenListedeOlanMesajID.equals(p0.key)) {
                        tumMesajlar.add(dahaFazlaMesajPos++, okunanMesaj!!)
                        myRecyclerViewAdapter.notifyItemInserted(dahaFazlaMesajPos - 1)
                    } else {

                        zatenListedeOlanMesajID = ilkGetirilenMesajID

                    }

                    if (dahaFazlaMesajPos == 1) {

                        ilkGetirilenMesajID = p0.key!!

                    }


                    //Log.e("KONTROL","ZATEN LİSTEDEKI ID:"+zatenListedeOlanMesajID+" ILK GETIRILIEN ID:"+ilkGetirilenMesajID+" mesaj id:"+p0!!.key)


                    myRecyclerView.scrollToPosition(0)

                    refreshLayout.setRefreshing(false)

                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

            })


    }

    private fun mesajlariGetir() {

        tumMesajlar.clear()

        childEventListener = mRef.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).limitToLast(SAYFA_BASI_GONDERI_SAYISI).addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                var okunanMesaj = p0.getValue(Mesaj::class.java)
                tumMesajlar.add(okunanMesaj!!)

                if (mesajPos == 0) {

                    ilkGetirilenMesajID = p0.key!!
                    zatenListedeOlanMesajID = p0.key!!

                }
                mesajPos++

                mesajGorulduBilgisiniGuncelle(p0.key.toString())

                myRecyclerViewAdapter.notifyItemInserted(tumMesajlar.size - 1)
                myRecyclerView.scrollToPosition(tumMesajlar.size - 1)

                //Log.e("KONTROL","İLK OKUNAN MESAJ ID :"+ilkGetirilenMesajID)


            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })


    }

    fun mesajGorulduBilgisiniGuncelle(mesajID: String) {
        FirebaseDatabase.getInstance().reference.child("mesajlar")
            .child(mesajGonderenUserId)
            .child(sohbetEdilecekUserId).child(mesajID)
            .child("goruldu").setValue(true)
            .addOnCompleteListener {
                FirebaseDatabase.getInstance().reference.child("konusmalar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).child("goruldu").setValue(true)
            }
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

        mRef.child("users").child(mesajGonderenUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                var sohbetSahibiProfil = p0.child("user_detail").child("profile_picture").value.toString()
                UniversalImageLoader.setImage(sohbetSahibiProfil, mesajCircleimg, mesajCirclePB)
            }
        })

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {

        FirebaseDatabase.getInstance().reference.child("mesajlar").child(mesajGonderenUserId).child(sohbetEdilecekUserId).removeEventListener(childEventListener)
        super.onStop()
    }

}


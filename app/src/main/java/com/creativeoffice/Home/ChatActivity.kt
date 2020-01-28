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
import com.dinuscxj.refresh.RecyclerRefreshLayout
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
    lateinit var myRecyclerViewAdapter: MesajRecyclerViewAdapter
    lateinit var myRecyclerView: RecyclerView

    //sayfalama icin
    val sayfaBasiGonderiSayisi = 5
    var sayfaNumarasi = 1

    var mesajPosition = 0
    var dahaFazlaMesajPosition = 0
    var ilkGetirilenMesajID = ""
    lateinit var childEventListener: ChildEventListener
    lateinit var childListenerDahaFazla: ChildEventListener

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

        setupMesajlarRecyclerView() //  recyclerViewi olustursun
        MesajlariGetir()
        refreshLayout.setOnRefreshListener(object : RecyclerRefreshLayout.OnRefreshListener {

            override fun onRefresh() {
                sayfaNumarasi++
                dahaFazlaMesajPosition=0
               // mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).removeEventListener(childEventListener)
                dahaFazlaMesajGetir()



                refreshLayout.setRefreshing(false)
            }

        })

        tvMesajGonderBtn.setOnClickListener {

            var mesajText = etMesaj.text.toString()
            var mesajAtan = HashMap<String, Any>()
            mesajAtan.put("mesaj", mesajText)
            mesajAtan.put("goruldu", true)
            mesajAtan.put("time", ServerValue.TIMESTAMP)
            mesajAtan.put("type", "text")
            mesajAtan.put("user_id", mesajGonderenUserID)


            var yeniMesajKey = mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).push().key
            mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).child(yeniMesajKey.toString()).setValue(mesajAtan)


            var mesajAlan = HashMap<String, Any>()
            mesajAlan.put("mesaj", mesajText)
            mesajAlan.put("goruldu", false)
            mesajAlan.put("time", ServerValue.TIMESTAMP)
            mesajAlan.put("type", "text")
            mesajAlan.put("user_id", mesajGonderenUserID)



            mRef.child("mesajlar").child(sohbetEdilecekUserID).child(mesajGonderenUserID).child(yeniMesajKey.toString()).setValue(mesajAlan)

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

    private fun dahaFazlaMesajGetir() {
        //ilk getirilen ID aslında son gelecek olan IDdır. endAt ile sen getir butun mesajları ama ılk getirilene kdr getır yanı sondan 5. ye kadar. sonra onun ustunu getırıcz sureklı.
        childListenerDahaFazla = mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).orderByKey().endAt(ilkGetirilenMesajID)
            .limitToLast(sayfaBasiGonderiSayisi).addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    var okunanMesaj = p0.getValue(Mesaj::class.java)

                    if (dahaFazlaMesajPosition == 0) {
                        ilkGetirilenMesajID = p0.key.toString() // burada limittolast ile sondan 5. mesajın keyını aldık yukarıda o keyden oncekı 5 mesajı ısteyecegız.
                    }

                    tumMesajlar.add(dahaFazlaMesajPosition++,okunanMesaj!!)


                    Log.e("kontrol", ilkGetirilenMesajID + " " + okunanMesaj.mesaj)


                    myRecyclerViewAdapter.notifyDataSetChanged()
                    myRecyclerView.scrollToPosition(sayfaBasiGonderiSayisi)
                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

            })

    }

    private fun MesajlariGetir() {

        tumMesajlar.clear()


        childEventListener =
            mRef.child("mesajlar").child(mesajGonderenUserID).child(sohbetEdilecekUserID).limitToLast(sayfaNumarasi * sayfaBasiGonderiSayisi).addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    var okunanMesaj = p0.getValue(Mesaj::class.java)
                    tumMesajlar.add(okunanMesaj!!)


                    if (mesajPosition == 0) {
                        ilkGetirilenMesajID = p0.key.toString() // burada limittolast ile sondan 5. mesajın keyını aldık yukarıda o keyden oncekı 5 mesajı ısteyecegız.
                    }
                    mesajPosition++
                    Log.e("kontrol", ilkGetirilenMesajID + " " + okunanMesaj.mesaj)


                    myRecyclerViewAdapter.notifyDataSetChanged()
                    myRecyclerView.scrollToPosition(tumMesajlar.size - 1)
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

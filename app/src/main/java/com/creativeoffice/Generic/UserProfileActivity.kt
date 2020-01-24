package com.creativeoffice.Generic


import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.Models.Posts
import com.creativeoffice.Models.UserPost
import com.creativeoffice.Models.Users
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search.bottomNavigationView
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.greenrobot.eventbus.EventBus

class UserProfileActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 4

    lateinit var secilenUserID: String
    lateinit var tumGonderiler: ArrayList<UserPost>
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setupNavigationView()

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference

        tumGonderiler = ArrayList()
        secilenUserID = intent.getStringExtra("arananKullaniciID")!!
        kullaniciBilgileriniGetir(secilenUserID)
        kullaniciPostlariniGetir(secilenUserID)
        setupButton()
        //ilk secilen her zaman imgGrid oldugundan rengi ilk basta mavı yaptık
        imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
        imgGrid.setOnClickListener {

            imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this, R.color.siyah), PorterDuff.Mode.SRC_IN)
            setupRecyclerView(1)
        }
        imgList.setOnClickListener {
            imgList.setColorFilter(ContextCompat.getColor(this, R.color.mavi), PorterDuff.Mode.SRC_IN)
            imgGrid.setColorFilter(ContextCompat.getColor(this, R.color.siyah), PorterDuff.Mode.SRC_IN)
            setupRecyclerView(2)
        }


    }

    private fun setupButton() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
        tvTakipButton.setOnClickListener {

            mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChild(secilenUserID)) {
                        mRef.child("following").child(mUser.uid).child(secilenUserID).removeValue()
                        mRef.child("follower").child(secilenUserID).child(mUser.uid).removeValue()
                        takipciSayilariniGuncelle()
                        takipBirakButtonOzellikleri()

                    } else {
                        mRef.child("following").child(mUser.uid).child(secilenUserID).setValue(secilenUserID)
                        mRef.child("follower").child(secilenUserID).child(mUser.uid).setValue(mUser.uid)
                        takipciSayilariniGuncelle()
                        takipEtButtonOzellikleri()
                    }
                }

            })

        }


    }

    private fun takipciSayilariniGuncelle() {
        var mRef = FirebaseDatabase.getInstance().reference

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var takipEttikleriminSayisi = p0.childrenCount.toInt().toString()
                mRef.child("users").child(mUser.uid).child("user_detail").child("following").setValue(takipEttikleriminSayisi)

                //follower takipçi
                mRef.child("follower").child(secilenUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        var takipEdenlerinSayisi = p0.childrenCount.toInt().toString()

                        mRef.child("users").child(secilenUserID).child("user_detail").child("follower").setValue(takipEdenlerinSayisi)
                    }

                })
            }

        })
    }


    private fun takipBilgisiniGetir() {

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChild(secilenUserID)) {
                    takipEtButtonOzellikleri()

                } else {

                    takipBirakButtonOzellikleri()

                }
            }

        })

    }

    fun takipEtButtonOzellikleri() {
        tvTakipButton.setText("Takip Ediliyor")
        tvTakipButton.setTextColor(ContextCompat.getColor(this@UserProfileActivity, R.color.mavi))
        tvTakipButton.setBackgroundResource(R.color.beyaz)
    }

    fun takipBirakButtonOzellikleri() {
        tvTakipButton.setText("Takip Et")
        tvTakipButton.setTextColor(ContextCompat.getColor(this@UserProfileActivity, R.color.beyaz))
        tvTakipButton.setBackgroundResource(R.drawable.register_button_aktif)
    }


    private fun kullaniciPostlariniGetir(kullaniciID: String) {

        mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var userID = kullaniciID
                var kullaniciAdi = p0.child("user_name").value.toString()
                var kullaniciFotoUrl = p0.child("user_detail").child("profile_picture").value.toString()

                FirebaseDatabase.getInstance().reference.child("post").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        //postSayisiGüncelle
                        var postSayisi = p0.childrenCount.toInt().toString()
                        FirebaseDatabase.getInstance().reference.child("users").child(kullaniciID).child("user_detail").child("post").setValue(postSayisi)

                        if (p0.hasChildren()) {


                            for (ds in p0.children) {
                                var eklenecekUserPost = UserPost() //boş contructer olusturdugumuz ıcın bu hata vermiyor.
                                eklenecekUserPost.userID = userID
                                eklenecekUserPost.userName = kullaniciAdi
                                eklenecekUserPost.userPhotoUrl = kullaniciFotoUrl
                                eklenecekUserPost.postID = ds.getValue(Posts::class.java)!!.post_id
                                eklenecekUserPost.postUrl = ds.getValue(Posts::class.java)!!.file_url
                                eklenecekUserPost.postAciklama = ds.getValue(Posts::class.java)!!.aciklama
                                eklenecekUserPost.postYuklenmeTarihi = ds.getValue(Posts::class.java)!!.yuklenme_tarihi

                                tumGonderiler.add(eklenecekUserPost)


                            }
                        }
                        setupRecyclerView(1)
                    }

                })

            }

        })


    }

    private fun kullaniciBilgileriniGetir(gelenID: String) {

        mRef.child("users").child(gelenID).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.getValue() != null) {
                    //  var okunanKullaniciBilgileri = p0.getValue(Users::class.java)

                    //    EventBus.getDefault().postSticky(EventbusDataEvents.KullaniciBilgileriniGonderr(okunanKullaniciBilgileri))

                    tvPostSayisi.text = p0.child("user_detail").child("post").value.toString()
                    tvProfilAdiToolbar.text = p0.child("user_name").value.toString()
                    tvFollowerSayisi.text = p0.child("user_detail").child("follower").value.toString()
                    tvFollowingSayisi.text = p0.child("user_detail").child("following").value.toString()
                    tvProfilGercekAdi.text = p0.child("user_name").value.toString()

                    //Profil fotosunu byle cagırıyoruz.
                    val imgURl: String = p0.child("user_detail").child("profile_picture").value.toString()

                    UniversalImageLoader.setImage(
                        imgURl,
                        circleProfileImage, mProgressBarActivityProfile
                    )

                    if (!p0.child("user_detail").child("biography").getValue()!!.equals("")) {

                        tvBiyografi.visibility = View.VISIBLE
                        tvBiyografi.text = p0.child("user_detail").child("biography").value.toString()

                    }

                    if (!p0.child("user_detail").child("web_site").getValue()!!.equals("")) {

                        tvWebSitesi.visibility = View.VISIBLE
                        tvWebSitesi.text = p0.child("user_detail").child("web_site").value.toString()

                    }

                }


                takipBilgisiniGetir()
            }


        })

    }

    private fun setupRecyclerView(layoutCesidi: Int) {

        if (layoutCesidi == 1) {

            var kullaniciPostListe = profileRecyclerView

            kullaniciPostListe.adapter = ProfilePostGridAdapter(this, tumGonderiler)
            kullaniciPostListe.layoutManager = GridLayoutManager(this, 3)


        } else if (layoutCesidi == 2) {

            var kullaniciPostListe = profileRecyclerView

            kullaniciPostListe.adapter = ProfilePostListRecyclerAdapter(this, tumGonderiler)

            kullaniciPostListe.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }


}

package com.creativeoffice.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.Login.LoginActivity
import com.creativeoffice.Models.Posts
import com.creativeoffice.Models.UserPost
import com.creativeoffice.Models.Users
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.BottomnavigationViewHelper
import com.creativeoffice.utils.HomeFragmentRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    lateinit var fragmentView: View
    private val ACTIVITY_NO = 0

    lateinit var tumGonderiler: ArrayList<UserPost>
    lateinit var tumTakipEttiklerim: ArrayList<String>

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { //saveInstance ise uygulama yan dondugunde hersey sıl bastan yapılır bunu engeller verileri korur. ınflater java kodlarını xml e cevırır.
        fragmentView = inflater.inflate(R.layout.fragment_home, container, false) //biz fragmenti nereye koyarsak container orasıdır.

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        tumGonderiler = ArrayList<UserPost>()



        tumTakipEttiklerim = ArrayList()

        tumTakipEttiklerimiGetir()



        fragmentView.imgTabCamera.setOnClickListener {

            (activity as HomeActivity).homeViewPager.setCurrentItem(0)

        }
        fragmentView.imgTabDirectMessage.setOnClickListener {
            (activity as HomeActivity).homeViewPager.setCurrentItem(2)


        }

        return fragmentView
    }

    private fun tumTakipEttiklerimiGetir() {
        tumTakipEttiklerim.add(mUser.uid.toString())

        mRef.child("following").child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.value != null) {
                    for (ds in p0.children) {
                        tumTakipEttiklerim.add(ds.key!!)
                    }
                    kullaniciPostlariniGetir()
                } else {

                }

            }


        })

    }

    private fun kullaniciPostlariniGetir() {

        mRef = FirebaseDatabase.getInstance().reference
        for (i in 0..tumTakipEttiklerim.size - 1) {
            var kullaniciID = tumTakipEttiklerim.get(i)
            mRef.child("users").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var userID = kullaniciID
                    var kullaniciAdi = p0.child("user_name").value.toString()
                    var kullaniciFotoUrl = p0.child("user_detail").child("profile_picture").value.toString()

                    mRef.child("post").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (p0.hasChildren()) {
                             //   Log.e("takip ettiklerinin post", kullaniciID+" kişinin fotosu var")

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

                            if (i>=tumTakipEttiklerim.size-1){
                                setupRcyclerView()
                            }

                        }
                    })
                }
            })
        }


    }

    private fun setupRcyclerView() {

        var recyclerView = fragmentView.recyclerView
        var recyclerAdapter = HomeFragmentRecyclerAdapter(activity!!, tumGonderiler)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
    }


    fun setupNavigationView() {
        var fragmentNavView = fragmentView.bottomNavigationView

        BottomnavigationViewHelper.setupBottomNavigationView(fragmentNavView)
        BottomnavigationViewHelper.setupNavigation(activity!!, fragmentNavView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...

        var menu = fragmentNavView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }


    private fun setupAuthListener() {
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    var intent = Intent(
                        activity,
                        LoginActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity!!.finish()
                } else {


                }


            }

        }
    }

    override fun onStart() {
        mAuth.addAuthStateListener(mAuthListener)
        super.onStart()
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

}
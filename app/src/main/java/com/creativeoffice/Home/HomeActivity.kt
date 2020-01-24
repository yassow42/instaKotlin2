package com.creativeoffice.Home

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.creativeoffice.Login.LoginActivity
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.BottomnavigationViewHelper
import com.creativeoffice.utils.EventbusDataEvents
import com.creativeoffice.utils.HomePagerAdapter
import com.creativeoffice.utils.UniversalImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus

class HomeActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        mAuth = FirebaseAuth.getInstance()
       // mAuth.signOut()
        setupAuthListener()
        setupHomeViewPager()
        initImageLoader()

    }


    private fun setupHomeViewPager() {

        var homePagerAdapter =
            HomePagerAdapter(supportFragmentManager) // fm bekledıgı ıcın supporFrag ıle kolayca halledılıyor.

        homePagerAdapter.addFragment(CameraFragment())
        homePagerAdapter.addFragment(HomeFragment()) // id = 1
        homePagerAdapter.addFragment(MessagesFragment())

        //activity_mainde bulunan view pagera olusturdugumuz adapterı atadık.
        homeViewPager.adapter = homePagerAdapter

        //view pagerı home ıle baslamasını sagladık
        homeViewPager.setCurrentItem(1)  // Bu pagerda id = 1 olanı ılk olarak aç


        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {


            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {


            }

            override fun onPageSelected(position: Int) {

                if (position == 0) {
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN) // fullscren olmamaya zorlamayı sılıyourz

                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 1)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 2)
                    homePagerAdapter.secilenFragmentıViewPageraEkle(homeViewPager, 0)
                    kameraIzniIste()


                }
                if (position == 1) {
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 0)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 2)
                    homePagerAdapter.secilenFragmentıViewPageraEkle(homeViewPager, 1)

                }
                if (position == 2) {
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 1)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager, 0)
                    homePagerAdapter.secilenFragmentıViewPageraEkle(homeViewPager, 2)

                }

            }


        })


    }

    private fun kameraIzniIste() {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
            .withListener(object :
                MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    EventBus.getDefault().postSticky(EventbusDataEvents.KameraIzinBilgisiGonder(true))


                    if (report!!.areAllPermissionsGranted()) {
                        homeViewPager.setCurrentItem(0)
                    }

                    if (report!!.isAnyPermissionPermanentlyDenied) {

                        homeViewPager.setCurrentItem(1)  // kullanıcı izin vermezse kameraya giremez.


                    }

                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    token!!.continuePermissionRequest()

                }


            }).check()
    }


    private fun initImageLoader() {

        var universalImageLoader = UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }


    override fun onBackPressed() {

        if (homeViewPager.currentItem == 1) {
            homeRoot.visibility = View.VISIBLE
            homeContainer.visibility = View.GONE
            super.onBackPressed()
        } else {
            homeRoot.visibility = View.VISIBLE
            homeContainer.visibility = View.GONE
            homeViewPager.setCurrentItem(1)
        }


    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    var intent = Intent(this@HomeActivity, LoginActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NO_ANIMATION
                    )
                    startActivity(intent)
                    finish()
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
        super.onResume()
        homeViewPager.setCurrentItem(1)

    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

}

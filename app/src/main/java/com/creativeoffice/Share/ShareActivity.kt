package com.creativeoffice.Share

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.creativeoffice.Login.LoginActivity
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.BottomnavigationViewHelper
import com.creativeoffice.utils.SharePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_share.*
import java.util.jar.Manifest

class ShareActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 2
    private val TAG = "ShareActivity"

    lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        setupAuthListener()
        storageVeKameraIzniIste()
        // setupShareViewPager()


    }


    private fun setupShareViewPager() {
        var tabAdlari = ArrayList<String>()
        tabAdlari.add("Galeri")
        tabAdlari.add("FOTOĞRAF")
        //  tabAdlari.add("VİDEO")


        var sharePagerAdapter = SharePagerAdapter(supportFragmentManager, tabAdlari)
        sharePagerAdapter.addFragment(ShareGalleryFragment())
        sharePagerAdapter.addFragment(ShareCameraFragment())
        // sharePagerAdapter.addFragment(ShareVideoFragment())

        shareViewPager.adapter = sharePagerAdapter
        shareViewPager.offscreenPageLimit = 1   //burada pagerler arası gecıslerde hepsi calısıyordu bunu arka ve onden 1 adet calıstır dıye sınırladık daha sonra addOnPagelıstener ıle de arkasında
        // kı ve ondekı fragmentı kapatacagız kı cameralar aynı anda calısamasın....

        /*   shareViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
               override fun onPageScrollStateChanged(state: Int) {

               }

               override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

               }

               override fun onPageSelected(position: Int) {

                   if (position == 0) {

                       sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,1)
                       sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,2)
                       sharePagerAdapter.secilenFragmentıViewPageraEkle(shareViewPager,0)

                   }
                   if (position == 1) {

                       sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,0)
                       sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,2)
                       sharePagerAdapter.secilenFragmentıViewPageraEkle(shareViewPager,1)
                   }
                   if (position == 2) {

                       sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,0)
                       sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,1)
                       sharePagerAdapter.secilenFragmentıViewPageraEkle(shareViewPager,2)
                   }

               }


           })*/
        shareTabLayout.setupWithViewPager(shareViewPager)

    }

    private fun storageVeKameraIzniIste() {

        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    if (report!!.areAllPermissionsGranted()) {
                        Log.e("HATA", "Tüm izinler verilmiş")
                        setupShareViewPager()

                    }else if (report!!.isAnyPermissionPermanentlyDenied) {
                        Log.e("HATA", "izinlerden birine bidaha sorma denmiş")

                        var builder = AlertDialog.Builder(this@ShareActivity)
                        builder.setTitle("İzin Gerekli")
                        builder.setMessage("Ayarlar kısmından uygulamaya izin vermeniz gerekiyor. Onaylar mısınız ?")
                        builder.setPositiveButton("AYARLARA GİT", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                var uri = Uri.fromParts("package", packageName, null)
                                intent.setData(uri)
                                startActivity(intent)
                                finish()
                            }

                        })
                        builder.setNegativeButton("IPTAL", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                              //  finish()
                            }

                        })
                        builder.show()

                    }else {
                        finish()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {

                    Log.e("HATA", "izinlerden biri reddedilmiş, kullanıcıyı ikna et")

                    var builder = AlertDialog.Builder(this@ShareActivity)
                    builder.setTitle("İzin Gerekli")
                    builder.setMessage("Uygulamaya izin vermeniz gerekiyor. Onaylar mısınız ?")
                    builder.setPositiveButton("ONAY VER", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                            token!!.continuePermissionRequest()
                        }

                    })
                    builder.setNegativeButton("IPTAL", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                            token!!.cancelPermissionRequest()
                            finish()
                        }

                    })
                    builder.show()


                }

            })
            .withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError?) {
                    Log.e("HATA", error!!.toString())
                }

            }).check()


    }


    override fun onBackPressed() {

        anaLayout.visibility = View.VISIBLE
        fragmentContainerLayout.visibility = View.GONE


        super.onBackPressed()
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    var intent = Intent(this@ShareActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                } else {

                }


            }

        }
    }

}

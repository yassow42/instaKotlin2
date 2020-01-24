package com.creativeoffice.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_profile_setting.*


class ProfileSettingActivity : AppCompatActivity() {


    private val ACTIVITY_NO = 4
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)


        setupNavigationView()
        setupToolbar()
        fragmentNavigation()


    }

    private fun fragmentNavigation() {
        tvProfilDuzenleHesapAyarlari.setOnClickListener {
            //Frame layout içinde fragment cagırma yapıyoruz. transaction
            profileSettingRoot.visibility = View.GONE //profilde ki genel yazıları gone ettık cunku arka planda gozukmesın

            var transaction = supportFragmentManager.beginTransaction()
            transaction.addToBackStack("editProfileFragmentEklendi")// geriye adım ekledık. Cunu gerı dedıgımızde dırek activityprofile gidiyordu. Şimdi ise Activityprofile settinge gıdıyr

            transaction.replace(R.id.profileSettingContainer, ProfileEditFragment())
            transaction.commit()

        }

        tvCikisYap.setOnClickListener {
            //Frame layout içinde fragment cagırma yapıyoruz. transaction
            profileSettingRoot.visibility = View.GONE

            var dialog = SignOutFragment()
            dialog.show(supportFragmentManager,"cikisDiyalogGoster")

        }

    }

    override fun onBackPressed() {

        profileSettingRoot.visibility = View.VISIBLE

        super.onBackPressed()
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(
            this,
            bottomNavigationView
        ) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

}

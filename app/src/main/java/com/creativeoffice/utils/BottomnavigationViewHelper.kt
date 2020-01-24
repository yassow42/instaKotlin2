package com.creativeoffice.utils

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.core.content.ContextCompat.startActivity
import com.creativeoffice.Home.HomeActivity
import com.creativeoffice.News.NewsActivity
import com.creativeoffice.Profile.ProfileActivity
import com.creativeoffice.Share.ShareActivity
import com.creativeoffice.instakotlin2.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.creativeoffice.Search.SearchActivity as SearchActivity1

class BottomnavigationViewHelper {

    companion object {

        fun setupBottomNavigationView(bottomNavigationViewEx: BottomNavigationViewEx) {


            bottomNavigationViewEx.enableAnimation(false)
            bottomNavigationViewEx.enableItemShiftingMode(false)
            bottomNavigationViewEx.enableShiftingMode(false)
            bottomNavigationViewEx.setTextVisibility(false)

        }

//2 şey istedik 1 context 2. ise navigationView istedik. Home Actvity de ikisini de verdik.
        fun setupNavigation(context: Context, bottomNavigationViewEx: BottomNavigationViewEx) {

            bottomNavigationViewEx.onNavigationItemSelectedListener =
                object : BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {

                        when (item.itemId) {
                            R.id.ic_home -> {

                                val intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)


                                context.startActivity(intent) //context tanımlamanın önemi
                                return true
                            }

                            R.id.ic_search -> {
                                val intent = Intent(context, SearchActivity1::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)


                                context.startActivity(intent)
                                return true
                            }

                            R.id.ic_share -> {
                                val intent = Intent(context, ShareActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)


                                context.startActivity(intent)
                                return true
                            }

                            R.id.ic_news -> {
                                val intent = Intent(context, NewsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)


                                context.startActivity(intent)
                                return true
                            }

                            R.id.ic_profile -> {

                                val intent = Intent(context, ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)


                                context.startActivity(intent)
                                return true
                            }


                        }
                        return false
                    }


                }
        }
    }
}
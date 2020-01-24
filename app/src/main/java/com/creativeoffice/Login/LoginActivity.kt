package com.creativeoffice.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.creativeoffice.Home.HomeActivity
import com.creativeoffice.Models.Users
import com.creativeoffice.instakotlin2.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etSifre

class LoginActivity : AppCompatActivity() {
    lateinit var mRef: FirebaseDatabase
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mRef = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        init()
        setupAuthListener()

        FirebaseDatabase.getInstance().reference.child("dsad").child("baglantı var mı ? ").setValue("baglandı")
        Log.e("activity", "loginactivity")

    }


    private fun init() {

        etEmailTelorUsername.addTextChangedListener(watcher)
        etSifre.addTextChangedListener(watcher)

        tvKaydol.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        tvGirisYap.setOnClickListener {

            oturumAcacakKullaniciyiDenetle(etEmailTelorUsername.text.toString(), etSifre.text.toString())

        }
    }

    private fun oturumAcacakKullaniciyiDenetle(etEmailTelorUsername: String, sifre: String) {

        mRef.reference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.value != null) {
                        var kullaniciBulundu = false
                        for (ds in p0.children) {  //kullanıcıları okuduk.

                            val okunanKullanici = ds.getValue(Users::class.java)

                            if (okunanKullanici!!.email!!.toString().equals(etEmailTelorUsername)) {
                                oturumAc(okunanKullanici, sifre, false)
                                kullaniciBulundu = true
                                break

                            } else if (okunanKullanici!!.user_name!!.toString().equals(etEmailTelorUsername)) {
                                oturumAc(okunanKullanici, sifre, false) // telefon no ile girmediği için false duryor ıkı tarafta
                                kullaniciBulundu = true
                                break

                            } else if (okunanKullanici!!.phone_number!!.toString().equals(etEmailTelorUsername)) {
                                oturumAc(okunanKullanici, sifre, true) //burada tel no ile giriş yapmak istediginden true yaptık.
                                kullaniciBulundu = true
                                break
                            }
                        }
                        if (kullaniciBulundu == false) {
                            Toast.makeText(this@LoginActivity, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            })

    }

    private fun oturumAc(okunanKullanici: Users, sifre: String, telefonIleGiris: Boolean) {

        var girisYapacakEmail = ""

        if (telefonIleGiris == true) {
            girisYapacakEmail = okunanKullanici.email_phone_number.toString()
        } else {

            if(!okunanKullanici.email.toString().trim().isNullOrEmpty() && okunanKullanici.email_phone_number.toString().trim().isNullOrEmpty()){
                girisYapacakEmail = okunanKullanici.email.toString()
                Log.e("EEE","BURADA5")
            }else {
                girisYapacakEmail = okunanKullanici.email_phone_number.toString()
            }



        }

        mAuth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0!!.isSuccessful) {

                        Log.e("EEE","BURADA6")
                        progressBar3.visibility= View.GONE

                    } else {
                        progressBar3.visibility= View.GONE
                        Toast.makeText(this@LoginActivity, " Kullanıcı Adı/Sifre Hatalı :", Toast.LENGTH_SHORT).show()
                    }
                }

            })


    }

    private var watcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (etEmailTelorUsername.text.toString().length >= 6 && etSifre.text.toString().length >= 6) {
                tvGirisYap.isEnabled = true
                tvGirisYap.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.beyaz))
                tvGirisYap.setBackgroundResource(R.drawable.register_button_aktif)

            } else {

                tvGirisYap.isEnabled = false
                tvGirisYap.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.sonukmavi
                    )
                )
                tvGirisYap.setBackgroundResource(R.drawable.register_button)
            }
        }

    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    var intent = Intent(this@LoginActivity, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }


}

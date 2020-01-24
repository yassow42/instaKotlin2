package com.creativeoffice.Login


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.EventbusDataEvents
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.*
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class TelefonKoduGirFragment : Fragment() {

    var gelenTelNo = ""

    lateinit var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationID = ""
    var gelenKod = ""

    lateinit var progressBar:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_telefon_kodu_gir, container, false)

        view.tvKullaniciTelNo.text = gelenTelNo

        progressBar = view.pbTelNoOnayla

        setupCallBack()

        view.btnTelKodİleri.setOnClickListener {

           // EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(gelenTelNo,null,verificationID,gelenKod,false))


            if (gelenKod.equals(view.etOnayKodu.text.toString())) {

                val transaction = activity!!.supportFragmentManager.beginTransaction()

                transaction.replace(R.id.loginContainer, KayitFragment())
                transaction.addToBackStack("TelefonKodu")
                transaction.commit()


            } else {
                Toast.makeText(activity, "olmadı mk", Toast.LENGTH_LONG).show()

            }

        }



        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(gelenTelNo, 60, TimeUnit.SECONDS, activity!!, mCallBack)


        view.btnTelKodİleri.isEnabled = false
        view.etOnayKodu.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {


                if (start + before + count >= 6) {

                    btnTelKodİleri.isEnabled = true

                    btnTelKodİleri.setTextColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.beyaz
                        )
                    )
                    btnTelKodİleri.setBackgroundResource(R.drawable.register_button_aktif)

                } else {
                    btnTelKodİleri.isEnabled = false

                    btnTelKodİleri.setTextColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.sonukmavi
                        )
                    )
                    btnTelKodİleri.setBackgroundResource(R.drawable.register_button)


                }
            }


        })



        return view
    }


    private fun setupCallBack() {

        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                if (!credential.smsCode.isNullOrEmpty()){
                    gelenKod = credential.smsCode!!
                    progressBar.visibility = View.INVISIBLE

                }

            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.e("hatalar", e.toString())
                progressBar.visibility = View.INVISIBLE

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                progressBar.visibility = View.VISIBLE
                verificationID = verificationId!!
            }
        }

    }


    //Buradan da mesajı alıyorr.
    @Subscribe(sticky = true)
    internal fun onTelefonNoEvent(kayitBilgileri: EventbusDataEvents.KayitBilgileriniGonder) {

        gelenTelNo = kayitBilgileri.telNo!!

        Log.e("yasin", "gelen no :  $gelenTelNo")
    }


    //bu fragment bı yerlerden mesaj alıcak. Eventbus yayınından yanii
    override fun onAttach(context: Context) {

        EventBus.getDefault().register(this)
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()

        EventBus.getDefault().unregister(this)
    }
}

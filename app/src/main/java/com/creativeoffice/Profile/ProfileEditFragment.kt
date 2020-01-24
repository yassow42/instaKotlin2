package com.creativeoffice.Profile


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.creativeoffice.Models.Users

import androidx.appcompat.app.AppCompatActivity
import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.EventbusDataEvents
import com.creativeoffice.utils.UniversalImageLoader
import com.google.firebase.database.*
import com.google.firebase.database.snapshot.BooleanNode
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ProfileEditFragment : Fragment() {

    val RESIM_SEC = 100

    lateinit var gelenKullaniciBilgileri: Users
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mStorage: StorageReference

    var profilPhotoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mStorage = FirebaseStorage.getInstance().reference


        setupKullaniciBilgileri(view) //view parametresi olarak verik cunku inflate ettigimiz view a direk metodun içinde ulasamayız.
        // setupProfilePicture()

        view.imgCLose.setOnClickListener {

            activity?.onBackPressed()

        }

        view.tvFotografDegistir.setOnClickListener {
            var intent = Intent()

            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, RESIM_SEC)
        }

        view.imgBtnDegisiklikleriKaydet.setOnClickListener {

            if (profilPhotoUri != null) {

                var dialogYukleniyor = YukleniyorFragment()
                dialogYukleniyor.show(activity!!.supportFragmentManager, "yukleniyorFragmenti")
                dialogYukleniyor.isCancelable = false

                mStorage.child("users").child(gelenKullaniciBilgileri.user_id!!).child("profile_picture").putFile(profilPhotoUri!!) // burada fotografı kaydettik veritabanına.
                    .addOnSuccessListener { UploadTask ->
                        UploadTask.storage.downloadUrl.addOnSuccessListener { itUri ->

                            val downloadUrl = itUri.toString()

                            ////////////Burada storageden veriyi databaseye attık.
                            mDatabaseRef.child("users").child(gelenKullaniciBilgileri.user_id!!).child("user_detail").child("profile_picture").setValue(downloadUrl)
                                .addOnCompleteListener { p0 ->

                                    if (p0.isSuccessful) {
                                        dialogYukleniyor.dismiss()

                                        kullaniciAdiniGuncelle(view, true)

                                    } else {
                                    //    Toast.makeText(activity, "Resim Yüklenemedi", Toast.LENGTH_LONG).show()
                                        kullaniciAdiniGuncelle(view, false)
                                    }
                                }
                        }
                    }
            } else {
                kullaniciAdiniGuncelle(view, null)
            }
        }

        return view
    }

    // true ise resim yuklenmıs
    //false ise resım yuklenemdı
    // null ise degsıtırmek ıstemedı
    private fun kullaniciAdiniGuncelle(view: View, profilResmiDegisti: Boolean?) {

        if (!gelenKullaniciBilgileri.user_name!!.equals(view.etUserName.text.toString())) {
            mDatabaseRef.child("users").orderByChild("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var userNameKullanimdaMi = false

                    for (user in p0.children) {

                        var okunanKullanici = user.getValue(Users::class.java)!!.user_name

                        if (okunanKullanici!!.equals(view.etUserName.text.toString())) {

                            userNameKullanimdaMi = true
                            profilBilgileriniGuncelle(view, profilResmiDegisti, false)
                          //  Toast.makeText(activity, "kullanıcı adı kullanımda", Toast.LENGTH_SHORT).show()
                            break

                        } else {
                            userNameKullanimdaMi = false
                        }
                    }

                    if (userNameKullanimdaMi == false) {
                        mDatabaseRef.child("users").child(gelenKullaniciBilgileri.user_id!!).child("user_name").setValue(view.etUserName.text.toString())
                      //  Toast.makeText(activity, "Kullanıcı Adı Güncellendi...", Toast.LENGTH_SHORT).show()
                        profilBilgileriniGuncelle(view, profilResmiDegisti, true)

                    }

                }


            })

        } else {
            profilBilgileriniGuncelle(view, profilResmiDegisti, null)

        }

    }

    private fun profilBilgileriniGuncelle(view: View, profilResmiDegisti: Boolean?, userNameDegisti: Boolean?) {
        var profilGüncellendiMi:Boolean? = null
        if (!gelenKullaniciBilgileri!!.adi_soyadi.equals(view.etProfileName.text.toString())) {
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri.user_id!!).child("adi_soyadi").setValue(view.etProfileName.text.toString())
            profilGüncellendiMi = true
        }
        //  if (gelenKullaniciBilgileri.user_detail!!.biography!!.isNotEmpty()) {
        if (!gelenKullaniciBilgileri.user_detail!!.biography.equals(view.etUserBio.text.toString())) {
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri.user_id!!).child("user_detail").child("biography").setValue(view.etUserBio.text.toString())
            profilGüncellendiMi = true
        }
        //  if (gelenKullaniciBilgileri.user_detail!!.web_site!!.isNotEmpty()) {

        if (!gelenKullaniciBilgileri.user_detail!!.web_site.equals(view.etWebSite.text.toString())) {

            mDatabaseRef.child("users").child(gelenKullaniciBilgileri.user_id!!).child("user_detail").child("web_site").setValue(view.etWebSite.text.toString())
            profilGüncellendiMi = true
        }



        if (profilResmiDegisti == null && userNameDegisti== null && profilGüncellendiMi==null){
            Toast.makeText(activity, "Degisiklik yok", Toast.LENGTH_SHORT).show()
        }else if ( userNameDegisti == false && (profilResmiDegisti == true || profilGüncellendiMi == true)){
            Toast.makeText(activity, "Kullanıcı Bilgiler Güncellendi ama kullanıcı adı kullanımda", Toast.LENGTH_SHORT).show()

        }else if(userNameDegisti==false){
            Toast.makeText(activity, "Kullanıcı adı kullanımda", Toast.LENGTH_SHORT).show()

        }
        else{

            Toast.makeText(activity, "Kullanıcı Güncellendi", Toast.LENGTH_SHORT).show()
            activity!!.onBackPressed()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESIM_SEC && resultCode == AppCompatActivity.RESULT_OK && data!!.data != null) {

            profilPhotoUri = data!!.data
            circleProfileImage.setImageURI(profilPhotoUri)

        }

    }

    private fun setupKullaniciBilgileri(view: View?) {


        view!!.etProfileName.setText(gelenKullaniciBilgileri.adi_soyadi)
        view!!.etUserName.setText(gelenKullaniciBilgileri.user_name)

        if (!gelenKullaniciBilgileri.user_detail!!.biography!!.isNullOrEmpty()) {
            view!!.etUserBio.setText(gelenKullaniciBilgileri.user_detail!!.biography)
        }
        if (!gelenKullaniciBilgileri.user_detail!!.web_site!!.isNullOrEmpty()) {
            view!!.etWebSite.setText(gelenKullaniciBilgileri.user_detail!!.web_site)
        }

        if (!gelenKullaniciBilgileri.user_detail!!.profile_picture!!.isNullOrEmpty()) {
            val imgUrl: String = gelenKullaniciBilgileri.user_detail!!.profile_picture!!
            UniversalImageLoader.setImage(imgUrl, view!!.circleProfileImage, view!!.mProgresBar)
        }


    }


    ////////////////////////EVENTBUSS////////////////////////////////////////////
    @Subscribe(sticky = true)
    internal fun onKullaniciBilgileriEvent(kullaniciBilgileri: EventbusDataEvents.KullaniciBilgileriniGonder) {

        gelenKullaniciBilgileri = kullaniciBilgileri.kullanici!! //Lateinit var olarak kullanıcıyı global actık. daha sonra yayında gelen veriyi aldık

    }


    override fun onAttach(context: Context) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }

    override fun onDetach() {

        EventBus.getDefault().unregister(this)
        super.onDetach()
    }
    ////////////////////////EVENTBUSS////////////////////////////////////////////


}

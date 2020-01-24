package com.creativeoffice.Share


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.creativeoffice.Home.HomeActivity
import com.creativeoffice.Models.Posts
import com.creativeoffice.Profile.YukleniyorFragment

import com.creativeoffice.instakotlin2.R
import com.creativeoffice.utils.DosyaIslemleri
import com.creativeoffice.utils.EventbusDataEvents
import com.creativeoffice.utils.UniversalImageLoader
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_share_next.*
import kotlinx.android.synthetic.main.fragment_share_next.view.*
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception
import java.sql.Time

/**
 * A simple [Fragment] subclass.
 */
class ShareNextFragment : Fragment() {
    var secilenDosyaYolu: String? = null //gelen dosya yolu
    var dosyaTuruResimMi: Boolean? = null

    lateinit var photoURI: Uri

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var mStorageReference: StorageReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_share_next, container, false)

        UniversalImageLoader.setImage("file://" + secilenDosyaYolu!!, view!!.imgNextFragment, view.progressBar2)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        mStorageReference = FirebaseStorage.getInstance().reference


        photoURI = Uri.parse("file://" + secilenDosyaYolu)


        view.tvPaylasButton.setOnClickListener {
            //resim dosyası sıkıştırma
            if (dosyaTuruResimMi == true) {

                DosyaIslemleri.compressResimDosya(this, secilenDosyaYolu!!)


            }//videoları sıkıstırır
            else if (dosyaTuruResimMi == false) {

                DosyaIslemleri.compressVideoDosya(this, secilenDosyaYolu!!)
            }


        }

        view.imgClose.setOnClickListener {
            activity!!.onBackPressed()
        }

        return view
    }

    private fun veritabaninaBilgileriYaz(yuklenenPhotoURl: String?) {
        var postID = mRef.child("post").child(mUser.uid).push().key

        var yuklenenPost = Posts(mUser.uid, postID, 0, etPostAciklama.text.toString(), yuklenenPhotoURl)

        mRef.child("post").child(mUser.uid).child(postID!!).setValue(yuklenenPost)
        mRef.child("post").child(mUser.uid).child(postID).child("yuklenme_tarihi").setValue(ServerValue.TIMESTAMP)

        mRef.child("all_post").child(postID).setValue(yuklenenPost)
        mRef.child("all_post").child(postID).child("yuklenme_tarihi").setValue(ServerValue.TIMESTAMP)


        //gönderi açıklamasını yorum dugumunu eklıyoruz.
        if (!etPostAciklama.text.toString().isNullOrEmpty()) {

            mRef.child("comments").child(postID).child(postID).child("user_id").setValue(mUser.uid)
            mRef.child("comments").child(postID).child(postID).child("yorum_tarih").setValue(ServerValue.TIMESTAMP)
            mRef.child("comments").child(postID).child(postID).child("yorum").setValue(etPostAciklama.text.toString())
            mRef.child("comments").child(postID).child(postID).child("yorum_begeni").setValue("0")

        }

        postSayisiniGuncelle()

        ///Burada artık paylas butonuna tıkladıktan sonra veritabanına bılgıler yazılıyordu. Yazma işlemi bittikten sonra
        var intent = Intent(activity!!, HomeActivity::class.java)
        startActivity(intent)

    }

    private fun postSayisiniGuncelle() {
        mRef.child("users").child(mUser.uid).child("user_detail").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                /*
                  var oankiGonderi= p0!!.child("post").getValue().toString().toInt()
                  oankiGonderi++
                  mRef.child("users").child(mUser.uid).child("user_detail").child("post").setValue(oankiGonderi.toString())

                  */

            }

        })


    }

    fun uploadStorage(filePath: String?) {

        var fileUri = Uri.parse("file://" + filePath)

        var dialogYukleniyor = CompressandLoadingFragment()
        dialogYukleniyor.show(activity!!.supportFragmentManager, "Yuklenıyor")
        dialogYukleniyor.isCancelable = false

        val ref = mStorageReference.child("users").child(mUser.uid).child(fileUri.toString())
        var uploadTask = ref.putFile(fileUri)

        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                dialogYukleniyor.dismiss()
                veritabaninaBilgileriYaz(downloadUri.toString())
            } else {
                dialogYukleniyor.dismiss()
                Toast.makeText(activity, "Hata oluştu", Toast.LENGTH_SHORT).show()
            }
        }

        uploadTask.addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {

            override fun onProgress(p0: UploadTask.TaskSnapshot) {
                var progress = 100.0 * p0!!.bytesTransferred / p0!!.totalByteCount
                //Log.e("HATA", "ILERLEME : " + progress)
                dialogYukleniyor.tvBilgi.text = "%" + progress.toInt().toString() + " yüklendi.."

            }


        })


    }

    //////////////////////eventbuss//////////////////////////
    @Subscribe(sticky = true)

    internal fun onDosyaEvent(secilenResim: EventbusDataEvents.PaylasilacakResmiGonder) {
        secilenDosyaYolu = secilenResim.dosyaYol
        dosyaTuruResimMi = secilenResim.dosyaTuruResimMi
    }

    override fun onAttach(context: Context) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }


}

package com.creativeoffice.Profile


import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.DialogFragment



import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*


/**
 * A simple [Fragment] subclass.
 */
class SignOutFragment : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        var alert = AlertDialog.Builder(activity)
            .setTitle("Instagram'dan Çıkış Yap")
            .setMessage("Emin Misiniz ?")
            .setPositiveButton("Çıkış Yap", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    FirebaseAuth.getInstance().signOut()
                    activity!!.finish()
                }

            })
            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dismiss()
                }

            })
            .create()

        return alert
    }


}

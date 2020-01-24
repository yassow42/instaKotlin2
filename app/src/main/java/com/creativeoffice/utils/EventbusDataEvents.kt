package com.creativeoffice.utils

import com.creativeoffice.Models.Users

class EventbusDataEvents {

    internal class KayitBilgileriniGonder(
        var telNo: String?,
        var email: String?,
        var verificationID: String?,
        var code: String?,
        var emailkayit: Boolean
    )

    internal class KullaniciBilgileriniGonder(var kullanici: Users?)

    internal class PaylasilacakResmiGonder(var dosyaYol: String?, var dosyaTuruResimMi: Boolean?)

    internal class GaleriDosyaYolunuGonder(var dosyaYolu: String?)

    internal class KameraIzinBilgisiGonder(var IzinVerildiMi: Boolean?)

    internal class YorumYapilacakGonderiIDYolla(var gonderiID: String?)


    internal class kullaniciIDgonder(var kullaniciID: String?)


}
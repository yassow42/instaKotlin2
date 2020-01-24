package com.creativeoffice.Models

class UserPost {

    var userID:String? = null
    var userName:String? = null
    var userPhotoUrl:String? = null
    var postID:String? = null
    var postAciklama:String? = null
    var postUrl:String? = null
    var postYuklenmeTarihi:Long?=null

    constructor(userID: String?, userName: String?, userPhotoUrl: String?, postID: String?, postAciklama: String?, postUrl: String?, postYuklenmeTarihi: Long?) {
        this.userID = userID
        this.userName = userName
        this.userPhotoUrl = userPhotoUrl
        this.postID = postID
        this.postAciklama = postAciklama
        this.postUrl = postUrl
        this.postYuklenmeTarihi = postYuklenmeTarihi
    }

    constructor()

    override fun toString(): String {
        return "UserPost(userID=$userID, userName=$userName, userPhotoUrl=$userPhotoUrl, postID=$postID, postAciklama=$postAciklama, postUrl=$postUrl, postYuklenmeTarihi=$postYuklenmeTarihi)"
    }


}
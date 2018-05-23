package com.example.hexa_aaronlee.nearbuy.DatabaseData


class UserData
{
    var email : String ?= null
    var password : String ?= null
    var name : String ?= null
    var user_id : String ?= null
    var profilePhoto : String ?= null
    var siginType : String ?= null

    constructor(){}
    constructor(email: String?, password: String?, name: String?, user_id: String?, profilePhoto: String?, siginType: String?) {
        this.email = email
        this.password = password
        this.name = name
        this.user_id = user_id
        this.profilePhoto = profilePhoto
        this.siginType = siginType
    }
}
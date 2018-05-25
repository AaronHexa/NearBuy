package com.example.hexa_aaronlee.nearbuy.DatabaseData

class HistoryData {

    var history_user :String? = null
    var history_id :String? = null

    constructor(){}

    constructor(history_user : String?, history_id: String?){
        this.history_user = history_user
        this.history_id = history_id
    }
}
package com.example.hexa_aaronlee.nearbuy.DatabaseData

import com.example.hexa_aaronlee.nearbuy.ChatHistory

class MessageData {

    var messageText : String ?= null
    var userSend : String ?= null
    var message_id : String ?= null

    constructor(){}

    constructor(messageText: String?, message_id: String?, userSend: String?) {
        this.messageText = messageText
        this.userSend = userSend
        this.message_id = message_id
    }
}
package com.harjeet.chitForChat.Models

class ChatFriendsModel {
    constructor(){

    }
    constructor(
        userId: String,
        name: String,
        image:String,
        origonalMessage:String,

    ) {
        this.name = name
        this.userId = userId
        this.image = image
        this.origonalMessage=origonalMessage
    }
    var origonalMessage: String? = null
        get() = field
        set(value) {
            field = value
        }


    var image: String? = null
        get() = field
        set(value) {
            field = value
        }
    var name: String? = null
        get() = field
        set(value) {
            field = value
        }
    var userId: String? = null
        get() = field
        set(value) {
            field = value
        }


}
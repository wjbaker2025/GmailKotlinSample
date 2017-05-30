package org.hevin.gmailkotlion.model

class Message {
    var id: Int = -1
    var from: String? = null
    var subject: String? = null
    var message: String? = null
    var timestamp: String? = null
    var picture: String? = null
    var isImport: Boolean = false
    var isRead: Boolean = false
    var color: Int = -1
}
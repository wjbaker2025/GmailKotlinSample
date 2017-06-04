package org.hevin.gmailkotlion.model

data class Message (
    var id: Int,
    var from: String,
    var subject: String,
    var message: String,
    var timestamp: String,
    var picture: String?,
    var isImport: Boolean,
    var isRead: Boolean,
    var color: Int
)
package org.hevin.gmailkotlion.network

import org.hevin.gmailkotlion.model.Message
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("inbox.json")
    fun getInbox(): Call<List<Message>>
}
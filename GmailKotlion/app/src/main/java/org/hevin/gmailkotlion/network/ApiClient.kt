package org.hevin.gmailkotlion.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private object Holder {
        val BASE_URL = "http://api.androidhive.info/json/"

        val INSTANCE: Retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
    }

    companion object {
        val instance: Retrofit by lazy { Holder.INSTANCE }
    }
}
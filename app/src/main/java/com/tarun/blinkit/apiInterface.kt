package com.tarun.blinkit


import com.tarun.blinkit.Models.checkStatuModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface apiInterface {

    @GET("apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")
    suspend fun checkStatus(
        @Path("merchantId") merchantId: String,
        @Path("transactionId") transactionId: String,
        @HeaderMap headers: Map<String, String>,

        ): Response<checkStatuModel>
}
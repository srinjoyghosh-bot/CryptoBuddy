package com.example.cryptoapp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CryptoApi {

    @Headers("X-CoinAPI-Key:2C00F25F-D35B-4268-8AAC-33D60AF8A985")
    @GET("v1/assets")
    Call<List<CryptoLive>> getCryptos();

    @Headers("X-CoinAPI-Key:2C00F25F-D35B-4268-8AAC-33D60AF8A985")
    @GET("v1/exchangerate/{asset_id_base}/{asset_id_quote}")
    Call<CryptoRate> getRate(@Path("asset_id_base") String base,@Path("asset_id_quote") String quote);

    @Headers("X-CoinAPI-Key:2C00F25F-D35B-4268-8AAC-33D60AF8A985")
    @GET("v1/exchangerate/{asset_id_base}/{asset_id_quote}/history")
    Call<List<CryptoTimeData>> getTimedResults(@Path("asset_id_base") String base, @Path("asset_id_quote") String quote, @Query("period_id") String period, @Query("time_start") LocalDateTime timeStart, @Query("time_end") LocalDateTime timeEnd);

    @Headers("X-CoinAPI-Key:2C00F25F-D35B-4268-8AAC-33D60AF8A985")
    @GET("v1/exchangerate/{asset_id_base}/{asset_id_quote}")
    Call<CryptoRate> getTimedRate(@Path("asset_id_base") String base,@Path("asset_id_quote") String quote,@Query("time") LocalDateTime time);

//API keys:
//2C00F25F-D35B-4268-8AAC-33D60AF8A985
//11F06E00-7031-44B7-9535-4641C3B8F766
//    98EF96D8-3B54-4A47-9F17-C3234452E8C4
}

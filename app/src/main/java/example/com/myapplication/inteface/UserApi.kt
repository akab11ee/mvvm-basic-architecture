package example.com.myapplication.inteface

import example.com.myapplication.data.LoginContainer
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("log/")
    fun userAuthenticate(
        @Query("userId") UserID: String,
        @Query("pwd") pwd: String
    ): Single<LoginContainer>
}
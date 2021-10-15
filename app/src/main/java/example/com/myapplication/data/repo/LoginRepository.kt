package example.com.myapplication.data.repo

import example.com.myapplication.data.LoginContainer
import example.com.myapplication.inteface.UserApi
import io.reactivex.Single
import javax.inject.Inject

class UserLoginRepository @Inject constructor(private val userApi: UserApi) {

    fun authenticateUser(userName: String, passWord: String): Single<LoginContainer> =
        userApi.userAuthenticate(userName, passWord)
}
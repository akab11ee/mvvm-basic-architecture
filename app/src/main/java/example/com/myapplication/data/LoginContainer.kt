package example.com.myapplication.data

import com.google.gson.annotations.SerializedName

data class LoginContainer(
    @SerializedName("ret_code")
    var ret_code: Int,
    @SerializedName("ret_msg")
    var ret_msg: String,
    @SerializedName("data")
    var data: UserLoginDetail
)
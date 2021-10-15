package example.com.myapplication.data

import com.google.gson.annotations.SerializedName

data class UserLoginDetail(
    @SerializedName("UserID")
    var userId: String? = null,
    @SerializedName("FirstName")
    var firstName: String? = null,
    @SerializedName("LastName")
    var lastName: String? = null,
    @SerializedName("token")
    var token: Long = 0
) {
    fun fillUserDetails(userDetails: UserLoginDetail) {
        userId = userDetails.userId
        firstName = userDetails.firstName
        lastName = userDetails.lastName
        token = userDetails.token
    }
}
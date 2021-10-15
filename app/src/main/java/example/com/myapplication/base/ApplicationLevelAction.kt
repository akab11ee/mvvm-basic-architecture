package example.com.myapplication.base

class ApplicationLevelAction {

    companion object {
        const val ARG_SESSION = "session"
    }

    private var sessionExpiredLiveData = SingleLiveData<Boolean>()
    private var legacyVersionLiveData = SingleLiveData<Boolean>()


    fun setApplicationLevelObject(applicationLevelActionObject: ApplicationLevelActionObject) {
        if (applicationLevelActionObject.errorCode == "LEGACY_VERSION" || applicationLevelActionObject.errorCode == "Blacklisted_All_NewUpdate") {
            legacyVersionLiveData.postValue(true)
        } else {
            sessionExpiredLiveData.postValue(true)
        }
    }

    data class ApplicationLevelActionObject(
        val isSessionExpired: Boolean = false,
        val errorCode: String? = null
    )
}
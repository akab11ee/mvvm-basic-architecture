package example.com.myapplication.data

import dagger.Reusable
import example.com.myapplication.base.SingleWrapper
import example.com.myapplication.base.SingleWrapperUseCase
import example.com.myapplication.data.repo.UserLoginRepository
import example.com.myapplication.inteface.ThreadScheduler
import javax.inject.Inject

@Reusable
class LoginUseCase @Inject constructor(
    threadScheduler: ThreadScheduler,
    private val repo: UserLoginRepository,
    private val userDetails: UserLoginDetail
) : SingleWrapperUseCase(threadScheduler) {

    operator fun invoke(userName: String, password: String): SingleWrapper<LoginContainer> {
        return buildUseCase {
            repo.authenticateUser(userName, password)
                .map {
                    this.userDetails.fillUserDetails(it.data)
                    return@map it
                }
        }
    }
}
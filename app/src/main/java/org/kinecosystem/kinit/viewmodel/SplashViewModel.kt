package org.kinecosystem.kinit.viewmodel

import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.network.ServicesProvider
import org.kinecosystem.kinit.repository.UserRepository
import org.kinecosystem.kinit.util.Scheduler
import org.kinecosystem.kinit.view.SplashNavigator
import javax.inject.Inject


class SplashViewModel(private var splashNavigator: SplashNavigator?) {

    private companion object {
        const val SPLASH_DURATION: Long = 1000L
    }

    @Inject
    lateinit var scheduler: Scheduler
    @Inject
    lateinit var analytics: Analytics
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var servicesProvider: ServicesProvider


    var googlePlayServicesAvailable: Boolean = true

    init {
        KinitApplication.coreComponent.inject(this)
    }

    fun onResume() {
        scheduler.scheduleOnMain({
            checkReadyToMove()
        }, SPLASH_DURATION)
        analytics.logEvent(Events.Analytics.ViewSplashscreenPage())
    }

    private fun checkReadyToMove() {
        if (googlePlayServicesAvailable) {
            userRepository.isFirstTimeUser = true
            moveToNextScreen()
        }
    }

    private fun moveToNextScreen() {
        if (userRepository.isFirstTimeUser
                && userRepository.isPhoneVerificationEnabled
                && !userRepository.isPhoneVerified) {
            splashNavigator?.moveToTutorialScreen()
        } else if (userRepository.isFirstTimeUser && !userRepository.isPhoneVerificationEnabled) {
            splashNavigator?.moveToTutorialScreen()
        } else if (!servicesProvider.walletService.ready.get())
            splashNavigator?.moveToWalletCreation()
        else {
            splashNavigator?.moveToMainScreen()
        }
    }
}


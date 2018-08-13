package org.kinecosystem.kinit.viewmodel

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.view.View
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.network.ServicesProvider
import org.kinecosystem.kinit.repository.UserRepository
import org.kinecosystem.kinit.util.Scheduler
import javax.inject.Inject


class WalletCreationViewModel {
    @Inject
    lateinit var analytics: Analytics
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var servicesProvider: ServicesProvider
    @Inject
    lateinit var scheduler: Scheduler

    var callback: Observable.OnPropertyChangedCallback? = null
    private var walletReady: ObservableBoolean
    private var listener: WalletEventsListener? = null

    private companion object {
        const val SPLASH_DURATION: Long = 2000L
        const val CREATE_WALLET_TIMEOUT = 20000L
    }

    init {
        KinitApplication.coreComponent.inject(this)
        servicesProvider.onBoardingService.appLaunch()
        walletReady = servicesProvider.walletService.ready
    }

    fun setListener(listener: WalletEventsListener) {
        this.listener = listener
    }

    fun onResume() {
        listener?.onWalletCreating()
        scheduler.scheduleOnMain({
            checkReadyToMove()
        }, SPLASH_DURATION)
    }

    private fun addWalletReadyCallback() {
        if (callback == null) {
            callback = object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(p0: Observable?, p1: Int) {
                    listener?.onWalletCreated()
                }
            }
            walletReady.addOnPropertyChangedCallback(callback)
        }
    }

    fun onDestroy() {
        if (callback != null) {
            walletReady.removeOnPropertyChangedCallback(callback)
            callback = null
        }
    }

    private fun scheduleTimeout() {
        scheduler.scheduleOnMain(
                {
                    if (walletReady.get()) {
                        listener?.onWalletCreating()
                    } else {
                        analytics.logEvent(Events.Analytics.ViewErrorPage(Analytics.VIEW_ERROR_TYPE_ONBOARDING))
                        listener?.onWalletCreationError()
                    }
                },
                CREATE_WALLET_TIMEOUT)
    }

    private fun checkReadyToMove() {
        if (walletReady.get()) {
            listener?.onWalletCreated()
        } else {
            userRepository.isFirstTimeUser = true
            addWalletReadyCallback()
            scheduleTimeout()
        }
    }

    fun onRetryClicked(view: View?) {
        analytics.logEvent(Events.Analytics.ClickRetryButtonOnErrorPage(Analytics.VIEW_ERROR_TYPE_ONBOARDING))
        listener?.onWalletCreating()
        servicesProvider.onBoardingService.appLaunch()
        checkReadyToMove()
    }

    fun onContactSupportClicked(view: View?) {
        analytics.logEvent(Events.Analytics.ClickContactLinkOnErrorPage(Analytics.VIEW_ERROR_TYPE_ONBOARDING))
        listener?.onContactSupport(userRepository)
    }

}

interface WalletEventsListener {
    fun onWalletCreated()
    fun onWalletCreationError()
    fun onWalletCreating()
    fun onContactSupport(userRepository: UserRepository)
}

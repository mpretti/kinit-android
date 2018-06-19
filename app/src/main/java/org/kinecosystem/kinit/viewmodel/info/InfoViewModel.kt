package org.kinecosystem.kinit.viewmodel.info

import android.view.View
import dagger.CoreComponent
import org.kinecosystem.kinit.BuildConfig
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.repository.UserRepository
import org.kinecosystem.kinit.util.SupportUtil
import org.kinecosystem.kinit.view.TabViewModel
import javax.inject.Inject

class InfoViewModel() : TabViewModel {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var analytics: Analytics

    init {
        KinitApplication.coreComponent.inject(this)
    }
    var version: String = if (BuildConfig.DEBUG) {
        "${BuildConfig.VERSION_NAME} ${BuildConfig.BUILD_TYPE}"
    } else {
        BuildConfig.VERSION_NAME
    }

    fun onContactSupportClicked(view: View) {
        analytics.logEvent(Events.Analytics.ClickSupportButton())
        analytics.logEvent(Events.Business.SupportRequestSent())
        SupportUtil.openEmailSupport(view.context, userRepository)
    }

    override fun onScreenVisibleToUser() {
        analytics.logEvent(Events.Analytics.ViewProfilePage())
    }
}

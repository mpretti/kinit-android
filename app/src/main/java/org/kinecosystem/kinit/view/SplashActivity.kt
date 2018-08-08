package org.kinecosystem.kinit.view

import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.repository.UserRepository
import org.kinecosystem.kinit.view.tutorial.TutorialActivity
import org.kinecosystem.kinit.view.walletCreation.WalletCreationActivity
import org.kinecosystem.kinit.viewmodel.SplashViewModel
import javax.inject.Inject


class SplashActivity : BaseActivity(), SplashNavigator {


    private companion object {
        private const val PLAY_SERVICES_UPDATE_REQUEST = 100
    }

    @Inject
    lateinit var userRepository: UserRepository

    override fun moveToTutorialScreen() {
        startActivity(TutorialActivity.getIntent(this))
        finish()
    }

    private var splashViewModel: SplashViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        KinitApplication.coreComponent.inject(this)
        super.onCreate(savedInstanceState)
        splashViewModel = SplashViewModel(this)
        moveToSplashScreen()
    }

    override fun moveToSplashScreen() {
        setContentView(R.layout.splash_layout)
    }

    override fun moveToMainScreen() {
        startActivity(MainActivity.getIntent(this))
        finish()
    }

    override fun moveToWalletCreation() {
        startActivity(WalletCreationActivity.getIntent(this, false))
        finish()
    }

    override fun onResume() {
        super.onResume()
        checkGoogleServices()
        splashViewModel?.onResume()
    }

    private fun checkGoogleServices() {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        splashViewModel?.googlePlayServicesAvailable = status == ConnectionResult.SUCCESS
        if (status != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, status, PLAY_SERVICES_UPDATE_REQUEST).show()
        }
    }
}

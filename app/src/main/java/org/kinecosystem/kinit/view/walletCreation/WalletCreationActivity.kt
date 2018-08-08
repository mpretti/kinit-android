package org.kinecosystem.kinit.view.walletCreation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.network.TaskService
import org.kinecosystem.kinit.repository.UserRepository
import org.kinecosystem.kinit.util.SupportUtil
import org.kinecosystem.kinit.view.BaseSingleFragmentActivity
import org.kinecosystem.kinit.view.MainActivity
import org.kinecosystem.kinit.viewmodel.WalletCreationViewModel
import javax.inject.Inject

class WalletCreationActivity : BaseSingleFragmentActivity(), WalletCreationUIActions {
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var taskService: TaskService

    private lateinit var createWalletFragment: Fragment
    lateinit var model: WalletCreationViewModel
        private set
    private var hasPrevious = false

    companion object {
        const val FRAGMENT_WALLET_CREATION = "FRAGMENT_WALLET_CREATION"

        private const val HAS_PREVIOUS = "HAS_PREVIOUS"
        @JvmStatic
        fun getIntent(context: Context, hasPrevious: Boolean): Intent {
            val intent = Intent(context, WalletCreationActivity::class.java)
            intent.putExtra(HAS_PREVIOUS, hasPrevious)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = WalletCreationViewModel(this)
        hasPrevious = intent.getBooleanExtra(HAS_PREVIOUS, false)
        createWalletFragment = WalletCreationFragment.newInstance(false)
        super.onCreate(savedInstanceState)
    }

    override fun moveToMainScreen() {
        startActivity(MainActivity.getIntent(this))
        finish()
    }

    override fun moveToWalletCreatedScreen() {
        taskService.retrieveNextTask()
        replaceFragment(WalletCreationCompleteFragment.newInstance())
    }

    override fun moveToWalletCreationScreen() {
        replaceFragment(createWalletFragment, FRAGMENT_WALLET_CREATION)
    }

    override fun moveToErrorScreen() {
        replaceFragment(WalletCreationErrorFragment.newInstance(false))
    }

    override fun getFragment(): Fragment {
        return createWalletFragment
    }

    override fun inject() {
        KinitApplication.coreComponent.inject(this)
    }

    override fun openContactSupport() {
        SupportUtil.openEmailSupport(this, userRepository)
    }

    override fun onDestroy() {
        super.onDestroy()
        model.onDestroy()
    }


    override fun onResume() {
        super.onResume()
        model.onResume()
    }

    override fun init() = Unit
}

interface WalletCreationUIActions {
    fun moveToMainScreen()
    fun moveToWalletCreatedScreen()
    fun moveToErrorScreen()
    fun moveToWalletCreationScreen()
    fun openContactSupport()
}

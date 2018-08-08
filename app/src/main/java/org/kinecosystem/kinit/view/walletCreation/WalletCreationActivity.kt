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
import org.kinecosystem.kinit.viewmodel.WalletEventsListener
import javax.inject.Inject

class WalletCreationActivity : BaseSingleFragmentActivity(), WalletCreationActions, WalletEventsListener {
    @Inject
    lateinit var taskService: TaskService

    private lateinit var createWalletFragment: Fragment
    private lateinit var createWalletErrorFragment: Fragment
    lateinit var model: WalletCreationViewModel
        private set

    companion object {
        const val FRAGMENT_WALLET_CREATION = "FRAGMENT_WALLET_CREATION"
        const val FRAGMENT_WALLET_CREATION_ERROR = "FRAGMENT_WALLET_CREATION_ERROR"

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, WalletCreationActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = WalletCreationViewModel()
        model.setListener(this)
        createWalletErrorFragment = WalletCreationErrorFragment.newInstance()
        createWalletFragment = WalletCreationFragment.newInstance()
        super.onCreate(savedInstanceState)
    }

    override fun moveToMainScreen() {
        startActivity(MainActivity.getIntent(this))
        finish()
    }

    override fun getFragment(): Fragment {
        return createWalletFragment
    }

    override fun inject() {
        KinitApplication.coreComponent.inject(this)
    }

    override fun onWalletCreated() {
        taskService.retrieveNextTask()
        replaceFragment(WalletCreationCompleteFragment.newInstance())
    }

    override fun onWalletCreationError() {
        replaceFragment(createWalletErrorFragment, FRAGMENT_WALLET_CREATION_ERROR)
    }

    override fun onWalletCreating() {
        replaceFragment(createWalletFragment, FRAGMENT_WALLET_CREATION)
    }

    override fun onContactSupport(userRepository: UserRepository) {
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

interface WalletCreationActions {
    fun moveToMainScreen()
    fun onContactSupport(userRepository: UserRepository)
}

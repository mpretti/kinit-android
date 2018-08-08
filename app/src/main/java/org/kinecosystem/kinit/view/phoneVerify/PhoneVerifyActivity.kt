package org.kinecosystem.kinit.view.phoneVerify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.quiz_answer_layout.*
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.network.OperationCompletionCallback
import org.kinecosystem.kinit.repository.UserRepository
import org.kinecosystem.kinit.util.GeneralUtils
import org.kinecosystem.kinit.view.BaseSingleFragmentActivity
import org.kinecosystem.kinit.view.tutorial.TutorialActivity
import org.kinecosystem.kinit.view.walletCreation.WalletCreationActivity
import org.kinecosystem.kinit.viewmodel.PhoneVerificationViewModel
import javax.inject.Inject

class PhoneVerifyActivity : BaseSingleFragmentActivity(), PhoneVerificationUIActions {
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var analytics: Analytics

    companion object {

        val TAG = PhoneVerifyActivity::class.java.simpleName ?: "PhoneVerifyActivity"
        const val FRAGMENT_CODE_TAG = "FRAGMENT_CODE_TAG"
        const val PHONE_AUTH_DATA_STORE = "PHONE_AUTH_DATA_STORE"
        private const val HAS_PREVIOUS = "HAS_PREVIOUS"
        private const val MAX_SEND_PHONE_COUNT = 3

        @JvmStatic
        fun getIntent(context: Context, hasPrevious: Boolean): Intent {
            val intent = Intent(context, PhoneVerifyActivity::class.java)
            intent.putExtra(HAS_PREVIOUS, hasPrevious)
            return intent
        }
    }

    private var model: PhoneVerificationViewModel? = null
    private var hasPreviousScreen: Boolean = false
    private var sendPhoneCount = 0
    private var hasPrevious = false
    private lateinit var phoneSendFragment: Fragment

    val callback = object : OperationCompletionCallback {
        override fun onError(errorCode: Int) {
            val codeVerificationFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_CODE_TAG) as CodeVerificationFragment?
            if (codeVerificationFragment?.isVisible == true) {
                codeVerificationFragment.onError()
            }
        }

        override fun onSuccess() {
            userRepository.isPhoneVerified = true
            analytics.logEvent(Events.Business.UserVerified())
            userRepository.isFirstTimeUser = false
            GeneralUtils.closeKeyboard(this@PhoneVerifyActivity, frame_container)
            onVerificationComplete()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = PhoneVerificationViewModel(this, callback)
        hasPrevious = intent.getBooleanExtra(HAS_PREVIOUS, false)
        phoneSendFragment = PhoneSendFragment.newInstance(hasPrevious)
        super.onCreate(savedInstanceState)
    }

    override fun onSendPhone(phoneNumber: String) {
        sendPhoneCount++
        model?.startPhoneNumberVerification(phoneNumber)
        replaceFragment(CodeVerificationFragment.newInstance(phoneNumber,
                sendPhoneCount >= MAX_SEND_PHONE_COUNT),
                FRAGMENT_CODE_TAG)
    }

    override fun onSendCode(code: String) {
        model?.verifyPhoneNumberWithCode(code)
    }

    override fun onVerificationComplete() {
        startActivity(WalletCreationActivity.getIntent(this, false))
        finish()
    }

    override fun onBackPressed(fromPage: Int) {
        if (fromPage == 0) {
            startActivity(TutorialActivity.getIntent(this))
            finish()
        } else if (fromPage == 1) {
            replaceFragment(phoneSendFragment)
        }
    }

    override fun getFragment(): Fragment {
        return phoneSendFragment
    }

    override fun inject() {
        KinitApplication.coreComponent.inject(this)
    }

    override fun init() = Unit
}

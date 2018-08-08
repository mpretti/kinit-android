package org.kinecosystem.kinit.view.phoneVerify


import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.os.*
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import kotlinx.android.synthetic.main.phone_code_verify_fragment.*
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.repository.DataStoreProvider
import org.kinecosystem.kinit.repository.UserRepository
import org.kinecosystem.kinit.util.SupportUtil
import org.kinecosystem.kinit.view.BaseFragment
import javax.inject.Inject

class CodeVerificationFragment : BaseFragment() {
    @Inject
    lateinit var analytics: Analytics
    @Inject
    lateinit var dataStoreProvider: DataStoreProvider
    @Inject
    lateinit var userRepository: UserRepository

    companion object {

        val TAG = CodeVerificationFragment::class.java.simpleName
        private const val KEY_RESEND_CODE = "KEY_RESEND_CODE"
        private const val PHONE_NUMBER = "PHONE_NUMBER"
        private const val SHOW_CONTACT_SUPPORT = "SHOW_CONTACT_SUPPORT"
        private const val COUNT_DOWN = 16 * DateUtils.SECOND_IN_MILLIS
        private const val CODE_LENGTH: Long = 6
        private const val VIBRATE_DURATION: Long = 500
        private const val MAX_ERROR_COUNT = 3

        @JvmStatic
        fun newInstance(phone: String, showContactSupport: Boolean): CodeVerificationFragment {
            val fragment = CodeVerificationFragment()
            val args = Bundle()
            args.putString(PHONE_NUMBER, phone)
            args.putBoolean(SHOW_CONTACT_SUPPORT, showContactSupport)
            fragment.arguments = args
            return fragment
        }
    }

    private val inputs = arrayOfNulls<TextView>(6)
    private val lines = arrayOfNulls<View>(6)
    private var actions: PhoneVerificationUIActions? = null
    private var codeErrorCount = 0
    private var timer: CountDownTimer? = object : CountDownTimer(COUNT_DOWN, DateUtils.SECOND_IN_MILLIS) {
        override fun onTick(l: Long) {
            val counterText = resources.getString(R.string.code_sms_counter_subtitle, l / 1000)
            counter.text = counterText
        }

        override fun onFinish() {
            counter.visibility = View.GONE
            resend.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        KinitApplication.coreComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputs[0] = input0
        inputs[1] = input1
        inputs[2] = input2
        inputs[3] = input3
        inputs[4] = input4
        inputs[5] = input5

        lines[0] = line0
        lines[1] = line1
        lines[2] = line2
        lines[3] = line3
        lines[4] = line4
        lines[5] = line5

        hitArea.setOnClickListener { showKeyboardFocusCodeInput(it) }
        for (i in inputs.indices) {
            inputs[i]?.text = ""
        }
        code_input.isFocusable = true
        code_input.requestFocus()
        code_input.y = 0f
        code_input.x = 50000f
        val counterText = resources.getString(R.string.code_sms_counter_subtitle, 15)
        counter.text = counterText
        val phoneNumber = arguments?.getString(PHONE_NUMBER)
        val shouldShowContactSupport = arguments?.getBoolean(SHOW_CONTACT_SUPPORT, false) ?: false
        val subtitleStr = resources.getString(R.string.verification_code_subtitle, phoneNumber)
        subtitle.text = subtitleStr
        analytics.protectView(subtitle)
        if (shouldShowContactSupport) {
            showContactSupport()
        } else {
            resend.setOnClickListener {
                sendEvent()
                actions?.onBackPressed(1)
            }
        }
        resend.visibility = View.GONE
        progressBar.visibility = View.GONE
        next.isClickable = true
        next.setOnClickListener { onSendCode() }
        back.setOnClickListener { actions?.onBackPressed(1) }
        code_input.addTextChangedListener(object : CustomTextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var c = 0
                while (c < s.length) {
                    inputs[c]?.text = "" + s[c]
                    c++
                }
                while (c < inputs.size) {
                    inputs[c]?.text = ""
                    c++
                }
                next.isEnabled = s.length >= CODE_LENGTH
            }
        })
        timer?.start()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.phone_code_verify_fragment, container, false)
        if (activity is PhoneVerificationUIActions) {
            actions = activity as PhoneVerificationUIActions?
        } else {
            Log.e(TAG, "activity must implements PhoneVerificationActions")
            activity?.finish()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        analytics.logEvent(Events.Analytics.ViewVerificationPage())
    }

    private fun showKeyboardFocusCodeInput(trigger: View?) {
        val handler = Handler()
        handler.postDelayed({
            trigger?.clearFocus()
            code_input.requestFocus()
            val mgr = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.showSoftInput(code_input, InputMethodManager.SHOW_IMPLICIT)
        }, 50)
    }

    private fun sendEvent() {
        val dataStore = dataStoreProvider.dataStore(PhoneVerifyActivity.PHONE_AUTH_DATA_STORE)
        var resendCodeCount = dataStore.getInt(KEY_RESEND_CODE, 0)
        resendCodeCount++
        analytics.logEvent(Events.Analytics.ClickNewCodeLinkOnVerificationPage(resendCodeCount))
        dataStore.putInt(KEY_RESEND_CODE, resendCodeCount)
    }

    fun onSendCode() {
        next.isEnabled = false
        actions?.onSendCode(code_input.text.toString())
        progressBar.visibility = View.VISIBLE
    }

    override fun onDetach() {
        super.onDetach()
        timer?.cancel()
        timer = null
    }

    fun onError() {
        progressBar.visibility = View.GONE
        vibrate()
        animateWiggle()
        code_input.setText("")
        next.isEnabled = false
        codeErrorCount++
        if (codeErrorCount >= MAX_ERROR_COUNT) {
            showContactSupport()
        }
        analytics.logEvent(Events.Analytics.ViewErrorMessageOnVerificationPage())

    }

    private fun showContactSupport() {
        resend.text = resources.getString(R.string.contact_support) + " >"
        resend.setOnClickListener { SupportUtil.openEmailSupport(activity!!, userRepository) }
    }

    private fun animateWiggle() {
        val animShake = AnimationUtils.loadAnimation(activity, R.anim.wiggle)
        for (i in inputs.indices) {
            inputs[i]?.startAnimation(animShake)
        }
        for (i in lines.indices) {
            lines[i]?.startAnimation(animShake)
        }
    }

    private fun vibrate() {
        val v = activity?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(VIBRATE_DURATION)
        }
    }
}
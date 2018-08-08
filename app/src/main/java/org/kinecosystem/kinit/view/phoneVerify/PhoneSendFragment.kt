package org.kinecosystem.kinit.view.phoneVerify


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.phone_send_fragment.*
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.util.PhoneUtils
import org.kinecosystem.kinit.view.BaseFragment
import javax.inject.Inject


class PhoneSendFragment : BaseFragment() {
    @Inject
    lateinit var analytics: Analytics
    private var phoneValid: Boolean = false
    private var prefixValid: Boolean = false
    private var actions: PhoneVerificationUIActions? = null

    companion object {

        val TAG = PhoneSendFragment::class.java.simpleName
        private const val MIN_PHONE_LENGTH = 5
        private const val HAS_BACK = "HAS_BACK"

        @JvmStatic
        fun newInstance(hasBack: Boolean): PhoneSendFragment {
            val fragment = PhoneSendFragment()
            val args = Bundle()
            args.putBoolean(HAS_BACK, hasBack)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        KinitApplication.coreComponent.inject(this)
        super.onCreate(savedInstanceState)
        analytics.protectView(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phone_input.requestFocus()
        next.isEnabled = false
        if (arguments?.getBoolean(HAS_BACK, false) == true) {
            back.visibility = View.VISIBLE
            back.setOnClickListener { actions?.onBackPressed(0) }
        }
        clear.setOnClickListener { phone_input.setText("") }
        val countryZipCode = PhoneUtils.getLocalDialPrefix(activity)
        if (!countryZipCode.isEmpty()) {
            prefix.setText(countryZipCode)
            prefix.keyListener = null
            prefixValid = true
        } else {
            prefix.setText("+")
            prefixValid = false

        }
        next.setOnClickListener { onSendPhone() }
        prefix.addTextChangedListener(object : CustomTextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length >= 2) {
                    error.visibility = View.INVISIBLE
                    prefixValid = true
                } else {
                    error.visibility = View.VISIBLE
                    prefixValid = false
                }
                next.isEnabled = phoneValid && prefixValid
            }
        })
        phone_input.addTextChangedListener(object : CustomTextWatcher {
            override fun afterTextChanged(s: Editable) {
                val length = s.length
                if (length >= 1) {
                    clear.visibility = View.VISIBLE
                } else {
                    clear.visibility = View.INVISIBLE
                }
                phoneValid = length > MIN_PHONE_LENGTH
                next.isEnabled = phoneValid && prefixValid
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.phone_send_fragment, container, false)
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
        analytics.logEvent(Events.Analytics.ViewPhoneAuthPage())
    }

    fun onSendPhone() {
        var prefixStr = prefix.text.toString()
        if (!prefixStr.startsWith("+")) {
            prefixStr = "+$prefixStr"
        }
        val fullPhone = prefixStr + phone_input.text
        actions?.onSendPhone(fullPhone)
        analytics.logEvent(Events.Analytics.ClickNextButtonOnPhoneAuthPage())
    }
}

interface CustomTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable) = Unit
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
}
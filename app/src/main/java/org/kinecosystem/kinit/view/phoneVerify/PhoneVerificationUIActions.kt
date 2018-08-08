package org.kinecosystem.kinit.view.phoneVerify

interface PhoneVerificationUIActions {

    fun onSendPhone(phoneNumber: String)

    fun onSendCode(code: String)

    fun onVerificationComplete()

    fun onBackPressed(fromPage: Int)
}

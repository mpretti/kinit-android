package org.kinecosystem.kinit.view.backup

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import org.kinecosystem.kinit.view.SingleFragmentActivity
import org.kinecosystem.kinit.viewmodel.Backup.BackUpModel

class WalletBackupActivity : SingleFragmentActivity(), BackUpActions {

    private var model: BackUpModel = BackUpModel()
    private var questionIndex = 0

    override fun getBackUpModel(): BackUpModel {
        return model
    }

    override fun onNext() {
        questionIndex++
        updateQuestionFragment()
        model.onNext()
    }

    private fun updateQuestionFragment(){
        replaceFragment(BackupSecurityQuestionFragment.newInstance(questionIndex), true)
    }

    override fun init() {
        model.initHints()
    }

    override fun getFragment(): Fragment {
        return WelcomeBackupFragment.newInstance()
    }


    override fun onStartBackup() {
        updateQuestionFragment()
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, WalletBackupActivity::class.java)
    }
}

interface BackUpActions {
    fun onStartBackup()
    fun onNext()
    fun getBackUpModel():BackUpModel
}
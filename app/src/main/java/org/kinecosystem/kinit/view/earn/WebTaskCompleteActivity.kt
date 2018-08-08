package org.kinecosystem.kinit.view.earn

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import org.kinecosystem.kinit.view.SingleFragmentActivity
import org.kinecosystem.kinit.view.earn.TaskErrorFragment.ERROR_TRANSACTION

class WebTaskCompleteActivity : SingleFragmentActivity(), WebTaskCompleteFragment.TaskCompleteListener, TransactionActions {

    private val taskWebCompleteFragment: WebTaskCompleteFragment = WebTaskCompleteFragment.newInstance()

    companion object {
        fun getIntent(context: Context) = Intent(context, WebTaskCompleteActivity::class.java)
    }

    init {
        taskWebCompleteFragment.listener = this
    }

    override fun getFragment(): Fragment = taskWebCompleteFragment
    override fun transactionError() = replaceFragment(TaskErrorFragment.newInstance(ERROR_TRANSACTION))
    override fun onAnimationComplete() = replaceFragment(TaskRewardFragment.newInstance())

}
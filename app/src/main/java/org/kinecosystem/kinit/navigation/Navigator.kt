package org.kinecosystem.kinit.navigation

import android.content.Context
import android.support.v7.app.AppCompatActivity
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.model.earn.isTaskWebView
import org.kinecosystem.kinit.model.spend.Offer
import org.kinecosystem.kinit.repository.TasksRepository
import org.kinecosystem.kinit.view.MainActivity
import org.kinecosystem.kinit.view.earn.QuestionnaireActivity
import org.kinecosystem.kinit.view.earn.WebTaskActivity
import org.kinecosystem.kinit.view.earn.WebTaskCompleteActivity
import org.kinecosystem.kinit.view.spend.Peer2PeerActivity
import org.kinecosystem.kinit.view.spend.PurchaseOfferActivity
import javax.inject.Inject

class Navigator(private val context: Context) {

    enum class Destination {
        TASK, MAIN_SCREEN, SPEND, PEER2PEER, COMPLETE_WEB_TASK
    }

    @Inject
    lateinit var tasksRepository: TasksRepository

    init {
        KinitApplication.coreComponent.inject(this)
    }

    fun navigateTo(dest: Destination) {
        navigateTo(dest, 0)
    }

    fun navigateTo(offer: Offer) {
        navigateToSpend(offer)
    }

    fun navigateTo(dest: Destination, index: Int = 0) {
        when (dest) {
            Destination.TASK -> navigateToTask()
            Destination.PEER2PEER -> navigateToPeer2Peer()
            Destination.COMPLETE_WEB_TASK -> navigateToCompleteWebTask()
            else -> navigateToMainScreen()
        }
    }

    private fun navigateToSpend(offer: Offer) {
        context.startActivity(PurchaseOfferActivity.getIntent(context, offer))
        if (context is AppCompatActivity) {
            context.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
        }
    }

    private fun navigateToCompleteWebTask() {
        context.startActivity(WebTaskCompleteActivity.getIntent(context))
        if (context is AppCompatActivity) {
            context.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
        }
    }

    private fun navigateToTask() {
        tasksRepository.task?.let {
            if (it.isTaskWebView()) {
                context.startActivity(WebTaskActivity.getIntent(context))
            } else {
                context.startActivity(QuestionnaireActivity.getIntent(context))
            }
        }
        if (context is AppCompatActivity) {
            context.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
        }
    }

    private fun navigateToPeer2Peer() {
        context.startActivity(Peer2PeerActivity.getIntent(context))
        if (context is AppCompatActivity) {
            context.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
        }
    }

    private fun navigateToMainScreen() {
        context.startActivity(MainActivity.getIntent(context))
    }
}
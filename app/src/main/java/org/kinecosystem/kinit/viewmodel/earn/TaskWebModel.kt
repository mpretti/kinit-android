package org.kinecosystem.kinit.viewmodel.earn

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.gson.JsonElement
import org.kinecosystem.kinit.BuildConfig
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.navigation.Navigator
import org.kinecosystem.kinit.network.OperationResultCallback
import org.kinecosystem.kinit.network.TaskService
import org.kinecosystem.kinit.network.Wallet
import org.kinecosystem.kinit.repository.QuestionnaireRepository
import org.kinecosystem.kinit.repository.UserRepository
import javax.inject.Inject

abstract class WebModel(val activity: FragmentActivity, val navigator: Navigator) {

    abstract var interfaceName: String
    abstract var url: String

    @Inject
    lateinit var questionnaireRepository: QuestionnaireRepository
    @Inject
    lateinit var wallet: Wallet

    init {
        KinitApplication.coreComponent.inject(this)
        wallet.onEarnTransactionCompleted.set(false)
    }

    fun startListenToPayment() {
        wallet.listenToPayment(questionnaireRepository.task?.memo!!)
    }

    fun onComplete() {
        navigator.navigateTo(Navigator.Destination.COMPLETE_WEB_TASK)
        activity.finish()
    }
}

class TrueXModel(activity: FragmentActivity, navigator: Navigator) : WebModel(activity, navigator) {

    @Inject
    lateinit var taskService: TaskService
    @Inject
    lateinit var userRepository: UserRepository
    val TRUEX_HASH: String = if (BuildConfig.DEBUG) BuildConfig.truexHashStage else BuildConfig.truexHashProd

    override var url = "file:///android_asset/truex.html"
    override var interfaceName: String = "Kinit"
    val loading: ObservableBoolean = ObservableBoolean(true)
    val javascript: ObservableField<String> = ObservableField("")

    init {
        KinitApplication.coreComponent.inject(this)
    }

    @JavascriptInterface
    fun updateStatus(status: String) {
        Log.d("###", "### got web status " + status)
    }

    @JavascriptInterface
    fun onCredit() {
        Log.d("###", "### got web credit ")
        startListenToPayment()
    }

    @JavascriptInterface
    fun onFinish() {
        Log.d("###", "### got web finish ")
        onComplete()
    }

    fun loadData() {
        taskService.getTrueXTask(object : OperationResultCallback<JsonElement?> {
            override fun onResult(json: JsonElement?) {
                javascript.set(
                    "updateTruexActivityData('${userRepository.userInfo.userId}', '${json.toString()}', '$TRUEX_HASH');")
                loading.set(false)
                //loading javascript done by binding
            }

            override fun onError(errorCode: Int) {
                Log.d("####", "#### task web model  getTrueXTask    error")
            }

        })
    }
}
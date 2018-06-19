package dagger

import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.network.ServicesProvider
import org.kinecosystem.kinit.network.firebase.KinInstanceIdService
import org.kinecosystem.kinit.network.firebase.KinMessagingService
import org.kinecosystem.kinit.repository.OffersRepository
import org.kinecosystem.kinit.repository.QuestionnaireRepository
import org.kinecosystem.kinit.view.BottomTabNavigation
import org.kinecosystem.kinit.view.MainActivity
import org.kinecosystem.kinit.view.SplashActivity
import org.kinecosystem.kinit.view.TabsAdapter
import org.kinecosystem.kinit.view.adapter.CouponsListAdapter
import org.kinecosystem.kinit.view.adapter.TransactionsListAdapter
import org.kinecosystem.kinit.view.customView.TransactionLayoutView
import org.kinecosystem.kinit.view.earn.QuestionnaireActivity
import org.kinecosystem.kinit.view.earn.TaskErrorFragment
import org.kinecosystem.kinit.view.phoneVerify.CodeVerificationFragment
import org.kinecosystem.kinit.view.phoneVerify.PhoneAuthCompleteFragment
import org.kinecosystem.kinit.view.phoneVerify.PhoneSendFragment
import org.kinecosystem.kinit.view.phoneVerify.PhoneVerifyActivity
import org.kinecosystem.kinit.view.spend.PurchaseOfferFragment
import org.kinecosystem.kinit.view.tutorial.TutorialActivity
import org.kinecosystem.kinit.viewmodel.PhoneVerificationViewModel
import org.kinecosystem.kinit.viewmodel.SplashViewModel
import org.kinecosystem.kinit.viewmodel.balance.BalanceViewModel
import org.kinecosystem.kinit.viewmodel.earn.QuestionViewModel
import org.kinecosystem.kinit.viewmodel.earn.QuestionnaireCompleteViewModel
import org.kinecosystem.kinit.viewmodel.earn.QuestionnaireRewardViewModel
import org.kinecosystem.kinit.viewmodel.earn.QuestionnaireViewModel
import org.kinecosystem.kinit.viewmodel.info.InfoViewModel
import org.kinecosystem.kinit.viewmodel.spend.Peer2PeerViewModel
import org.kinecosystem.kinit.viewmodel.spend.PurchaseOfferViewModel
import org.kinecosystem.kinit.viewmodel.spend.SpendViewModel
import javax.inject.Singleton

@Singleton
@Component(
    modules = [(ContextModule::class), (UserRepositoryModule::class), (QuestionnaireRepositoryModule::class), (OffersRepositoryModule::class), (AnalyticsModule::class), (NotificationModule::class), (DataStoreProviderModule::class), (ServicesProviderModule::class)])
interface CoreComponent {

    fun inject(balanceViewModel: BalanceViewModel)
    fun inject(spendViewModel: SpendViewModel)
    fun inject(tabsAdapter: TabsAdapter)
    fun inject(transactionsListAdapter: TransactionsListAdapter)
    fun inject(couponsListAdapter: CouponsListAdapter)
    fun inject(purchaseOfferViewModel: PurchaseOfferViewModel)
    fun inject(splashViewModel: SplashViewModel)
    fun inject(questionnaireViewModel: QuestionnaireViewModel)
    fun inject(questionnaireCompleteViewModel: QuestionnaireCompleteViewModel)
    fun inject(questionnaireRewardViewModel: QuestionnaireRewardViewModel)
    fun inject(questionViewModel: QuestionViewModel)
    fun inject(peer2PeerViewModel: Peer2PeerViewModel)
    fun inject(phoneVerificationViewModel: PhoneVerificationViewModel)
    fun inject(kinMessagingService: KinMessagingService)
    fun inject(infoViewModel: InfoViewModel)
    fun inject(kinInstanceIdService: KinInstanceIdService)
    fun inject(tutorialActivity: TutorialActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(phoneVerifyActivity: PhoneVerifyActivity)
    fun inject(phoneSendFragment: PhoneSendFragment)
    fun inject(taskErrorFragment: TaskErrorFragment)
    fun inject(codeVerificationFragment: CodeVerificationFragment)
    fun inject(phoneAuthCompleteFragment: PhoneAuthCompleteFragment)
    fun inject(questionnaireActivity: QuestionnaireActivity)
    fun inject(app: KinitApplication)
    fun inject(splashActivity: SplashActivity)
    fun inject(transactionLayoutView: TransactionLayoutView)
    fun inject(bottomTabNavigation: BottomTabNavigation)


}
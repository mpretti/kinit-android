package org.kinecosystem.kinit.viewmodel.earn

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.text.format.DateUtils.DAY_IN_MILLIS
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.model.earn.Task
import org.kinecosystem.kinit.model.earn.startDateInMillis
import org.kinecosystem.kinit.model.earn.tagsString
import org.kinecosystem.kinit.navigation.Navigator
import org.kinecosystem.kinit.network.OperationCompletionCallback
import org.kinecosystem.kinit.network.TaskService
import org.kinecosystem.kinit.network.Wallet
import org.kinecosystem.kinit.repository.TasksRepository
import org.kinecosystem.kinit.util.Scheduler
import org.kinecosystem.kinit.util.TimeUtils
import org.kinecosystem.kinit.view.TabViewModel
import java.text.SimpleDateFormat
import java.util.*

private const val AVAILABILITY_DATE_FORMAT = "MMM dd"

class EarnViewModel(val taskRepository: TasksRepository, val wallet: Wallet,
    val taskService: TaskService, val scheduler: Scheduler, val analytics: Analytics,
    private val navigator: Navigator) :
    TabViewModel {

    var shouldShowTask = ObservableBoolean()
    var shouldShowTaskNotAvailableYet = ObservableBoolean()
    var shouldShowNoTask = ObservableBoolean(false)
    var isTaskStarted: ObservableBoolean
    var nextAvailableDate: ObservableField<String> = ObservableField("")
    var isAvailableTomorrow: ObservableBoolean = ObservableBoolean(false)
    var balance: ObservableField<String>
    var authorName = ObservableField<String>()
    var authorImageUrl = ObservableField<String?>()
    var title = ObservableField<String?>()
    var description = ObservableField<String?>()
    var kinReward = ObservableField<String>()
    var minToComplete = ObservableField<String>()

    private var scheduledRunnable: Runnable? = null

    init {
        balance = wallet.balance
        isTaskStarted = taskRepository.isTaskStarted
        refresh()
    }

    fun startTask() {
        val task = taskRepository.task
        val bEvent = Events.Business.EarningTaskStarted(
            task?.provider?.name,
            task?.minToComplete,
            task?.kinReward,
            task?.tagsString(),
            task?.id,
            task?.title,
            task?.type)
        analytics.logEvent(bEvent)

        val aEvent = Events.Analytics.ClickStartButtonOnTaskPage(
            isTaskStarted.get(),
            task?.provider?.name,
            task?.minToComplete,
            task?.kinReward,
            task?.tagsString(),
            task?.id,
            task?.title,
            task?.type)
        analytics.logEvent(aEvent)
        navigator.navigateTo(Navigator.Destination.TASK)
    }

    private fun refresh() {
        if(!isTaskStarted.get()){
                    val task = taskRepository.task
                    authorName.set(task?.provider?.name)
                    authorImageUrl.set(task?.provider?.imageUrl)
                    title.set(task?.title)
                    description.set(task?.description)
                    kinReward.set(task?.kinReward?.toString())
                    minToComplete.set(convertMinToCompleteToString(task?.minToComplete))
                    handleAvailability()
                }
    }

    private fun handleAvailability() {
        if (scheduledRunnable != null) {
            scheduler.cancel(scheduledRunnable)
        }

        if (taskRepository.task == null) {
            shouldShowNoTask.set(true)
            shouldShowTask.set(false)
            shouldShowTaskNotAvailableYet.set(false)
        } else {
            val taskAvailable = isTaskAvailable()
            shouldShowTask.set(taskAvailable)
            shouldShowTaskNotAvailableYet.set(!taskAvailable)
            shouldShowNoTask.set(false)

            if (!shouldShowTask.get()) {

                nextAvailableDate.set(nextAvailableDate())
                isAvailableTomorrow.set(isAvailableTomorrow())
                if (isAvailableTomorrow.get()) {
                    val diff = taskRepository.task?.startDateInMillis()!! - scheduler.currentTimeMillis()
                    scheduledRunnable = Runnable {
                        shouldShowTask.set(true)
                        shouldShowTaskNotAvailableYet.set(false)
                    }
                    scheduler.scheduleOnMain(scheduledRunnable, diff)
                } else {
                    scheduledRunnable = Runnable { handleAvailability() }
                    scheduler.scheduleOnMain(scheduledRunnable, DAY_IN_MILLIS)
                }
            }
        }
    }

    private fun isTaskAvailable(): Boolean {
        val taskDate: Long = taskRepository.task?.startDateInMillis() ?: scheduler.currentTimeMillis()
        return scheduler.currentTimeMillis() >= taskDate
    }

    private fun isAvailableTomorrow(): Boolean {
        return timeToUnlockInDays(taskRepository.task) == 1
    }

    private fun timeToUnlockInDays(task: Task?): Int {
        val millisAtNextMidnight = TimeUtils.millisAtNextMidnight(scheduler.currentTimeMillis())
        val startDate = task?.startDateInMillis() ?: scheduler.currentTimeMillis()

        return (1 + ((startDate - millisAtNextMidnight) / DAY_IN_MILLIS)).toInt()
    }

    fun nextAvailableDate(): String {
        val dateInMillis = taskRepository.task?.startDateInMillis() ?: 0
        return SimpleDateFormat(AVAILABILITY_DATE_FORMAT).format(Date(dateInMillis))
    }

    override fun onScreenVisibleToUser() {
        refresh()
        when {
            shouldShowTask.get() -> onEarnScreenVisible()
            shouldShowNoTask.get() -> onNoTasksAvailableVisible()
            else -> onLockedScreenVisible()
        }
        checkForUpdates()
    }

    private fun checkForUpdates() {
        val currentLastUpdatedTime = taskRepository.task?.lastUpdatedAt ?: 0
        val currentTaskDisplayedId = taskRepository.task?.id.orEmpty()

        taskService.retrieveNextTask(object : OperationCompletionCallback {
            override fun onError(errorCode: Int) {
            }

            override fun onSuccess() {
                if (taskRepository.task != null) {
                    taskRepository.task?.lastUpdatedAt?.let {
                        if (it > currentLastUpdatedTime ||
                            currentTaskDisplayedId != taskRepository.task?.id.orEmpty()) {
                            refresh()
                        }
                    }
                }
            }

        })
    }

    private fun onEarnScreenVisible() {
        val task = taskRepository.task
        val event = Events.Analytics.ViewTaskPage(task?.provider?.name,
            task?.minToComplete,
            task?.kinReward,
            task?.tagsString(),
            task?.id,
            task?.title,
            task?.type)
        analytics.logEvent(event)
    }

    private fun onLockedScreenVisible() {
        val task = taskRepository.task
        val timeToUnlockInDays = timeToUnlockInDays(task)

        val event = Events.Analytics.ViewLockedTaskPage(timeToUnlockInDays)
        analytics.logEvent(event)
    }

    private fun onNoTasksAvailableVisible() {
        analytics.logEvent(Events.Analytics.ViewEmptyStatePage(Analytics.MENU_ITEM_NAME_EARN))
    }

    private fun convertMinToCompleteToString(minToComplete: Float?): String =
        when {
            minToComplete == null -> "0"
            (minToComplete * 10).toInt() % 10 == 0 -> minToComplete.toInt().toString()
            else -> minToComplete.toString()
        }
}


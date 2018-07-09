package org.kinecosystem.kinit.viewmodel.earn

import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.v4.app.Fragment
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.model.TaskState
import org.kinecosystem.kinit.model.earn.Task
import org.kinecosystem.kinit.model.earn.isTypeDualImage
import org.kinecosystem.kinit.model.earn.tagsString
import org.kinecosystem.kinit.repository.TasksRepository
import org.kinecosystem.kinit.view.earn.*
import javax.inject.Inject

private const val NEXT_QUESTION_PAGE = 0
private const val QUESTIONNAIRE_COMPLETE_PAGE = 1
private const val REWARD_PAGE = 2
private const val SUBMIT_ERROR_PAGE = 3
private const val TRANSACTION_ERROR_PAGE = 4


class QuestionnaireViewModel(restoreState: Boolean) :
        QuestionnaireActions {

    @Inject
    lateinit var questionnaireRepository: TasksRepository
    @Inject
    lateinit var analytics: Analytics

    private var task: Task?
    var questionnaireProgress: ObservableInt = ObservableInt()
    var nextFragment: ObservableField<Fragment> = ObservableField()
    var currentPage: Int

    init {
        KinitApplication.coreComponent.inject(this)
        task = questionnaireRepository.task
        currentPage =
                when {
                    restoreState -> getPageFromState()
                    !questionnaireRepository.isTaskComplete() -> NEXT_QUESTION_PAGE
                    else -> QUESTIONNAIRE_COMPLETE_PAGE
                }
        moveToNextPage(currentPage)
    }

    override fun nextQuestion() {
        moveToNextPage(
                if (!questionnaireRepository.isTaskComplete()) {
                    NEXT_QUESTION_PAGE
                } else QUESTIONNAIRE_COMPLETE_PAGE
        )
    }

    override fun transactionError() {
        moveToNextPage(TRANSACTION_ERROR_PAGE)
    }

    override fun submissionError() {
        moveToNextPage(SUBMIT_ERROR_PAGE)
    }

    override fun submissionComplete() {
        moveToNextPage(REWARD_PAGE)
    }

    private fun nextQuestionIndex(): Int {
        return questionnaireRepository.getNumOfAnsweredQuestions()
    }

    private fun moveToNextPage(page: Int) {
        currentPage = page
        questionnaireProgress.set(
                ((questionnaireRepository.getNumOfAnsweredQuestions().toDouble() / task?.questions!!.size) * 100).toInt())
        nextFragment.set(getFragment())
    }

    private fun getFragment(): Fragment {

        return when (currentPage) {
            NEXT_QUESTION_PAGE -> {
                if (questionnaireRepository.task?.questions?.get(nextQuestionIndex())?.isTypeDualImage()!!) {
                    QuestionDualImageFragment.newInstance(nextQuestionIndex())
                } else {
                    QuestionFragment.newInstance(nextQuestionIndex())
                }
            }
            QUESTIONNAIRE_COMPLETE_PAGE -> QuestionnaireCompleteFragment.newInstance()
            REWARD_PAGE -> TaskRewardFragment.newInstance()
            TRANSACTION_ERROR_PAGE -> TaskErrorFragment.newInstance(
                    TaskErrorFragment.ERROR_TRANSACTION)
            else -> TaskErrorFragment.newInstance(TaskErrorFragment.ERROR_SUBMIT)
        }
    }

    fun onBackPressed() {
        val event: Events.Event
        if (questionnaireRepository.isTaskComplete()) {
            event = Events.Analytics.ClickCloseButtonOnRewardPage(task?.provider?.name,
                    task?.minToComplete,
                    task?.kinReward,
                    task?.tagsString(),
                    task?.id,
                    task?.title,
                    task?.type)
        } else {
            val question = task?.questions?.get(nextQuestionIndex())
            event = Events.Analytics.ClickCloseButtonOnQuestionPage(task?.provider?.name,
                    task?.minToComplete,
                    task?.kinReward,
                    question?.answers?.count(),
                    task?.questions?.count(),
                    question?.id,
                    nextQuestionIndex() + 1,
                    question?.type,
                    task?.tagsString(),
                    task?.id,
                    task?.title)
        }
        analytics.logEvent(event)
    }


    private fun getPageFromState(): Int {
        return when {
            questionnaireRepository.taskState == TaskState.SUBMITTED_SUCCESS_WAIT_FOR_REWARD -> REWARD_PAGE
            questionnaireRepository.taskState == TaskState.TRANSACTION_COMPLETED -> REWARD_PAGE
            questionnaireRepository.taskState == TaskState.TRANSACTION_ERROR -> TRANSACTION_ERROR_PAGE
            questionnaireRepository.taskState == TaskState.SUBMIT_ERROR_RETRY -> QUESTIONNAIRE_COMPLETE_PAGE
            questionnaireRepository.taskState == TaskState.SUBMIT_ERROR_NO_RETRY -> SUBMIT_ERROR_PAGE
            else -> {
                if (!questionnaireRepository.isTaskComplete())
                    NEXT_QUESTION_PAGE
                else QUESTIONNAIRE_COMPLETE_PAGE
            }
        }
    }
}

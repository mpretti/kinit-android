package org.kinecosystem.kinit.repository

import android.content.Context
import android.databinding.ObservableBoolean
import android.util.Log
import com.google.gson.Gson
import org.kinecosystem.kinit.model.TaskState
import org.kinecosystem.kinit.model.earn.*
import org.kinecosystem.kinit.util.ImageUtils

private const val QUESTIONNAIRE_ANSWERS_STORAGE = "kin.app.task.chosen.answers"
private const val TASK_STORAGE = "kin.app.task"
private const val TASK_KEY = "task"
private const val TASK_STATE_KEY = "task_state"

class TasksRepository(dataStoreProvider: DataStoreProvider, defaultTask: String? = null) {
    var task: Task?
        private set
    private var chosenAnswers: ArrayList<ChosenAnswers> = ArrayList()
    private val chosenAnswersCache: DataStore
    private val taskCache: DataStore = dataStoreProvider.dataStore(TASK_STORAGE)
    private val taskStorageName: String
    var isTaskStarted: ObservableBoolean
    var taskState: Int
        set(state) {
            Log.d("TasksRepository", "setting task state to $state")
            taskCache.putInt(TASK_STATE_KEY, state)
        }
        get() {
            val sss= taskCache.getInt(TASK_STATE_KEY, TaskState.IDLE)
            Log.d("TasksRepository", "getting task state $sss")
            return this.taskCache.getInt(TASK_STATE_KEY, TaskState.IDLE)
        }


    init {
        task = getCachedTask(defaultTask)
        Log.d("###", "#### TasksRepository  getCachedTask(defaultTask) $task")
        taskStorageName = QUESTIONNAIRE_ANSWERS_STORAGE + task?.id
        Log.d("###", "#### TasksRepository taskStorageName $taskStorageName init chosenAnswersCache ")
        chosenAnswersCache = dataStoreProvider.dataStore(taskStorageName)

        isTaskStarted = ObservableBoolean(taskState != TaskState.IDLE)
    }

    fun resetTaskState() {
        taskState = TaskState.IDLE
    }

    private fun getCachedTask(defaultTask: String?): Task? {
        val cachedTask = taskCache.getString(TASK_KEY, defaultTask)
        return Gson().fromJson(cachedTask, Task::class.java)
    }

    fun setChosenAnswers(questionId: String, answersIds: List<String>) {
        chosenAnswers.add(ChosenAnswers(questionId, answersIds))

        chosenAnswersCache.putStringList(questionId, answersIds)
        Log.d("###", "#### TasksRepository chosenAnswersCache putStringList $questionId init $answersIds ")
        isTaskStarted.set(true)
    }

    fun getChosenAnswersByQuestionId(questionId: String): List<String> = chosenAnswersCache.getStringList(questionId,
        listOf())

    fun getNumOfAnsweredQuestions(): Int {
        return getChosenAnswers().size
    }

    fun isTaskComplete(): Boolean {
        return task?.questions?.size == getNumOfAnsweredQuestions()
    }

    fun getChosenAnswers(): ArrayList<ChosenAnswers> {
        if (chosenAnswers.isEmpty()) {
            val answersMap = chosenAnswersCache.getAll()
            Log.d("###", "#### TasksRepository getChosenAnswers chosenAnswersCache answersMap $answersMap ")
            for (answers in answersMap) {
                if (answers.value is String) {
                    chosenAnswers.add(ChosenAnswers(answers.key, listOf(answers.value as String)))
                } else if (answers.value is HashSet<*>) {
                    chosenAnswers.add(ChosenAnswers(answers.key, (answers.value as HashSet<String>).toList()))
                }
            }
        }
        Log.d("###", "#### TasksRepository getChosenAnswers chosenAnswersCache new chosenAnswers $chosenAnswers ")

        return chosenAnswers
    }

    fun isTaskAvailable(): Boolean {
        if (task == null) return false
        val taskDate: Long = task?.startDateInMillis() ?: System.currentTimeMillis()
        return System.currentTimeMillis() >= taskDate
    }

    fun clearChosenAnswers() {
        chosenAnswers.clear()
        chosenAnswersCache.clearAll()
        isTaskStarted.set(false)
        Log.d("###", "#### TasksRepository  chosenAnswersCache.clearAll() $chosenAnswersCache ")
    }

    fun replaceTask(task: Task?, applicationContext: Context) {
        Log.d("###", "#### TasksRepository  replace RRRRRRR tasks.clearAll() $chosenAnswersCache ")
        clearChosenAnswers()
        Log.d("###", "#### TasksRepository  replaceRRRRRR  tasks.clearAll() $chosenAnswersCache ")
        this.task = task
        if (task != null) {
            taskCache.putString(TASK_KEY, Gson().toJson(task))
            Log.d("###", "#### TasksRepository  taskCache.putString(TASK_KEY, Gson().toJson(task)) ")

            for (question: Question in task.questions.orEmpty()) {
                if (question.hasImages()) {
                    ImageUtils.fetchImages(applicationContext, question.getImagesUrls())
                }
            }
        } else {
            taskCache.clear(TASK_KEY)
        }
    }
}


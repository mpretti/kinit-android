package org.kinecosystem.kinit.model.earn

import com.google.gson.annotations.SerializedName
import org.kinecosystem.kinit.model.Provider
import org.kinecosystem.kinit.model.isValid

const val TASK_TYPE_QUESTIONNAIRE: String = "questionnaire"
const val TASK_TYPE_QUIZ: String = "quiz"
const val TASK_TYPE_TRUEX: String = "truex"


data class Task(
        @SerializedName("id")
        val id: String? = null,
        @SerializedName("memo")
        val memo: String? = null,
        @SerializedName("title")
        val title: String? = null,
        @SerializedName("desc")
        val description: String? = null,
        @SerializedName("price")
        val kinReward: Int? = null,
        @SerializedName("start_date")
        val startDateInSeconds: Long? = 0,
        @SerializedName("min_to_complete")
        val minToComplete: Float? = null,
        @SerializedName("tags")
        val tags: List<String>? = null,
        @SerializedName("provider")
        val provider: Provider? = null,
        @SerializedName("type")
        val type: String? = null,
        @SerializedName("updated_at")
        val lastUpdatedAt: Long? = null,
        @SerializedName("items")
        val questions: List<Question>? = null) {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Task) return false
        if (id != other.id) return false
        if (memo != other.memo) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (kinReward != other.kinReward) return false
        if (minToComplete != other.minToComplete) return false
        if (tags != other.tags) return false
        if (provider != other.provider) return false
        if (type != other.type) return false
        if (lastUpdatedAt != other.lastUpdatedAt) return false
        if (questions != other.questions) return false
        return true
    }
}

fun Task.isValid(): Boolean {
    if (id.isNullOrBlank() || title.isNullOrBlank() || description.isNullOrBlank() || type.isNullOrBlank() || memo.isNullOrBlank() ||
            kinReward == null || minToComplete == null || provider == null || questions == null || startDateInSeconds == null || lastUpdatedAt == null) {
        return false
    }
    if (isQuiz()) {
        if (questions.isEmpty() || questions.any { question -> question.quiz_data == null })
            return false
    }
    if (!questions.orEmpty().all { question -> question.isValid() }) {
        return false
    }
    return provider.isValid()
}

fun Task.isQuestionnaire(): Boolean = type.equals(TASK_TYPE_QUESTIONNAIRE)

fun Task.isQuiz(): Boolean = type.equals(TASK_TYPE_QUIZ)

fun Task.isTaskWebView(): Boolean = type.equals(TASK_TYPE_TRUEX)

fun Task.tagsString(): String? {
    return tags?.joinToString(", ")
}

fun Task.startDateInMillis(): Long? {
    return startDateInSeconds?.times(1000)
}

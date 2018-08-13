package org.kinecosystem.kinit.view.backup

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.choose_backup_question_.*
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.databinding.ChooseBackupQuestionBinding
import org.kinecosystem.kinit.network.BackupApi
import org.kinecosystem.kinit.view.BaseFragment


class BackupSecurityQuestionFragment : BaseFragment() {
    companion object {
        private val QUESTION_INDEX = "QUESTION_INDEX"
        private val INVALID_INDEX = -1

        fun newInstance(questionIndex: Int): Fragment {
            var fragment = BackupSecurityQuestionFragment()
            var args = Bundle()
            args.putInt(QUESTION_INDEX, questionIndex)
            fragment.arguments = args
            return fragment
        }
    }

    var questionIndex: Int = INVALID_INDEX

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questions.adapter = HintSpinnerAdapter(activity?.applicationContext,  (activity as BackUpActions).getBackUpModel().getHints())
        info.text = "Question:" + questionIndex
        //item selected listener for spinner
        questions.onItemSelectedListener =  (activity as BackUpActions).getBackUpModel()

        nextBtn.setOnClickListener {
            activity?.let {
                (it as BackUpActions).onNext()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (activity !is BackUpActions) {
            Log.e("BackupSecurityQ", "Activity must implement BackUpActions")
            activity?.finish()
        }
        arguments?.let {
            questionIndex = it.getInt(QUESTION_INDEX, INVALID_INDEX)
        }
        if (questionIndex == INVALID_INDEX) {
            Log.e("BackupSecurityQ", "BackupSecurityQuestionFragment no question index")
            activity?.finish()
        }
        val binding = DataBindingUtil.inflate<ChooseBackupQuestionBinding>(inflater, R.layout.choose_backup_question_, container, false)
        binding.model = (activity as BackUpActions).getBackUpModel()
        return binding.root
    }
}

class HintSpinnerAdapter(val context: Context?, val list: List<BackupApi.BackUpHint>) : BaseAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var hintsList:MutableList<BackupApi.BackUpHint> = list.toMutableList()

    init {
        val title = context?.resources?.getString(R.string.choose_hint)
        title?.let {
            hintsList.add(0, BackupApi.BackUpHint(-1, it))
        }
    }

    override fun getView(index: Int, p1: View?, p2: ViewGroup?): View {
        val view = inflater.inflate(R.layout.hint_question_spinner_item_layout, null)
        view.findViewById<TextView>(R.id.question).text = hintsList[index].hint
        return view
    }

    override fun getItem(index: Int): Any {
        return hintsList[index]
    }

    override fun getItemId(index: Int): Long {
        return hintsList[index].id.toLong()
    }

    override fun getCount(): Int {
        return hintsList.size
    }

}
package org.kinecosystem.kinit.viewmodel.Backup

import android.databinding.ObservableBoolean
import android.util.Log
import android.view.View
import android.widget.AdapterView
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.network.ServicesProvider
import org.kinecosystem.kinit.repository.UserRepository
import javax.inject.Inject

class BackUpModel : AdapterView.OnItemSelectedListener {

    var isQuestionSelected = ObservableBoolean(false)

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(adpater: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
        val hint = userRepository.backUpHints[index]
        Log.d("###", "### on selected $hint")
        if(index == 0){
            isQuestionSelected.set(false)
        }
        else isQuestionSelected.set(true)
    }



    @Inject
    lateinit var servicesProvider: ServicesProvider

    @Inject
    lateinit var userRepository: UserRepository


    init {
        KinitApplication.coreComponent.inject(this)
    }

    fun initHints() {
        servicesProvider.backupService.initHints()
    }

    fun onNext() {
        //servicesProvider.backupService.updateHints(listOf(3,4))
        //servicesProvider.backupService.updateBackupDataTo("shay.baz@kik.com", "Mary Had a Little Lamb")
    }

    fun getHints() = userRepository.backUpHints

}
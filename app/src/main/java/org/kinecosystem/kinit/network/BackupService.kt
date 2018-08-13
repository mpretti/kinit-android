package org.kinecosystem.kinit.network

import android.content.Context
import android.util.Log
import org.kinecosystem.kinit.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BackupService(val applicationContext: Context, val userRepo: UserRepository, val server: BackupApi) {


    fun initHints(){
        server.getHints().enqueue(object : Callback<BackupApi.HintsResponse>{
            override fun onResponse(call: Call<BackupApi.HintsResponse>?, response: Response<BackupApi.HintsResponse>?) {
                if(response != null && response.isSuccessful){
                    response.body()?.let {
                        userRepo.backUpHints =  it.hints
                        Log.d("####", "#### got hindtsss userss 0000" + userRepo.backUpHints[0])
                        Log.d("####", "#### got hindtsss userss11111 " + userRepo.backUpHints[1])
                    }
                }
            }

            override fun onFailure(call: Call<BackupApi.HintsResponse>?, t: Throwable?) {
                userRepo.backUpHints = listOf()
            }

        })
    }

    fun updateHints(hintsIds: List<Int>){
        server.updateHints(userRepo.userId(), userRepo.authToken, BackupApi.Hints(hintsIds)).enqueue(object : Callback<BackupApi.StatusResponse>{
            override fun onFailure(call: Call<BackupApi.StatusResponse>?, t: Throwable?) {
                Log.d("###", "####updateHints on fail ")
            }

            override fun onResponse(call: Call<BackupApi.StatusResponse>?, response: Response<BackupApi.StatusResponse>?) {
                Log.d("###", "####updateHints on repos " + response!!.body())
            }

        })
    }

    fun updateBackupDataTo(emailAddress:String, key:String){
        server.sendBackUpEmail(userRepo.userId(), userRepo.authToken, BackupApi.BackupData(emailAddress, key)).enqueue(object : Callback<BackupApi.StatusResponse>{
            override fun onFailure(call: Call<BackupApi.StatusResponse>?, t: Throwable?) {
                Log.d("###", "####update Data on fail ")
            }

            override fun onResponse(call: Call<BackupApi.StatusResponse>?, response: Response<BackupApi.StatusResponse>?) {
                Log.d("###", "####update Data  on  " + response!!.body())
            }

        })
    }

}
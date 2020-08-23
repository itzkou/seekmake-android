package com.kou.seekmake.data.retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonSyntaxException
import com.kou.seekmake.models.SeekMake.*
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class SeekMakeRepository {
    fun signUp(api: SeekMakeApi, userSeek: UserSeek): LiveData<SignUpResponse> {
        val apiresp = MutableLiveData<SignUpResponse>()

        api.signUp(userSeek).enqueue(object : Callback<SignUpResponse> {
            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(SignUpResponse(null, "0"))
                else if (t is JsonSyntaxException)
                    apiresp.postValue(SignUpResponse(null, "2"))

            }

            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                when {
                    response.code() == 200 -> apiresp.postValue(response.body())
                    response.code() == 422 -> {
                        apiresp.postValue(SignUpResponse(null, "1"))
                    }
                }
            }
        })

        return apiresp
    }

    fun signIn(api: SeekMakeApi, userSeek: UserSeek): LiveData<LoginResponse> {
        val apiresp = MutableLiveData<LoginResponse>()
        api.signIn(userSeek).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(LoginResponse(null, "0"))
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                when {
                    response.code() == 200 -> apiresp.postValue(response.body())
                    response.code() == 401 -> apiresp.postValue(LoginResponse(null, "1"))
                }
            }

        })
        return apiresp

    }

    fun getClient(api: SeekMakeApi, id: String): LiveData<ClientResponse> {
        val apiresp = MutableLiveData<ClientResponse>()

        api.getClient(id).enqueue(object : Callback<ClientResponse> {
            override fun onFailure(call: Call<ClientResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(ClientResponse(null, "0"))
            }

            override fun onResponse(call: Call<ClientResponse>, response: Response<ClientResponse>) {
                if (response.isSuccessful)
                    apiresp.postValue(response.body())
            }
        })
        return apiresp
    }

    fun pwdReset(api: SeekMakeApi, userSeek: UserSeek): LiveData<PassResetResponse> {
        val apiresp = MutableLiveData<PassResetResponse>()
        api.resetPwd(userSeek).enqueue(object : Callback<PassResetResponse> {
            override fun onFailure(call: Call<PassResetResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(PassResetResponse("0", null))
            }

            override fun onResponse(call: Call<PassResetResponse>, response: Response<PassResetResponse>) {
                if (response.isSuccessful)
                    apiresp.postValue(response.body())
            }

        })
        return apiresp
    }

    fun updateProfile(api: SeekMakeApi, id: String, userSeek: UserSeek): LiveData<UpdateProfileResponse> {
        val apiresp = MutableLiveData<UpdateProfileResponse>()
        api.updateProfile(id, userSeek).enqueue(object : Callback<UpdateProfileResponse> {
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(UpdateProfileResponse(null, "0"))
            }

            //TODO don't forget to make sure that inputs are validate in order to get code 200
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                when {
                    response.code() == 200 -> apiresp.postValue(response.body())
                    response.code() == 401 -> apiresp.postValue(UpdateProfileResponse(null, "1"))
                }
            }

        })
        return apiresp
    }

    fun updatePwd(api: SeekMakeApi, id: String, pwdRequest: PwdRequest): LiveData<PwdUpdateResponse> {
        val apiresp = MutableLiveData<PwdUpdateResponse>()
        api.updatePwd(id, pwdRequest).enqueue(object : Callback<PwdUpdateResponse> {
            override fun onFailure(call: Call<PwdUpdateResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(PwdUpdateResponse(null, "0"))
            }

            override fun onResponse(call: Call<PwdUpdateResponse>, response: Response<PwdUpdateResponse>) {
                when {
                    response.code() == 200 -> apiresp.postValue(response.body())
                    //validation error
                    response.code() == 400 -> apiresp.postValue(PwdUpdateResponse(null, "1"))
                    // old pwd is not correct
                    response.code() == 422 -> apiresp.postValue(PwdUpdateResponse(null, "2"))

                }
            }

        })
        return apiresp
    }

    fun uploadFile(api: SeekMakeApi, file: MultipartBody.Part): LiveData<FileResponse> {
        val apiresp = MutableLiveData<FileResponse>()

        api.uploadFile(file).enqueue(object : Callback<FileResponse> {
            override fun onFailure(call: Call<FileResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(FileResponse("0"))
            }

            override fun onResponse(call: Call<FileResponse>, response: Response<FileResponse>) {
                if (response.isSuccessful)
                    apiresp.postValue(response.body())
            }

        })
        return apiresp
    }

    fun submitOrder(api: SeekMakeApi, token: String, order: Order): LiveData<OrderResponse> {
        val apiresp = MutableLiveData<OrderResponse>()

        api.submitOrder("Bearer $token", order).enqueue(object : Callback<OrderResponse> {
            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(OrderResponse(null, "0"))
            }

            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                //TODO code 200
                if (response.code() == 200)
                    apiresp.postValue(response.body())
                else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Log.d("vijr", jObjError.getJSONObject("data").toString())
                }

            }

        })

        return apiresp


    }

    fun getDemands(api: SeekMakeApi, token: String, id: String): LiveData<DemandsResponse> {
        val apiresp = MutableLiveData<DemandsResponse>()
        api.getDemands("Bearer $token", id).enqueue(object : Callback<DemandsResponse> {
            override fun onFailure(call: Call<DemandsResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(DemandsResponse(null, "0"))
            }

            override fun onResponse(call: Call<DemandsResponse>, response: Response<DemandsResponse>) {
                if (response.code() == 200)
                    apiresp.postValue(response.body())
            }

        })

        return apiresp

    }

    fun updateDemand(api: SeekMakeApi, id: String): LiveData<UpdateDemandResponse> {
        val apiresp = MutableLiveData<UpdateDemandResponse>()
        api.updateDemand(id).enqueue(object : Callback<UpdateDemandResponse> {
            override fun onFailure(call: Call<UpdateDemandResponse>, t: Throwable) {
                if (t is IOException)
                    apiresp.postValue(UpdateDemandResponse(null, "0"))
            }

            override fun onResponse(call: Call<UpdateDemandResponse>, response: Response<UpdateDemandResponse>) {
                if (response.code() == 200)
                    apiresp.postValue(response.body())
            }

        })

        return apiresp

    }
}

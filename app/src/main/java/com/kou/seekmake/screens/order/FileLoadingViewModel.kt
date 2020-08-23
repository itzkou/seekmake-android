package com.kou.seekmake.screens.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.data.retrofit.SeekMakeApi
import com.kou.seekmake.data.retrofit.SeekMakeRepository
import com.kou.seekmake.models.SeekMake.ClientResponse
import com.kou.seekmake.models.SeekMake.FileResponse
import com.kou.seekmake.models.SeekMake.Order
import com.kou.seekmake.models.SeekMake.OrderResponse
import com.kou.seekmake.screens.common.BaseViewModel
import okhttp3.MultipartBody

class FileLoadingViewModel(private val repository: SeekMakeRepository, onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {
    private var uploadResponse: LiveData<FileResponse> = MutableLiveData<FileResponse>()

    private val api = SeekMakeApi.create()


    fun uploadFile(file: MultipartBody.Part): LiveData<FileResponse> {
        uploadResponse = repository.uploadFile(api, file)
        return uploadResponse
    }

    fun getClient(id: String): LiveData<ClientResponse> {

        return repository.getClient(api, id)
    }

    fun submitOrder(token: String, order: Order): LiveData<OrderResponse> {
        return repository.submitOrder(api, token, order)
    }
}
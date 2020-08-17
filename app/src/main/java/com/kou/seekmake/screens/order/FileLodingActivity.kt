package com.kou.seekmake.screens.order

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.RealPathUtil
import kotlinx.android.synthetic.main.activity_file_loding.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class FileLodingActivity : BaseActivity() {
    private lateinit var vm: FileLoadingViewModel
    private lateinit var mFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_loding)

        val orderType = intent.getStringExtra(EXTRA_ORDER_TYPE) ?: return finish()

        vm = initViewModel()

        requestToGetFile(PERMISSION_EXTERNAL_STORAGE)

        btn_upload_file.setOnClickListener {
            getFile()
        }

        btn_next.setOnClickListener {
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {


            val uri: Uri = data!!.data!!
            val realPath = RealPathUtil.getRealPath(this, uri)!!
            mFile = File(realPath)
            val requestFile: RequestBody = RequestBody.create(
                    MediaType.parse(contentResolver.getType(uri)!!),
                    mFile
            )
            val body = MultipartBody.Part.createFormData("file", mFile.name, requestFile)

            vm.uploadFile(body).observe(this, Observer {
                if (it.URL == "0")
                    Toast.makeText(this, "Network Faillure", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "File is ready", Toast.LENGTH_SHORT).show()


            })


        }
    }


    private fun getFile() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        startActivityForResult(intent, 0)
    }

    private fun requestToGetFile(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // First time
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
            } else {
                // Not first time
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
            }
        }
    }


    companion object {
        private const val EXTRA_ORDER_TYPE = "ORDER_TYPE"
        const val PERMISSION_EXTERNAL_STORAGE = 7


        fun start(context: Context, orderType: String) {
            val intent = Intent(context, FileLodingActivity::class.java)
            intent.putExtra(EXTRA_ORDER_TYPE, orderType)

            context.startActivity(intent)
        }
    }
}
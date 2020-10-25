package com.kou.seekmake.screens.order

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.kou.seekmake.R
import com.kou.seekmake.models.SeekMake.Client
import com.kou.seekmake.models.SeekMake.Material
import com.kou.seekmake.models.SeekMake.Order
import com.kou.seekmake.screens.common.*
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager
import kotlinx.android.synthetic.main.activity_file_loding.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class FileLodingActivity : BaseActivity() {
    private lateinit var technique: String
    private lateinit var vm: FileLoadingViewModel
    private lateinit var mFile: File
    private lateinit var mClient: Client
    private var m1 = Material("Acier", R.drawable.ic_acier)
    private var m2 = Material("Alucobond", R.drawable.ic_aluco)
    private var m3 = Material("Bois", R.drawable.ic_bois)
    private var arr = arrayListOf(m1, m2, m3)

    /** extra order criteria**/
    private var chosenMaterial: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_loding)
        requestToGetFile(PERMISSION_EXTERNAL_STORAGE)
        technique = intent.getStringExtra(EXTRA_ORDER_TYPE) ?: return finish()
        spinner_material.adapter = CustomAdapter(this, arr)


        setupAuthGuard {
            vm = initViewModel()
            /*** getting the client **/
            PrefsManager.geID(this)?.let { _id ->
                vm.getClient(_id).observe(this, Observer {
                    if (it.msg == "0")
                        Toast.makeText(this, "Network Faillure", Toast.LENGTH_SHORT).show()
                    else if (it.data != null) {
                        mClient = it.data


                    }
                })
            }
            /*** selecting the file **/
            btn_upload_file.setOnClickListener {
                getFile()
            }
            /** extra attributes **/
            spinner_material.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position) {
                        0 -> {
                            chosenMaterial = "acier"
                            imHint.setImageResource(R.drawable.ic_acier)
                        }
                        1 -> {
                            chosenMaterial = "alucobond"
                            imHint.setImageResource(R.drawable.ic_aluco)
                        }
                        2 -> {
                            chosenMaterial = "bois"
                            imHint.setImageResource(R.drawable.ic_bois)
                        }


                    }
                }

            }


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
            BuilderLoading.showDialog(this)

            /** File Upload **/
            vm.uploadFile(body).observe(this, Observer { fileResponse ->

                if (fileResponse.URL == "0") {
                    BuilderLoading.dialog.dismiss()
                    Toast.makeText(this, "Network Faillure", Toast.LENGTH_SHORT).show()
                } else {
                    /*** submitting order **/
                    BuilderLoading.dialog.dismiss()

                    btn_next.setOnClickListener {
                        var qty = 1
                        if (qty_input.text.toString().isNotEmpty())
                            qty = qty_input.text.toString().toInt()


                        vm.submitOrder(PrefsManager.geToken(this)!!, Order(mClient.address, mClient.city, mClient._id, "https://seekmake.com:3000/" + fileResponse.URL, mClient.firstname, mClient.lastname, mClient.tel, technique, "normal", mClient.zip, epaisseur_input.text.toString(), chosenMaterial, qty, resolution_input.text.toString(), true)).observe(this, Observer { orderResponse ->
                            if (orderResponse.msg == "0")
                                Toast.makeText(this, "Network Faillure", Toast.LENGTH_SHORT).show()
                            else if (orderResponse.data != null) {
                                Toast.makeText(this, "Order submitted !", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, DeliveryActivity::class.java))
                            }

                        })
                    }
                }


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
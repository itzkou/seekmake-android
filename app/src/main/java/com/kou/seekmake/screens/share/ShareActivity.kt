package com.kou.seekmake.screens.share

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.anilokcun.uwmediapicker.model.UwMediaPickerMediaModel
import com.kou.seekmake.R
import com.kou.seekmake.data.firebase.common.FirebaseHelper
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.CameraHelper
import com.kou.seekmake.screens.common.loadImage
import com.kou.seekmake.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_share.*
import java.io.File

class ShareActivity : BaseActivity() {
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User
    private lateinit var mViewModel: ShareViewModel
    private val allSelectedMediaPaths by lazy { arrayListOf<String>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        setupAuthGuard {
            /*** Media Picker ***/
            openMediaDialog()

            mViewModel = initViewModel()
            mFirebase = FirebaseHelper(this)

            mCamera = CameraHelper(this)


            back_image.setOnClickListener { finish() }
            share_text.setOnClickListener { share() }

            mViewModel.user.observe(this, Observer {
                it?.let {
                    mUser = it
                }
            })
            mViewModel.shareCompletedEvent.observe(this, Observer {
                finish()
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                post_image.loadImage(mCamera.imageUri?.toString())
                Log.d("bobo", mCamera.imageUri?.toString()!!)

            } else {
                finish()
            }
        }
    }

    private fun share() {
        if (mCamera.imageUri != null)
            mViewModel.share(mUser, mCamera.imageUri, caption_input.text.toString())
        else if (allSelectedMediaPaths[0].isNotEmpty())
            mViewModel.share(mUser, Uri.fromFile(File(allSelectedMediaPaths[0])), caption_input.text.toString())
    }

    /** Media Picker Dialog **/
    private fun openMediaDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Media")
        builder.setMessage("Choose your files")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Camera") { _, _ ->
            mCamera.takeCameraPicture()
        }
        builder.setNegativeButton("Gallery") { _, _ ->
            requestToOpenImagePicker(PERMISSION_REQUEST_CODE_PICK_IMAGE_VIDEO, UwMediaPicker.GalleryMode.ImageGallery, ::openUwMediaPicker)
        }
        //TODO add videos next time

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    /** Opens UwMediaPicker for select images*/
    private fun openUwMediaPicker(galleryMode: UwMediaPicker.GalleryMode) {
        UwMediaPicker.with(this)
                .setGalleryMode(galleryMode)
                .setGridColumnCount(4)
                .setMaxSelectableMediaCount(10)
                .setLightStatusBar(true)                                // Is llight status bar enable, default is true
                .enableImageCompression(true)                // Is image compression enable, default is false
                .setCompressionMaxWidth(1280F)                // Compressed image's max width px, default is 1280
                .setCompressionMaxHeight(720F)                // Compressed image's max height px, default is 720
                .setCompressFormat(Bitmap.CompressFormat.JPEG)        // Compressed image's format, default is JPEG
                .setCompressionQuality(85)                // Image compression quality, default is 85
                .setCompressedFileDestinationPath("${application.getExternalFilesDir(null)!!.path}/Pictures")
                .launch(::onMediaSelected)
    }

    private fun onMediaSelected(selectedMediaList: List<UwMediaPickerMediaModel>?) {
        if (selectedMediaList != null) {
            val selectedMediaPathList = selectedMediaList.map { it.mediaPath }
            allSelectedMediaPaths.addAll(selectedMediaPathList)
            post_image.loadImage(allSelectedMediaPaths[0])


        } else {
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show()
        }
    }

    /** Request to open Image Picker Intent and Handle the permissions */
    private fun requestToOpenImagePicker(requestCode: Int, galleryMode: UwMediaPicker.GalleryMode, openMediaPickerFunc: (UwMediaPicker.GalleryMode) -> Unit) {
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
        } else {
            // Permission has already been granted
            openMediaPickerFunc.invoke(galleryMode)
        }
    }

    /** Handles Media Pick Request */
    private fun handleMediaPickPermissionRequest(grantResults: IntArray, galleryMode: UwMediaPicker.GalleryMode, openMediaPicker: (UwMediaPicker.GalleryMode) -> Unit) {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // Permission granted
            openMediaPicker.invoke(galleryMode)
        } else {
            // Permission denied
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Without "Never Ask Again" Checked
                Toast.makeText(this, "Without permission you cant do that.", Toast.LENGTH_SHORT).show()
            } else {
                // With "Never Ask Again" Checked
                Toast.makeText(this, "You need to give permission. Go to Settings...", Toast.LENGTH_SHORT).show()
            }
        }
        return
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE_PICK_IMAGE -> {
                handleMediaPickPermissionRequest(grantResults, UwMediaPicker.GalleryMode.ImageGallery, ::openUwMediaPicker)
            }
            PERMISSION_REQUEST_CODE_PICK_VIDEO -> {
                handleMediaPickPermissionRequest(grantResults, UwMediaPicker.GalleryMode.VideoGallery, ::openUwMediaPicker)
            }
            PERMISSION_REQUEST_CODE_PICK_IMAGE_VIDEO -> {
                handleMediaPickPermissionRequest(grantResults, UwMediaPicker.GalleryMode.ImageAndVideoGallery, ::openUwMediaPicker)
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE_PICK_IMAGE = 9
        const val PERMISSION_REQUEST_CODE_PICK_VIDEO = 10
        const val PERMISSION_REQUEST_CODE_PICK_IMAGE_VIDEO = 11
        const val TAG = "ShareActivity"

        init {
            // For using vector drawables on lower APIs
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
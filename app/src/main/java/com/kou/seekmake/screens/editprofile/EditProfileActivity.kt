package com.kou.seekmake.screens.editprofile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.anilokcun.uwmediapicker.model.UwMediaPickerMediaModel
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.*
import com.kou.seekmake.screens.share.ShareActivity
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File

class EditProfileActivity : BaseActivity(), PasswordDialog.Listener {
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mCamera: CameraHelper
    private lateinit var mViewModel: EditProfileViewModel
    private val allSelectedMediaPaths by lazy { arrayListOf<String>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        mCamera = CameraHelper(this)

        back_image.setOnClickListener { finish() }
        save_image.setOnClickListener {
            updateProfile()
            share()
        }
        change_photo_text.setOnClickListener {
            openMediaDialog()
        }
        /*** Auth Guard is used for token and session ***/
        setupAuthGuard {
            mViewModel = initViewModel()

            mViewModel.user.observe(this, Observer {
                it?.let {
                    mUser = it
                    name_input.setText(mUser.name)
                    username_input.setText(mUser.username)
                    website_input.setText(mUser.website)
                    bio_input.setText(mUser.bio)
                    email_input.setText(mUser.email)
                    phone_input.setText(mUser.phone?.toString())
                    profile_image.loadUserPhoto(mUser.photo)
                }
            })
            mViewModel.shareCompletedEvent.observe(this, Observer {
                BuilderLoading.dialog.dismiss()
                finish()
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mCamera.requestCode) {
            if (resultCode == RESULT_OK) {
                Log.d("cam", "Camera data retrieved")

            } else {
                finish()
            }
        }
    }


    private fun updateProfile() {
        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                PasswordDialog().show(supportFragmentManager, "password_dialog")
            }
        } else {
            showToast(error)
        }
    }

    private fun readInputs(): User {
        return User(
                name = name_input.text.toString(),
                username = username_input.text.toString(),
                email = email_input.text.toString(),
                website = website_input.text.toStringOrNull(),
                bio = bio_input.text.toStringOrNull(),
                phone = phone_input.text.toString().toLongOrNull()
        )
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            mViewModel.updateEmail(
                    currentEmail = mUser.email,
                    newEmail = mPendingUser.email,
                    password = password)
                    .addOnSuccessListener { updateUser(mPendingUser) }
        } else {
            showToast(getString(R.string.you_should_enter_your_password))
        }
    }

    private fun updateUser(user: User) {
        mViewModel.updateUserProfile(currentUser = mUser, newUser = user)
                .addOnSuccessListener {
                    showToast(getString(R.string.profile_saved))
                    finish()
                }
    }

    private fun validate(user: User): String? =
            when {
                user.name.isEmpty() -> getString(R.string.please_enter_name)
                user.username.isEmpty() -> getString(R.string.please_enter_username)
                user.email.isEmpty() -> getString(R.string.please_enter_email)
                else -> null
            }


    private fun share() {
        if (mCamera.imageUri != null) {
            BuilderLoading.showDialog(this)
            val imageCompressor = ImgCompressor(1080F,
                    1080F,
                    Bitmap.CompressFormat.JPEG,
                    85, "${application.getExternalFilesDir(null)!!.path}/Pictures")

            val compressedImageFile = imageCompressor.compress(File(mCamera.cameraFile.path))
            mViewModel.uploadAndSetUserPhoto(Uri.fromFile(compressedImageFile))
        } else if (allSelectedMediaPaths.isNotEmpty()) {
            BuilderLoading.showDialog(this)
            mViewModel.uploadAndSetUserPhoto(Uri.fromFile(File(allSelectedMediaPaths[0])))
        }

    }

    /** Media Picker Dialog **/
    private fun openMediaDialog() {
        val builder = AlertDialog.Builder(this, R.style.media)
        builder.setTitle("Choose your Files")
        builder.setPositiveButton("Camera") { _, _ ->
            mCamera.takeCameraPicture()
        }
        builder.setNegativeButton("Gallery") { _, _ ->
            requestToOpenImagePicker(ShareActivity.PERMISSION_REQUEST_CODE_PICK_IMAGE_VIDEO, UwMediaPicker.GalleryMode.ImageGallery, ::openUwMediaPicker)
        }

        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    /** Opens UwMediaPicker for select images*/
    private fun openUwMediaPicker(galleryMode: UwMediaPicker.GalleryMode) {
        UwMediaPicker.with(this)
                .setGalleryMode(galleryMode)
                .setGridColumnCount(4)
                .setMaxSelectableMediaCount(1)
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
            profile_image.loadImage(allSelectedMediaPaths[0])


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
                Toast.makeText(this, "You need to give permission. Go to Settings", Toast.LENGTH_SHORT).show()
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

        const val PERMISSION_REQUEST_CODE_PICK_IMAGE = 3
        const val PERMISSION_REQUEST_CODE_PICK_VIDEO = 4
        const val PERMISSION_REQUEST_CODE_PICK_IMAGE_VIDEO = 5
        const val TAG = "EditProfileActivity"
    }
}
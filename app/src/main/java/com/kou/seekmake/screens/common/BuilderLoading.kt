package com.kou.seekmake.screens.common

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.kou.seekmake.R

class BuilderLoading {
    companion object {
        lateinit var dialog: AlertDialog
        fun showDialog(context: Context) {

            val dialogView = LayoutInflater.from(context).inflate(R.layout.builder_loading, null)

            val anim = dialogView.findViewById<LottieAnimationView>(R.id.animAuth)
            val builder = AlertDialog.Builder(context)

            builder.setView(dialogView)

            anim.playAnimation()

            dialog = builder.create()

            dialog.show()


        }

    }
}
package com.kou.seekmake.screens.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.igreenwood.loupe.Loupe
import com.kou.seekmake.R
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private var url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        url = intent.getStringExtra(IMAGE_URL) ?: return finish()
        image.loadImgRound(url, 24)
        val loupe = Loupe.create(image, framo) { // imageView is your ImageView
            onViewTranslateListener = object : Loupe.OnViewTranslateListener {
                override fun onDismiss(view: ImageView) {
                    finish()
                }

                override fun onRestore(view: ImageView) {

                }

                override fun onStart(view: ImageView) {

                }

                override fun onViewTranslate(view: ImageView, amount: Float) {

                }


            }
        }
    }

    companion object {
        private const val IMAGE_URL = "IMG_URL"


        fun start(context: Context, imageUrl: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(IMAGE_URL, imageUrl)

            context.startActivity(intent)
        }
    }
}
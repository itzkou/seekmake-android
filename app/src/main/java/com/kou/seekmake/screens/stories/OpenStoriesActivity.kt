package com.kou.seekmake.screens.stories

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.loadUserPhoto
import com.kou.seekmake.screens.common.setupAuthGuard
import com.kou.seekmake.screens.common.setupBlur
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_open_stories.*


class OpenStoriesActivity : BaseActivity(), StoriesProgressView.StoriesListener {
    private lateinit var mViewModel: StoryViewModel


    private var counter = 0
    private var userStories: MutableList<String> = mutableListOf()
    private var pressTime = 0L
    private var limit = 500L

    private val onTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    storiesView.pause()
                    return false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    storiesView.resume()
                    return limit < now - pressTime
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_open_stories)
        storiesView.setStoriesListener(this)
        setupBlur(topBlur, this, window)



        setupAuthGuard {
            val userId = intent.getStringExtra("user_uid")
            var isLiked = false
            mViewModel = initViewModel()
            mViewModel.getStories(userId!!).observe(this, Observer { stories ->
                stories?.let {
                    if (it.isNotEmpty()) {
                        it.forEach { story ->
                            userStories.add(story.image)
                        }
                        storiesView.setStoriesCount(userStories.size)
                        storiesView.setStoryDuration(6000L)
                        storiesView.startStories(0)
                        counter = 0
                        imStory.loadUserPhoto(userStories[counter])


                    }

                    //todo change it
                    imloveReact.setOnClickListener {
                        isLiked = !isLiked

                        imloveReact.setImageResource(
                                if (isLiked) R.drawable.ic_heartsa
                                else R.drawable.ic_heartsd)

                        if (isLiked)
                            rain_heart.playAnimation()
                        rain_heart.visibility = if (isLiked) View.VISIBLE
                        else View.GONE
                    }


                }
            })

        }

        // bind reverse view
        reverse.setOnClickListener {
            storiesView.reverse()
        }
        reverse.setOnTouchListener(onTouchListener)

        // bind skip view
        skip.setOnClickListener {
            storiesView.skip()
        }

        skip.setOnTouchListener(onTouchListener)
    }

    override fun onNext() {
        if (counter < userStories.size)
            imStory.loadUserPhoto(userStories[++counter])


    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        imStory.loadUserPhoto(userStories[--counter])

    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        storiesView.destroy()
        super.onDestroy()
    }

}
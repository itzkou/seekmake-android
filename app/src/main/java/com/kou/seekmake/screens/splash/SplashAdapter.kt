package com.kou.seekmake.screens.splash

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.kou.seekmake.R

class SplashAdapter(context: Context) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private var mcontext: Context = context


    //  Arrays used to bind data into a single layout file "slide_layout"
    var slider_image = intArrayOf(R.drawable.ic_3d, R.drawable.ic_fraisage, R.drawable.ic_laser)


    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj as ScrollView
    }

    override fun getCount(): Int {
        return slider_image.size

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //IDK what this is but put it
        layoutInflater = mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.layout_slide, container, false)

        //now find slider views by id

        val imslide = view.findViewById<ImageView>(R.id.imslide)
        val dots: LottieAnimationView = view.findViewById<LottieAnimationView>(R.id.dots)
        dots.playAnimation()


        //data binding
        imslide.setImageResource(slider_image[position])

        container.addView(view)

        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ScrollView)
    }
}
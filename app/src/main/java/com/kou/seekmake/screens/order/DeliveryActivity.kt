package com.kou.seekmake.screens.order

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.kou.seekmake.R
import kotlinx.android.synthetic.main.activity_delivery.*

class DeliveryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            btn_send.setOnClickListener {
                when (view.getId()) {
                    R.id.radio24 -> {
                        if (checked) {
                            //TODO
                        }
                    }
                    R.id.radio72 ->
                        if (checked) {
                            //TODO
                        }
                }
            }

            when (view.getId()) {
                R.id.radio24 -> {
                    radio24.setButtonDrawable(R.drawable.ic_truck24a)
                    radio72.setButtonDrawable(R.drawable.ic_truck72d)
                }
                R.id.radio72 -> {
                    radio72.setButtonDrawable(R.drawable.ic_truck72a)
                    radio24.setButtonDrawable(R.drawable.ic_truck24d)
                }

            }
        }
    }
}
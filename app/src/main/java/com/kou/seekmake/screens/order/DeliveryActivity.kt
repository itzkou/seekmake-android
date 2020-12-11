package com.kou.seekmake.screens.order

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.config
import com.kou.seekmake.screens.profile.ProfileActivity
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
                            val snacko = Snackbar.make(btn_send, " Your order is pending approval !", Snackbar.LENGTH_SHORT)
                            snacko.config(btn_send.context)
                            snacko.show()
                            startActivity(Intent(this, ProfileActivity::class.java))
                        }
                    }
                    R.id.radio72 ->
                        if (checked) {
                            val snacko = Snackbar.make(btn_send, " Your order is pending approval !", Snackbar.LENGTH_SHORT)
                            snacko.config(btn_send.context)
                            snacko.show()
                            startActivity(Intent(this, ProfileActivity::class.java))
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
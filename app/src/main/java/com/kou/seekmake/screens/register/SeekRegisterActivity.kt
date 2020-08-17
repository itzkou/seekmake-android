package com.kou.seekmake.screens.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.kou.seekmake.R
import com.kou.seekmake.models.SeekMake.UserSeek
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager
import com.kou.seekmake.screens.common.coordinateBtnAndInputs
import com.kou.seekmake.screens.splash.Splash
import kotlinx.android.synthetic.main.activity_seek_register.*

class SeekRegisterActivity : BaseActivity() {
    private lateinit var vm: SeekRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seek_register)

        vm = initViewModel()

        coordinateBtnAndInputs(btn_seek_register, phone_input, adress_input, city_input, zip_input)
        btn_seek_register.setOnClickListener {
            val userSeek = UserSeek(adress_input.text.toString(), city_input.text.toString(), PrefsManager.geMail(this)!!, PrefsManager.geFname(this)!!, PrefsManager.geLname(this)!!, PrefsManager.gePwd(this)!!, PrefsManager.gePwd(this)!!, phone_input.text.toString(), zip_input.text.toString())

            vm.signUp(userSeek).observe(this, Observer {
                when (it.msg) {
                    "0" -> Toast.makeText(this, "Network Faillure", Toast.LENGTH_SHORT).show()
                    "1" -> Toast.makeText(this, "Validation failed", Toast.LENGTH_SHORT).show()
                    "OK" -> {
                        PrefsManager.seID(this, it.data!!._id)
                        startActivity(Intent(this, Splash::class.java))
                        finish()
                    }
                }
            })

        }


    }


}
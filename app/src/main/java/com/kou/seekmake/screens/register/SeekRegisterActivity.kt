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
import com.kou.seekmake.screens.common.coordinatePhone
import com.kou.seekmake.screens.splash.Splash
import kotlinx.android.synthetic.main.activity_seek_register.*

class SeekRegisterActivity : BaseActivity() {
    private lateinit var vm: SeekRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seek_register)

        vm = initViewModel()

        coordinateBtnAndInputs(btn_seek_register, adress_input, city_input, zip_input)
        coordinatePhone(btn_seek_register, phone_input)
        btn_seek_register.setOnClickListener {
            val userSeek = UserSeek(address = adress_input.text.toString(), city = city_input.text.toString(), email = PrefsManager.geMail(this)!!, firstname = PrefsManager.geFname(this)!!, lastname = PrefsManager.geLname(this)!!, password = PrefsManager.gePwd(this)!!, password_confirmation = PrefsManager.gePwd(this)!!, tel = phone_input.text.toString(), zip = zip_input.text.toString())

            vm.signUp(userSeek).observe(this, Observer { signUpResponse ->
                PrefsManager.seID(this, signUpResponse.data!!._id)
                when (signUpResponse.msg) {
                    "0" -> Toast.makeText(this, "Network Faillure", Toast.LENGTH_SHORT).show()
                    "1" -> Toast.makeText(this, "Validation failed", Toast.LENGTH_SHORT).show()
                    "2" -> Toast.makeText(this, "Email exists", Toast.LENGTH_SHORT).show()
                    "OK" -> {
                        vm.signIn(userSeek).observe(this, Observer { loginResponse ->
                            when (loginResponse.msg) {
                                "0" -> Toast.makeText(this, "Network Faillure", Toast.LENGTH_SHORT).show()
                                "1" -> Toast.makeText(this, "Verify your credentials", Toast.LENGTH_SHORT).show()
                                "OK" -> {
                                    PrefsManager.seToken(this, loginResponse.data!!.token)
                                    startActivity(Intent(this, Splash::class.java))
                                    finish()
                                }
                            }
                        })

                    }
                }
            })

        }


    }


}
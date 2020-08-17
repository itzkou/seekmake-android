package com.kou.seekmake.screens.register

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager
import com.kou.seekmake.screens.common.coordinateBtnAndInputs
import com.kou.seekmake.screens.common.coordinateName
import com.kou.seekmake.screens.common.coordinatePwd
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {
    private lateinit var mViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mViewModel = initViewModel()

        coordinateBtnAndInputs(btn_register, email_input)
        coordinatePwd(btn_register, pwd_register)
        coordinateName(btn_register, full_name)



        btn_register.setOnClickListener {
            mViewModel.onEmailEntered(email_input.text.toString(), full_name.text.toString(), pwd_register.text.toString())
        }


        mViewModel.goToSeekRegister.observe(this, Observer {
            startSeekRegisterActivity()
        })

    }


    private fun startSeekRegisterActivity() {

        val fName = full_name.text.toString().substring(0, full_name.text.toString().indexOf(" ") + 1)
        val lName = full_name.text.toString().substring(full_name.text.toString().lastIndexOf(" ") + 1, full_name.text.toString().length)
        val pwd = pwd_register.text.toString()
        val mail = email_input.text.toString()
        PrefsManager.seFname(this, fName)
        PrefsManager.seLname(this, lName)
        PrefsManager.seMail(this, mail)
        PrefsManager.sePwd(this, pwd)
        startActivity(Intent(this, SeekRegisterActivity::class.java))
        finish()
    }

    companion object {
        const val TAG = "RegisterActivity"
    }
}
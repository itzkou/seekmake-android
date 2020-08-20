package com.kou.seekmake.screens.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.kou.seekmake.R
import com.kou.seekmake.models.SeekMake.UserSeek
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.JWTUtils
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager
import com.kou.seekmake.screens.common.coordinateBtnAndInputs
import com.kou.seekmake.screens.home.HomeActivity
import com.kou.seekmake.screens.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.json.JSONObject

class LoginActivity : BaseActivity(), KeyboardVisibilityEventListener, View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        KeyboardVisibilityEvent.setEventListener(this, this)
        coordinateBtnAndInputs(login_btn, email_input, password_input)
        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

        mViewModel = initViewModel()
        mViewModel.goToSeekLogin.observe(this, Observer {
            mViewModel.signIn(UserSeek(email = email_input.text.toString(), password = password_input.text.toString())).observe(this, Observer {

                when (it.msg) {
                    "0" -> Toast.makeText(this, "Network Faillure", Toast.LENGTH_SHORT).show()
                    "1" -> Toast.makeText(this, "Verify your credentials", Toast.LENGTH_SHORT).show()
                    "OK" -> {
                        val json = JSONObject(JWTUtils.decoded(it.data!!.token)!!)
                        val id = json.getJSONObject("data").getString("_id")
                        PrefsManager.seID(this, id)
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }


            })


        })
        mViewModel.goToRegisterScreen.observe(this, Observer {
            startActivity(Intent(this, RegisterActivity::class.java))
        })
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_btn ->
                mViewModel.onLoginClick(
                        email = email_input.text.toString(),
                        password = password_input.text.toString()
                )
            R.id.create_account_text -> mViewModel.onRegisterClick()

        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            create_account_text.visibility = View.GONE
        } else {
            create_account_text.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}

package com.kou.seekmake.screens.authmedium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kou.seekmake.R
import com.kou.seekmake.common.toUnit
import com.kou.seekmake.data.firebase.common.auth
import com.kou.seekmake.data.firebase.common.database
import com.kou.seekmake.data.retrofit.SeekMakeApi
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.models.SeekMake.LoginResponse
import com.kou.seekmake.models.SeekMake.UserSeek
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.JWTUtils
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager
import com.kou.seekmake.screens.home.HomeActivity
import com.kou.seekmake.screens.login.LoginActivity
import com.kou.seekmake.screens.register.SeekRegisterActivity
import kotlinx.android.synthetic.main.activity_social_auth.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SocialAuth : BaseActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_auth)
        /******* Social Auth ******/
        mAuth = FirebaseAuth.getInstance()


        /**Facebook ***/
        callbackManager = CallbackManager.Factory.create()

        btnFb.setReadPermissions("email", "public_profile")
        btnFb.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                if (AccessToken.getCurrentAccessToken() != null) {
                    val request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken()
                    ) { me, _ ->
                        val email = me.get("email").toString()
                        handleFacebookAccessToken(loginResult.accessToken, email)
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "email")
                    request.parameters = parameters
                    request.executeAsync()


                }


            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // updateUI(null)
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                //updateUI(null)
            }
        })
        btnSign.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        /***Google ***/
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogle.setOnClickListener { signInGoo() }

    }

    //TODO handle when back key pressed
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*** Facebook ***/
        callbackManager.onActivityResult(requestCode, resultCode, data)

        /** google**/
        if (requestCode == RC_SIGN_IN && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!, account.email!!)
            } catch (e: ApiException) {
                Log.d("firebaseAuthWithGoogle:", e.toString())

            }
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken, email: String) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        isUserExistsForEmail(email).addOnSuccessListener { exist ->
            if (!exist) {
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = mAuth.currentUser
                                createUser(mkUser(user!!.displayName!!, user.email!!), user.uid).addOnSuccessListener {
                                    startSeekRegisterActivity(user.displayName!!, "social123", user.email!!)
                                }.addOnFailureListener { Log.d("createUser", it.message!!) }

                            }


                        }
            } else {

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = mAuth.currentUser
                                val api = SeekMakeApi.create()
                                api.signIn(UserSeek(email = user!!.email!!, password = "social123")).enqueue(object : Callback<LoginResponse> {
                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        if (t is IOException)
                                            Toast.makeText(this@SocialAuth, "Network Faillure", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                        if (response.code() == 200) {
                                            val json = JSONObject(JWTUtils.decoded(response.body()!!.data!!.token)!!)
                                            val id = json.getJSONObject("data").getString("_id")
                                            PrefsManager.seID(this@SocialAuth, id)
                                            PrefsManager.seToken(this@SocialAuth, response.body()!!.data!!.token)
                                            startActivity(Intent(this@SocialAuth, HomeActivity::class.java))
                                            finish()
                                        }
                                    }

                                })


                            } else {
                                Toast.makeText(baseContext, task.exception!!.message,
                                        Toast.LENGTH_SHORT).show()
                            }


                        }
            }

        }

    }

    fun signOutFb() {
        mAuth.signOut()
        LoginManager.getInstance().logOut()

    }

    private fun firebaseAuthWithGoogle(idToken: String, email: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        isUserExistsForEmail(email).addOnSuccessListener { exist ->
            if (!exist) {
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = mAuth.currentUser

                                createUser(mkUser(user!!.displayName!!, user.email!!), user.uid).addOnSuccessListener {
                                    startSeekRegisterActivity(user.displayName!!, "social123", user.email!!)
                                }.addOnFailureListener { Log.d("createUser", it.message!!) }

                            }


                        }
            } else {

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = mAuth.currentUser
                                val api = SeekMakeApi.create()
                                api.signIn(UserSeek(email = user!!.email!!, password = "social123")).enqueue(object : Callback<LoginResponse> {
                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        if (t is IOException)
                                            Toast.makeText(this@SocialAuth, "Network Faillure", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                        if (response.code() == 200) {
                                            val json = JSONObject(JWTUtils.decoded(response.body()!!.data!!.token)!!)
                                            val id = json.getJSONObject("data").getString("_id")
                                            PrefsManager.seID(this@SocialAuth, id)
                                            PrefsManager.seToken(this@SocialAuth, response.body()!!.data!!.token)
                                            startActivity(Intent(this@SocialAuth, HomeActivity::class.java))
                                            finish()
                                        }


                                    }

                                })

                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.exception)
                            }


                        }
            }

        }

    }

    private fun signInGoo() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOutGoo() {
        // Firebase sign out
        mAuth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        mAuth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
        }
    }


    private fun createUser(user: User, uid: String): Task<Unit> =
            database.child("users").child(uid).setValue(user).toUnit()

    private fun startSeekRegisterActivity(fullName: String, pwd: String, mail: String) {

        var fName = fullName.substring(0, fullName.indexOf(" ") + 1)
        var lName = fullName.substring(fullName.lastIndexOf(" ") + 1, fullName.length)


        if (fName.length < 3)
            fName += ".."
        if (lName.length < 3)
            lName += ".."


        PrefsManager.seFname(this, fName)
        PrefsManager.seLname(this, lName)
        PrefsManager.seMail(this, mail)
        PrefsManager.sePwd(this, pwd)
        startActivity(Intent(this, SeekRegisterActivity::class.java))
        finish()
    }


    private fun isUserExistsForEmail(email: String): Task<Boolean> =
            auth.fetchSignInMethodsForEmail(email).onSuccessTask {
                val signInMethods = it?.signInMethods ?: emptyList<String>()
                Tasks.forResult(signInMethods.isNotEmpty())
            }

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun mkUsername(fullName: String) =
            fullName.toLowerCase().replace(" ", ".")

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
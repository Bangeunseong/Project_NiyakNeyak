package com.capstone.project_niyakneyak.login.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.project_niyakneyak.databinding.ActivityLoginBinding
import com.capstone.project_niyakneyak.login.viewmodel.LoginViewModel
import com.capstone.project_niyakneyak.login.viewmodel.LoginViewModelFactory
import com.capstone.project_niyakneyak.main.activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private val mGoogleSignInClient: GoogleSignInClient? = null
    private val mAuth: FirebaseAuth? = null
    private var loginViewModel: LoginViewModel? = null
    private var binding: ActivityLoginBinding? = null
    private val launcher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            handleSignInResult(data)
        }
    }

    companion object{
        private const val TAG = "GOOGLE_ACTIVITY"
        private const val default_web_client_id = "264335307721-6gi684ianko5vqjira4o4uqttlppl6ab.apps.googleusercontent.com"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]
        loginViewModel!!.loginFormState.observe(this, Observer { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }
            binding!!.mainLoginPlain.isEnabled = loginFormState.isDataValid
            if (loginFormState.usernameError != null) {
                binding!!.mainIdLayout.isErrorEnabled = true
                binding!!.mainIdLayout.error = getString(loginFormState.usernameError!!)
            } else binding!!.mainIdLayout.isErrorEnabled = false
            if (loginFormState.passwordError != null) {
                binding!!.mainPasswordLayout.isErrorEnabled = true
                binding!!.mainPasswordLayout.error = getString(loginFormState.passwordError!!)
            } else binding!!.mainPasswordLayout.isErrorEnabled = false
        })
        loginViewModel!!.loginResult.observe(this, Observer { loginResult ->
            if (loginResult == null) {
                return@Observer
            }
            binding!!.loadingBarPlain.visibility = View.GONE
            binding!!.loadingBarGoogle.visibility = View.GONE
            binding!!.loadingBarNaver.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser()
            }
        })
        val afterTextChanged: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                loginViewModel!!.loginDataChanged(
                    binding!!.mainIdEditText.text.toString(),
                    binding!!.mainPwEditText.text.toString()
                )
            }
        }
        binding!!.mainIdEditText.addTextChangedListener(afterTextChanged)
        binding!!.mainPwEditText.addTextChangedListener(afterTextChanged)
        binding!!.mainPwEditText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel!!.login(
                    binding!!.mainIdEditText.text.toString(),
                    binding!!.mainPwEditText.text.toString()
                )
            }
            false
        }
        binding!!.mainLoginPlain.setOnClickListener { v ->
            binding!!.loadingBarPlain.visibility = View.VISIBLE
            loginViewModel!!.login(
                binding!!.mainIdEditText.text.toString(),
                binding!!.mainPwEditText.text.toString()
            )
        }

        binding!!.mainLoginPlain.setOnClickListener {
            binding!!.loadingBarPlain.visibility = View.VISIBLE
            val email = binding!!.mainIdEditText.text.toString()
            val password = binding!!.mainPwEditText.text.toString()
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding!!.mainLoginGoogle.setOnClickListener {
            binding!!.loadingBarGoogle.visibility = View.VISIBLE
            signIn()
        }

        binding!!.mainRegisterBtn.setOnClickListener {
            //회원가입 화면으로 이동
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java) //현재 acitivity, 이동할 activity
            startActivity(intent) //액티비티 이동
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        currentUser?.let { updateUI(it) }
    }



    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in
            val userId = user.uid
            val userEmail = user.email
            // Perform actions based on the signed-in user, such as navigating to the main activity

            // Example: Navigate to MainActivity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish() // Optional: Close the login activity to prevent going back to it using the back button
        } else {
            signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        launcher.launch(signInIntent)
    }
    fun signOut() {
        Log.d(TAG, "signOut: " + mAuth!!.currentUser!!.email)
        mAuth.signOut()
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun handleSignInResult(data: Intent?) {
        if (data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                updateUI(null)
            }
        }
    }

    private fun updateUiWithUser() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("USER_ID", binding!!.mainIdEditText.text.toString())
        intent.putExtra("USER_PW", binding!!.mainPwEditText.text.toString())
        startActivityIfNeeded(intent, 0)
    }

    private fun showLoginFailed(@StringRes errorString: Int?) {
        Toast.makeText(applicationContext, errorString!!, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            loginViewModel!!.logout(data!!.getStringExtra("USER_ID"), data.getStringExtra("USER_PW"))
        }
    }
}
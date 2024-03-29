package com.capstone.project_niyakneyak.login.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.project_niyakneyak.databinding.ActivityLoginBinding
import com.capstone.project_niyakneyak.login.etc.LoggedInUserView
import com.capstone.project_niyakneyak.login.etc.LoginResult
import com.capstone.project_niyakneyak.login.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth


class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    private var gso: GoogleSignInOptions? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data
                handleGoogleSignInResult(data)
            }
        }
    private val signUpLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            loginViewModel.loginResult.value = LoginResult(LoggedInUserView(mAuth.currentUser!!.uid))
        }
    }

    companion object{
        private const val default_web_client_id = "264335307721-6gi684ianko5vqjira4o4uqttlppl6ab.apps.googleusercontent.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize GoogleSignInOptions and FirebaseAuth
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(default_web_client_id)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso!!)
        mAuth = Firebase.auth

        // Setting View by Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize LoginViewModel
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        // Observe LoginForm
        loginViewModel.loginFormState.observe(this, Observer { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }
            binding.mainLoginPlain.isEnabled = loginFormState.isDataValid
            if (loginFormState.usernameError != null) {
                binding.mainIdLayout.isErrorEnabled = true
                binding.mainIdLayout.error = getString(loginFormState.usernameError!!)
            } else binding.mainIdLayout.isErrorEnabled = false
            if (loginFormState.passwordError != null) {
                binding.mainPasswordLayout.isErrorEnabled = true
                binding.mainPasswordLayout.error = getString(loginFormState.passwordError!!)
            } else binding.mainPasswordLayout.isErrorEnabled = false
        })

        // Observe LoginResult
        loginViewModel.loginResult.observe(this, Observer { loginResult ->
            if (loginResult == null) return@Observer

            binding.loadingBarPlain.visibility = View.GONE
            binding.loadingBarGoogle.visibility = View.GONE
            binding.loadingBarNaver.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error!!)
            }
            if (loginResult.success != null) {
                setResult(RESULT_OK)
                finish()
            }
        })

        // Setting TextChangedListener in Id and Pw EditText
        val afterTextChanged: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(binding.mainIdEditText.text.toString(), binding.mainPwEditText.text.toString())
            }
        }
        binding.mainIdEditText.addTextChangedListener(afterTextChanged)
        binding.mainPwEditText.addTextChangedListener(afterTextChanged)
        binding.mainPwEditText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                binding.mainLoginPlain.callOnClick()
            false
        }

        // Setting onClickListener in each button
        // Login Process
        binding.mainLoginPlain.setOnClickListener {
            binding.loadingBarPlain.visibility = View.VISIBLE
            emailSignIn()
        }
        binding.mainLoginGoogle.setOnClickListener {
            binding.loadingBarGoogle.visibility = View.VISIBLE
            googleSignIn()
        }
        // Register Process
        binding.mainRegisterBtn.setOnClickListener {
            // 회원가입 화면으로 이동
            val intent = Intent(this, RegisterActivity::class.java)
            signUpLauncher.launch(intent)
        }
    }

    // Handle Email SignIn Process
    private fun emailSignIn(){
        val email = binding.mainIdEditText.text.toString()
        val password = binding.mainPwEditText.text.toString()
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Change LiveData about LoginResult as Success
                loginViewModel.loginResult.value = LoginResult(LoggedInUserView(mAuth.currentUser!!.uid))
            } else {
                // Change LiveData about LoginResult as Failed
                loginViewModel.loginResult.value = LoginResult(task.exception)
            }
        }
    }

    // Handle Google SignIn Process
    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Change LiveData about LoginResult as Success
                    loginViewModel.loginResult.value = LoginResult(LoggedInUserView(mAuth.currentUser!!.uid))
                } else {
                    // Change LiveData about LoginResult as Failed
                    loginViewModel.loginResult.value = LoginResult(task.exception)
                }
            }
    }
    private fun handleGoogleSignInResult(data: Intent?) {
        if (data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                loginViewModel.loginResult.value = LoginResult(e)
            }
        }
    }

    // Handle Login Failure
    private fun showLoginFailed(errorString: Exception) {
        Toast.makeText(this, errorString.toString(), Toast.LENGTH_SHORT).show()
    }
}
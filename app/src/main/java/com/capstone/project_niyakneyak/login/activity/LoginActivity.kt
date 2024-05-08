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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.project_niyakneyak.alarm.service.RescheduleAlarmService
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.user_model.UserAccount
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
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class LoginActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
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
            loginViewModel.loginResult.value = LoginResult(LoggedInUserView(firebaseAuth.currentUser!!.uid))
        }
    }

    companion object{
        private const val default_web_client_id = "264335307721-6gi684ianko5vqjira4o4uqttlppl6ab.apps.googleusercontent.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestToken = intent.getIntExtra("request_token", 0)

        // Initialize GoogleSignInOptions and FirebaseAuth
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(default_web_client_id)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso!!)

        firestore = Firebase.firestore
        firebaseAuth = Firebase.auth

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
                firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID).where(Filter.equalTo(Alarm.FIELD_IS_STARTED, true)).get()
                    .addOnSuccessListener {
                        for(snapshot in it.documents){
                            val alarm = snapshot.toObject<Alarm>() ?: continue
                            alarm.scheduleAlarm(applicationContext)
                        }
                        if(requestToken == 1){
                            val intentService = Intent(applicationContext, RescheduleAlarmService::class.java)
                            applicationContext.stopService(intentService)
                        }
                        setResult(RESULT_OK)
                        finish()
                    }.addOnFailureListener {
                        Log.w("RescheduleService", "Reschedule Failed: $it")
                        setResult(RESULT_OK)
                        finish()
                    }
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
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Change LiveData about LoginResult as Success
                loginViewModel.loginResult.value = LoginResult(LoggedInUserView(firebaseAuth.currentUser!!.uid))
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
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Change LiveData about LoginResult as Success
                    loginViewModel.loginResult.value = LoginResult(LoggedInUserView(firebaseAuth.currentUser!!.uid))
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
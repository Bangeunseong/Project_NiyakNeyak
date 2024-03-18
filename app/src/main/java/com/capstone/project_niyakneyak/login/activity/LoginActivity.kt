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
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.project_niyakneyak.databinding.ActivityLoginBinding
import com.capstone.project_niyakneyak.login.viewmodel.LoginViewModel
import com.capstone.project_niyakneyak.login.viewmodel.LoginViewModelFactory
import com.capstone.project_niyakneyak.main.activity.MainActivity

class LoginActivity : AppCompatActivity() {
    private var loginViewModel: LoginViewModel? = null
    private var binding: ActivityLoginBinding? = null
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
        binding!!.mainLoginPlain.setOnClickListener {
            binding!!.loadingBarPlain.visibility = View.VISIBLE
            loginViewModel!!.login(
                binding!!.mainIdEditText.text.toString(),
                binding!!.mainPwEditText.text.toString()
            )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            loginViewModel!!.logout(
                data!!.getStringExtra("USER_ID"),
                data.getStringExtra("USER_PW")
            )
        }
    }
}
package com.capstone.project_niyakneyak.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.LoginRepository;
import com.capstone.project_niyakneyak.databinding.ActivityLoginBinding;
import com.capstone.project_niyakneyak.ui.login.LoggedInUserView;
import com.capstone.project_niyakneyak.ui.login.LoginFormState;
import com.capstone.project_niyakneyak.ui.login.LoginResult;
import com.capstone.project_niyakneyak.ui.login.LoginViewModel;
import com.capstone.project_niyakneyak.ui.login.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                binding.mainLoginPlain.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    binding.mainIdLayout.setErrorEnabled(true);
                    binding.mainIdLayout.setError(getString(loginFormState.getUsernameError()));
                }
                else binding.mainIdLayout.setErrorEnabled(false);

                if (loginFormState.getPasswordError() != null) {
                    binding.mainPasswordLayout.setErrorEnabled(true);
                    binding.mainPasswordLayout.setError(getString(loginFormState.getPasswordError()));
                }
                else binding.mainPasswordLayout.setErrorEnabled(false);
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                binding.loadingBarPlain.setVisibility(View.GONE);
                binding.loadingBarGoogle.setVisibility(View.GONE);
                binding.loadingBarNaver.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(binding.mainIdEditText.getText().toString(),
                        binding.mainPwEditText.getText().toString());
            }
        };

        binding.mainIdEditText.addTextChangedListener(afterTextChanged);
        binding.mainPwEditText.addTextChangedListener(afterTextChanged);
        binding.mainPwEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    loginViewModel.login(binding.mainIdEditText.getText().toString(),
                            binding.mainPwEditText.getText().toString());
                }
                return false;
            }
        });

        binding.mainLoginPlain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.loadingBarPlain.setVisibility(View.VISIBLE);
                loginViewModel.login(binding.mainIdEditText.getText().toString(),
                        binding.mainPwEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
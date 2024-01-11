package com.capstone.project_niyakneyak.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.capstone.project_niyakneyak.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        menu.add(0,0,0,R.string.action_menu_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 0:
                //TODO: Need to modify (Using AlertDialog should be enough to give users language options)
                Toast toast = Toast.makeText(LoginActivity.this, "Need to replace with function", Toast.LENGTH_LONG);
                toast.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbarLogin.setTitle(R.string.toolbar_login_title);
        setSupportActionBar(binding.toolbarLogin);

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
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USER_ID", binding.mainIdEditText.getText().toString());
        intent.putExtra("USER_PW", binding.mainPwEditText.getText().toString());

        binding.mainIdEditText.setText("");
        binding.mainPwEditText.setText("");

        startActivityIfNeeded(intent, 0);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            loginViewModel.logout(data.getStringExtra("USER_ID"), data.getStringExtra("USER_PW"));
        }
    }
}
package com.capstone.project_niyakneyak.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.databinding.ActivityLoginBinding;
import com.capstone.project_niyakneyak.ui.main.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private static final int RC_SIGN_IN = 10;
    private static final String TAG = "GoogleActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;


    String default_web_client_id = "264335307721-6gi684ianko5vqjira4o4uqttlppl6ab.apps.googleusercontent.com";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

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
    public void getGoogleCredentials() {
        String googleIdToken = "";
        // [START auth_google_cred]
        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
        // [END auth_google_cred]
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());//바인딩은 xml에 있는 뷰를 객체화 시켜줌
        setContentView(binding.getRoot());
        binding.toolbarLogin.setTitle(R.string.toolbar_login_title);
        setSupportActionBar(binding.toolbarLogin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(default_web_client_id)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

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
                String email = binding.mainIdEditText.getText().toString();
                String password = binding.mainPwEditText.getText().toString();

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        binding.mainLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.loadingBarGoogle.setVisibility(View.VISIBLE);
                signIn();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); //현재 acitivity, 이동할 activity
                startActivity(intent);//액티비티 이동
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USER_ID", binding.mainIdEditText.getText().toString());
        intent.putExtra("USER_PW", binding.mainPwEditText.getText().toString());
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

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in
            String userId = user.getUid();
            String userEmail = user.getEmail();
            // Perform actions based on the signed-in user, such as navigating to the main activity

            // Example: Navigate to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
            finish(); // Optional: Close the login activity to prevent going back to it using the back button
        } else {
            // User is signed out
            // Implement actions for when the user is signed out, such as displaying a login screen
            // or showing a message that the user needs to sign in.
            signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    handleSignInResult(data);
                }
            });

    // 사용할 곳에서 다음과 같이 사용
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        launcher.launch(signInIntent);
    }

    private void handleSignInResult(Intent data) {
        if (data != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    public void signOut() {
        // [START auth_sign_out]
        Log.d(TAG, "signOut: " + mAuth.getCurrentUser().getEmail());
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]
    }

}
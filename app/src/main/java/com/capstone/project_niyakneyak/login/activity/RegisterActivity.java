package com.capstone.project_niyakneyak.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.user_model.UserAccount ;
import com.capstone.project_niyakneyak.main.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스, 서버에 연동시키는 객체
    private EditText mEtEmail, mEtPassword, mEtName, mEtPhoneNum; //회원가입 입력필드
    private Button mBtnRegister;// 회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_register.xml을 바인딩합니다.
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("niyakneyak");

        mEtEmail = findViewById(R.id.et_email);
        mEtPassword = findViewById(R.id.et_password);
        mEtName = findViewById(R.id.et_name);
        mEtPhoneNum = findViewById(R.id.et_phoneNum);

        mBtnRegister = findViewById(R.id.btn_register); // Initialize mBtnRegister

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPassword.getText().toString();

                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {//회원가입 성공
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();//인증객체에서 현재의 유저를 가져옴
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPwd);
                            account.setName(mEtName.getText().toString());
                            account.setPhoneNum(mEtPhoneNum.getText().toString());

                            //니약내약의 하위개념으로 정보를 넣음
                            //setValue를 통해 데이터베이스에 삽입
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                            //가입이 이루어졌을 때, 가입 정보를 데이터베이스에 저장

                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                            task.getException().getMessage();
                            Log.d("RegisterActivity", "Register Failed");
                        }
                    }
                });

            }
        });
    }
}

package com.capstone.project_niyakneyak.ui.main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.ui.login.activity.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PracticeActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef;// 실시간 데이터베이스

    private EditText mEtEmail, mEtPwd;
    private Button mBtnRegister;

    public PracticeActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        mFirebaseAuth= FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference();

        mEtEmail= findViewById(R.id.et_email_sun);
        mEtPwd= findViewById(R.id.et_pwd_sun);
        mBtnRegister= findViewById((R.id.btn_reg_sun));

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = mEtEmail.getText().toString();//  문자열로 번경된 입력값임
                String strPwd = mEtPwd.getText().toString();//

                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(PracticeActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                        }
                    }
                });//첫번쨰는 이메일 두번째는 비밀번호
            }
        });

    }
}
package com.techspark.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailet, passwordet;
    Button loginbtn;
    ProgressBar progressBar;
    TextView registerbtntv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailet = findViewById(R.id.email_et_login);
        passwordet = findViewById(R.id.password_et_login);
        loginbtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        registerbtntv = findViewById(R.id.register_textview);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();

            }
            void loginUser(){
                String email = emailet.getText().toString();
                String password = passwordet.getText().toString();

                boolean isValidated = validateData(email,password);

                if(!isValidated){
                    return;
                }

                loginAccountInFirebase(email,password);

            }

            void loginAccountInFirebase(String email,String password){
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                changeInProgress(true);
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            // login is success
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                // go to main activity
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }
                            else{
                                Utility.showToast(LoginActivity.this,"Email not verified, Please verify your email.");
                            }
                        }
                        else{
                            // login failed
                            Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());
                        }
                    }
                });
            }

            void changeInProgress(boolean inProgress){
                if(inProgress){
                    progressBar.setVisibility(View.VISIBLE);
                    loginbtn.setVisibility(View.GONE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    loginbtn.setVisibility(View.VISIBLE);
                }
            }

            boolean validateData(String email,String password){
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailet.setError("Email is invalid");
                    return false;
                }
                if(password.length()<6){
                    passwordet.setError("Password length is invalid");
                    return false;
                }
                return true;
            }
        });

        registerbtntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterationActivity.class));
            }
        });


    }
}
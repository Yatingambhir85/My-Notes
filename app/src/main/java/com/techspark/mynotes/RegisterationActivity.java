package com.techspark.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterationActivity extends AppCompatActivity {

    EditText emailet, passwordet, cpasswordet;
    Button createbtn;
    ProgressBar progressBar;
    TextView loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        emailet = findViewById(R.id.email_et);
        passwordet = findViewById(R.id.password_et);
        cpasswordet = findViewById(R.id.confirmpassword_et);
        createbtn = findViewById(R.id.createacount_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginbtn = findViewById(R.id.login_textview);

        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailet.getText().toString();
                String password = passwordet.getText().toString();
                String cpassword = cpasswordet.getText().toString();

                boolean isValidated = validateData(email,password,cpassword);
                
                if(!isValidated){
                    return;
                }
                
                createAccountInFirebase(email,password);
                
            }
            
            void createAccountInFirebase(String email,String password){
                     changeInProgress(true);

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterationActivity.this,
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            // create account is done
                            Utility.showToast(RegisterationActivity.this,"Successfully created account, Check email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else{
                            // failure
                            Utility.showToast(RegisterationActivity.this,task.getException().getLocalizedMessage());
                        }
                    }
                });

            }
            
            void changeInProgress(boolean inProgress){
                if(inProgress){
                    progressBar.setVisibility(View.VISIBLE);
                    createbtn.setVisibility(View.GONE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    createbtn.setVisibility(View.VISIBLE);
                }
            }
            
            boolean validateData(String email,String password,String cpassword){
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailet.setError("Email is invalid");
                    return false;
                }
                if(password.length()<6){
                    passwordet.setError("Password length is invalid");
                    return false;
                }
                if(!password.equals(cpassword)){
                    passwordet.setError("Password not matched");
                    return false;
                }
                return true;
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
package com.romitMohane.mathtrainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginOrRegister extends AppCompatActivity {

    private Button btnlogin,btnUsername;
    private TextView tvRegister,tvHead,tvError;
    private EditText etEmail,etPass,etUsername;
    private String username;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUsers;
    private FirebaseUser firebaseUser;

    Dialog usernamePopup;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseUsers= FirebaseDatabase.getInstance().getReference("users");

        usernamePopup= new Dialog(this);
        progressDialog=new ProgressDialog(this);

        if (firebaseUser!=null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
            Toast.makeText(this,"Logged in",Toast.LENGTH_SHORT).show();
        }

        getSupportActionBar().setTitle("Login to Math Trainer");
        btnlogin=findViewById(R.id.btnLogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnlogin.getText().equals("Login"))
                    login();
                else if (btnlogin.getText().equals("Register"))
                    register();
            }
        });
        tvRegister=findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToRegistration();
            }
        });
        tvHead=findViewById(R.id.tvHead);
        tvError=findViewById(R.id.tvError);
        etEmail=findViewById(R.id.etEmailAddress);
        etPass=findViewById(R.id.etPassword);


    }

    private void register() {
        String email=etEmail.getText().toString().trim();
        String password=etPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            tvError.setText("Enter an Email!");
            return;
        }
        if (TextUtils.isEmpty(password)){
            tvError.setText("Enter a Password!");
            return;
        }
        //not empty
        progressDialog.setMessage("Registering..");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                           takeUsername();
                        }
                        else
                            tvError.setText("Enter valid Email!");
                    }
                });
    }

    private void login() {
        String email=etEmail.getText().toString().trim();
        String password=etPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            tvError.setText("Enter an Email!");
            return;
        }
        if (TextUtils.isEmpty(password)){
            tvError.setText("Enter a Password!");
            return;
        }
        //not empty
        progressDialog.setMessage("Logging in..");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(),"Login Successful!",Toast.LENGTH_SHORT).show();
                        }
                        else
                            tvError.setText("Enter valid Credentials!");
                    }
                });
    }

    private void takeUsername(){
        usernamePopup.setContentView(R.layout.username_popup);
        etUsername=usernamePopup.findViewById(R.id.etUsername);
        btnUsername=usernamePopup.findViewById(R.id.btnUsername);
        btnUsername.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etUsername.getText().toString())){
                Toast.makeText(getApplicationContext(),"Enter a name!",Toast.LENGTH_SHORT).show();
                return;
            }
            username=etUsername.getText().toString().trim();
            for (int i=0;i<username.length();i++){
                if (username.charAt(i)=='.'||username.charAt(i)=='$'||username.charAt(i)=='['||username.charAt(i)==']'||username.charAt(i)=='#'||username.charAt(i)=='/'){
                    Toast.makeText(getApplicationContext(),"Invalid Characters!",Toast.LENGTH_SHORT).show();
                    etUsername.setText("");
                    return;
                }
            }
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profile =new UserProfileChangeRequest.Builder()
                    .setDisplayName(username).build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                usernamePopup.dismiss();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                                Toast.makeText(getApplicationContext(),"Registration Successful!",Toast.LENGTH_SHORT).show();
                                addUser();
                            }
                            else
                                tvError.setText("Invalid Username");
                        }
                    });
        });
        usernamePopup.show();
        usernamePopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        usernamePopup.setCanceledOnTouchOutside(false);
        usernamePopup.setCancelable(false);
    }

    private void changeToRegistration(){
        if (tvRegister.getText().equals("Don't have an Account? Register here!")) {
            getSupportActionBar().setTitle("Register to Math Trainer");
            btnlogin.setText("Register");
            etPass.setHint("Create a Password");
            tvRegister.setText("Already have an account? Login here!");
            tvHead.setText("Register");
        }
        else if (tvRegister.getText().equals("Already have an account? Login here!")){
            getSupportActionBar().setTitle("Login to Math Trainer");
            btnlogin.setText("Login");
            etPass.setHint("Password");
            tvRegister.setText("Don't have an Account? Register here!");
            tvHead.setText("Login");
        }
    }
    private void addUser(){
        firebaseUser=firebaseAuth.getCurrentUser();
        String email =firebaseUser.getEmail();
        String username=firebaseUser.getDisplayName();
        User user =new User(email,username,"0","0","0","0","0");
        databaseUsers.child(firebaseUser.getUid()).setValue(user);
    }
}
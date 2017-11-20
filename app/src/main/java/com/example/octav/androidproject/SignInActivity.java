package com.example.octav.androidproject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignInActivity extends AppCompatActivity {

    private final String AUTH_TAG = "auth_log";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mPasswordField;
    private EditText mEmailField;
    private Button mSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mPasswordField = (EditText) findViewById(R.id.password);
        mEmailField = (EditText) findViewById(R.id.email);
        mSignInBtn = (Button) findViewById(R.id.signIn);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed it
                    Toast.makeText(SignInActivity.this, "User has signed in successfully",
                            Toast.LENGTH_LONG).show();
                    updateUi(user);
                    Log.d(AUTH_TAG, "onAuthStateChanged: singed_in:" + user.getUid());
                } else {
                    //User is signed out
                    Log.d(AUTH_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mSignInBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = String.valueOf(mEmailField.getText());
                String password = String.valueOf(mPasswordField.getText());

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignInActivity.this, "Email and password should not be empty",
                            Toast.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(AUTH_TAG, "signInWithEmail: onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        Log.w(AUTH_TAG, "signInWithEmail:failed", task.getException());
                                        Toast.makeText(SignInActivity.this, "Sign in failed",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    private void updateUi(FirebaseUser user) {
        mEmailField.setText("");
        mPasswordField.setText("");

        startActivity(new Intent(SignInActivity.this, MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
            updateUi(user);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }
}

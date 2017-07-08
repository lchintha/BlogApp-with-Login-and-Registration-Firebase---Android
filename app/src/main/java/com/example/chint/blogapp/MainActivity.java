package com.example.chint.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button signin, signup;
    private FirebaseAuth fbAuth;
    private ProgressDialog progress;
    private FirebaseAuth.AuthStateListener fbListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fbAuth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);
        fbListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        };
        signin = (Button)findViewById(R.id.signin);
        signup = (Button)findViewById(R.id.signup);

        email = (EditText)findViewById(R.id.enteremail);
        password = (EditText)findViewById(R.id.enterepassword);
    }
    public void onClicked(View view){
        int id = view.getId();
        if(id==R.id.signin) {
            String eMail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (!TextUtils.isEmpty(eMail) && !TextUtils.isEmpty(pass)) {
                progress.setMessage("Loging In...");
                progress.show();
                fbAuth.signInWithEmailAndPassword(eMail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progress.dismiss();
                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                        else {
                            progress.dismiss();
                            Toast.makeText(MainActivity.this, "Failed to Login...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
                Toast.makeText(this, "Please Fill all Fields", Toast.LENGTH_LONG).show();
        }
        if(id==R.id.signup){
            startActivity(new Intent(this, RegistrationActivity.class));
        }
    }
}

package com.example.chint.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText name, email, phone, password;
    private FirebaseAuth fbAuth;
    private DatabaseReference dbreference;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fbAuth = FirebaseAuth.getInstance();
        dbreference = FirebaseDatabase.getInstance().getReference().child("Users");

        name = (EditText)findViewById(R.id.rname);
        email = (EditText)findViewById(R.id.remail);
        phone = (EditText)findViewById(R.id.rmobile);
        password = (EditText)findViewById(R.id.rpass);

        progress = new ProgressDialog(this);
    }

    public void onRegistrationClicked(View view){
        final String rname = name.getText().toString().trim();
        String rpass = password.getText().toString().trim();
        String remail = email.getText().toString().trim();
        final String rmobile = phone.getText().toString().trim();

        if(!TextUtils.isEmpty(rname) && !TextUtils.isEmpty(rpass) && !TextUtils.isEmpty(remail) && !TextUtils.isEmpty(rmobile)){
            progress.setMessage("Signing UP.....");
            progress.show();

            fbAuth.createUserWithEmailAndPassword(remail, rpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String userid = fbAuth.getCurrentUser().getUid();
                        DatabaseReference currentuser = dbreference.child(userid);
                        currentuser.child("name").setValue(rname);
                        currentuser.child("mobile").setValue(rmobile);
                        currentuser.child("profilepic").setValue("ProfilePIC");
                        progress.dismiss();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        Toast.makeText(RegistrationActivity.this, "Please SignIn...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progress.dismiss();
                        Toast.makeText(RegistrationActivity.this, "Failed to SignUP", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Please Fill All Fields...", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.chint.blogapp;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddPost extends AppCompatActivity {
    Button addpost;
    ImageButton addimage;
    EditText title, desc;
    public FirebaseAuth fbAuth;
    public final int IMAGE_REQUEST_CODE = 1;
    public Uri imageUri;
    private StorageReference sreference;
    private DatabaseReference dbreference;
    private FirebaseAuth.AuthStateListener fbListener;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        progress = new ProgressDialog(this);
        sreference = FirebaseStorage.getInstance().getReference();
        fbAuth = FirebaseAuth.getInstance();
        dbreference = FirebaseDatabase.getInstance().getReference().child("Blogs");
        addpost = (Button)findViewById(R.id.addpost);
        addimage = (ImageButton)findViewById(R.id.addimage);
        title = (EditText)findViewById(R.id.entertitle);
        desc = (EditText)findViewById(R.id.enterdescription);

        fbAuth = FirebaseAuth.getInstance();
        fbListener = new FirebaseAuth.AuthStateListener() {
            Intent i;
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    i = new Intent(AddPost.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        fbAuth.addAuthStateListener(fbListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onClicked(View v){
        int id = v.getId();
        if(id==R.id.addpost) {
            final String stitle = title.getText().toString().trim();
            final String sdesc = desc.getText().toString().trim();

            if(!TextUtils.isEmpty(stitle) && !TextUtils.isEmpty(sdesc) && imageUri != null){
                progress.setMessage("Posting to your Blog...");
                progress.show();
                StorageReference path = sreference.child("Blog_images").child(imageUri.getLastPathSegment());
                path.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        DatabaseReference newPost = dbreference.push();
                        newPost.child("title").setValue(stitle);
                        newPost.child("description").setValue(sdesc);
                        newPost.child("image").setValue(downloadUrl.toString());
                        progress.dismiss();
                        Intent i = new Intent(AddPost.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        //startActivity(new Intent(AddPost.this, HomeActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.dismiss();
                        Toast.makeText(AddPost.this, "Failed to Post", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
                Toast.makeText(this, "Enter all the Fields...", Toast.LENGTH_SHORT).show();

        }
        if(id == R.id.addimage){
            askPermission(Manifest.permission.READ_EXTERNAL_STORAGE,IMAGE_REQUEST_CODE);
        }
    }


    private void askPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please go to Setting & Change Permissions", Toast.LENGTH_LONG).show();
            }
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        }else{
            Intent galleryAccess = new Intent(Intent.ACTION_GET_CONTENT);
            galleryAccess.setType("image/*");
            startActivityForResult(galleryAccess, IMAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            imageUri = data.getData();
            addimage.setImageURI(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            /*Intent i = new Intent(AddPost.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);*/
            fbAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}

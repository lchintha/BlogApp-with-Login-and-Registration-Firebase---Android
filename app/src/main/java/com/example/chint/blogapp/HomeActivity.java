package com.example.chint.blogapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView rview;
    private DatabaseReference dbreference;
    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener fbListener;
    //private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbreference = FirebaseDatabase.getInstance().getReference().child("Blogs");
        //storageReference = FirebaseStorage.getInstance().getReference();
        rview = (RecyclerView)findViewById(R.id.rview);
        rview.setHasFixedSize(true);
        rview.setLayoutManager(new LinearLayoutManager(this));
        //dbreference.keepSynced(true);

        fbAuth = FirebaseAuth.getInstance();
        fbListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(fbListener);
        FirebaseRecyclerAdapter<BlogElements, BlogViewHolder> fbradapter = new FirebaseRecyclerAdapter<BlogElements, BlogViewHolder>(
                BlogElements.class,
                R.layout.layout_model,
                BlogViewHolder.class,
                dbreference
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, BlogElements model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            }
        };
        rview.setAdapter(fbradapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View view;
        public BlogViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setTitle(String title){
            TextView btitle = (TextView)view.findViewById(R.id.posttitle);
            btitle.setText(title);
        }
        public void setDescription(String desc){
            TextView bdesc = (TextView)view.findViewById(R.id.postdescription);
            bdesc.setText(desc);
        }
        public void setImage(final Context ctx, final String image){
            final ImageView bimage = (ImageView) view.findViewById(R.id.postimage);

            StorageReference test = FirebaseStorage.getInstance().getReference()
                    .child("Blog_images/08E7-231F:332EE38400000578-4125738-image-a-132_1484600112489.jpg");
            test.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(ctx).load(image).into(bimage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ctx, "Failed To Load Posts...", Toast.LENGTH_SHORT).show();
                }
            });

         //  Picasso.with(ctx).load(image).into(bimage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            fbAuth.signOut();
        }
        if(id == R.id.addpost){
            startActivity(new Intent(this, AddPost.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

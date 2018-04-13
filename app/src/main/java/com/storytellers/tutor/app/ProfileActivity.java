package com.storytellers.tutor.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.storytellers.tutor.app.Utils.CircleTransform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_REQUEST_CODE = 1;
    EditText userDesc, userKnows;
    ImageView userImage;
    Button saveButton;
    Toolbar toolbar;
    TextView userName;
    ProgressBar upload;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("users/profile-images");

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        userId = mAuth.getCurrentUser().getUid();
        databaseReference = database.getReference("users/" + userId);

        userDesc = findViewById(R.id.profile_desc);
        userKnows = findViewById(R.id.profile_knows);
        userImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.username);
        upload = findViewById(R.id.image_upload);

        upload.setVisibility(View.GONE);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDesc.setText(dataSnapshot.child("description").getValue(String.class));
                userKnows.setText(dataSnapshot.child("knows").getValue(String.class));
                userName.setText(dataSnapshot.child("name").getValue(String.class));

                Picasso.get().load(dataSnapshot.child("image").getValue(String.class))
                        .transform(new CircleTransform())
                        .error(R.drawable.ic_user).placeholder(R.drawable.ic_user).into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_REQUEST_CODE);
                }
            }
        });

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userDescText = userDesc.getText().toString().trim();
                String userKnowsText = userKnows.getText().toString().trim();

                saveUserData(userDescText, userKnowsText);
            }
        });
    }

    private void saveUserData(String userDescText, String userKnowsText) {
        databaseReference.child("description").setValue(userDescText);
        databaseReference.child("knows").setValue(userKnowsText);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            upload.setVisibility(View.VISIBLE);
            Uri uri = data.getData();
            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byte[] dataBytes = byteArrayOutputStream.toByteArray();

                final UploadTask uploadTask = storageReference.child(userId).putBytes(dataBytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        upload.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        upload.setVisibility(View.GONE);
                        databaseReference.child("image").setValue(taskSnapshot.getDownloadUrl().toString());
                        SharedPreferences sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("image", taskSnapshot.getDownloadUrl().toString());
                        editor.apply();
                        Picasso.get().load(taskSnapshot.getDownloadUrl().toString())
                                .transform(new CircleTransform())
                                .error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                                .into(userImage);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("desc", userDesc.getText().toString().trim());
        outState.putString("knows", userKnows.getText().toString().trim());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        userDesc.setText(savedInstanceState.getString("desc"));
        userKnows.setText(savedInstanceState.getString("knows"));
    }
}

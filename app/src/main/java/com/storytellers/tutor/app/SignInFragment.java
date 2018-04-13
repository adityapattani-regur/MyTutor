package com.storytellers.tutor.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class SignInFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TextInputLayout email, password;
    Button loginBtn;
    ProgressBar progressBar;

    SharedPreferences sharedPreferences;

    String username_str, image_str;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("users");

    public SignInFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signin_layout, container, false);
        email = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.login_button);
        progressBar = view.findViewById(R.id.progress);

        progressBar.setVisibility(View.INVISIBLE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_str = email.getEditText().getText().toString().trim();
                String password_str = password.getEditText().getText().toString().trim();

                if(!email_str.isEmpty()) {
                    if(!password_str.isEmpty()) {
                        signUserIn(email_str, password_str);
                    } else {
                        password.setError("Password cannot be null");
                    }
                } else {
                    email.setError("Email cannot be null");
                }
            }
        });

        return view;
    }

    private void signUserIn(final String email_str, String password_str) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email_str, password_str)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot users : dataSnapshot.getChildren()) {
                                        if (users.getKey().equals(user.getUid())) {
                                            username_str = users.child("name").getValue(String.class);
                                            image_str = users.child("image").getValue(String.class);
                                            Log.e("Sign In", username_str);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("name", username_str);
                                            editor.putString("email", email_str);
                                            editor.putString("image", image_str);
                                            editor.apply();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("Error", databaseError.getDetails());
                                }
                            });

                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

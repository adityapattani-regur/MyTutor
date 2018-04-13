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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.storytellers.tutor.app.Utils.CheckerUtils;

import static android.content.ContentValues.TAG;

public class SignUpFragment extends Fragment {

    TextInputLayout username, email, password, confPassword;
    Button signUpBtn;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("users");

    private SharedPreferences sharedPreferences;

    public SignUpFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_layout, container, false);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confPassword = view.findViewById(R.id.conf_password);
        signUpBtn = view.findViewById(R.id.signup_button);
        progressBar = view.findViewById(R.id.progress);

        progressBar.setVisibility(View.INVISIBLE);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_str = username.getEditText().getText().toString().trim();
                String email_str = email.getEditText().getText().toString().trim();
                String password_str = password.getEditText().getText().toString().trim();
                String confPassword_str = confPassword.getEditText().getText().toString().trim();

                if(CheckerUtils.isEmailValid(email_str)){
                    if (password_str.equals(confPassword_str)){
                        if (!username_str.isEmpty()) {
                            progressBar.setVisibility(View.VISIBLE);
                            signUserUp(username_str, email_str, password_str);
                        } else {
                            username.setError("Username cannot be null");
                        }
                    } else {
                        password.setError("Please check if both the passwords match");
                        confPassword.setError("Please check if both the passwords match");
                    }
                } else {
                    email.setError("Not a valid email");
                }
            }
        });
        return view;
    }

    private void signUserUp(final String username_str, final String email_str, String password_str) {
        mAuth.createUserWithEmailAndPassword(email_str, password_str)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "createUserWithEmail:success");
                            Toast.makeText(getActivity(), "Sign Up successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            reference.child(user.getUid()).child("id").setValue(user.getUid());
                            reference.child(user.getUid()).child("name").setValue(username_str);
                            reference.child(user.getUid()).child("email").setValue(email_str);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", username_str);
                            editor.putString("email", email_str);
                            editor.putString("image", null);
                            editor.putBoolean("firstUse", true);
                            editor.apply();

                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

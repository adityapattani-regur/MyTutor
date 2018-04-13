package com.storytellers.tutor.app;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.storytellers.tutor.app.Utils.CircleTransform;

import java.util.Calendar;
import java.util.Objects;

public class AddReviewActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView reviewCharacters, userName;
    EditText reviewDesc;
    Button submitButton;
    ImageView reviewImage;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("reviews");

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);

        final Tutor tutor = (Tutor) getIntent().getSerializableExtra("tutor");

        final String userId = mAuth.getCurrentUser().getUid();
        final String username = sharedPreferences.getString("name", "");
        final String userImage = sharedPreferences.getString("image", null);

        reviewCharacters = findViewById(R.id.review_chars);
        reviewDesc = findViewById(R.id.review);
        userName = findViewById(R.id.username);
        reviewImage = findViewById(R.id.user_image);
        submitButton = findViewById(R.id.submit_review);

        userName.setText(tutor.getName());
        Picasso.get().load(tutor.getImage())
                .transform(new CircleTransform())
                .error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                .into(reviewImage);

        reviewDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String length = s.length() + "/250";
                reviewCharacters.setText(length);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review_text = reviewDesc.getText().toString().trim();

                Review review = new Review(tutor.getId(), tutor.getName(), userId, username, userImage, review_text);
                addReviewToDatabase(review);
            }
        });
    }

    private void addReviewToDatabase(Review review) {
        String timeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());
        reference.child(review.getTutorId() + "-" + timeInMillis).setValue(review);
        finish();
    }
}

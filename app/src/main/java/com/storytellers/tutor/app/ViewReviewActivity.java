package com.storytellers.tutor.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ViewReviewActivity extends AppCompatActivity {

    TextView reviewText, reviewTutor, reviewerName;
    ImageView reviewImage;
    Toolbar toolbar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        Review review = (Review) intent.getSerializableExtra("review");

        reviewText = findViewById(R.id.review_value);
        reviewTutor = findViewById(R.id.view_review_name);
        reviewImage = findViewById(R.id.view_review_image);
        reviewerName = findViewById(R.id.view_reviewer_name);

        reference = database.getReference("reviews/" + review.getReviewId());

        reviewText.setText(review.getDesc());
        reviewTutor.setText(review.getTutorName());
        reviewerName.setText(review.getUserName());
        Picasso.get().load(review.getUserImage()).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(reviewImage);
    }
}

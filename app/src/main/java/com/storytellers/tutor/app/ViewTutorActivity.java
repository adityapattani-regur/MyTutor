package com.storytellers.tutor.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.storytellers.tutor.app.Utils.CircleTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ViewTutorActivity extends AppCompatActivity implements ReviewAdapter.ReviewAdapterListener {

    Toolbar toolbar;
    TextView title;
    Button contactButton, addReviewBtn;
    RecyclerView userReviews, tutorKnows;
    TextView tutorDesc, tutorRecoms, emptyTestimonials;
    ImageView tutorImage;
    Tutor tutor;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("reviews");

    SharedPreferences sharedPreferences;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutor);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        tutor = (Tutor) intent.getSerializableExtra("tutor");

        title = findViewById(R.id.username);
        title.setText(tutor.getName());

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        username = sharedPreferences.getString("name", null);

        final String tutorId = tutor.getId();

        tutorImage = findViewById(R.id.tutor_image);
        emptyTestimonials = findViewById(R.id.empty_testimonials);

        Picasso.get().load(tutor.getImage()).transform(new CircleTransform())
                .error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                .into(tutorImage);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        userReviews = findViewById(R.id.tutor_reviews_list);
        tutorKnows = findViewById(R.id.tutor_knows_list);

        emptyTestimonials.setVisibility(View.VISIBLE);
        userReviews.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        tutorKnows.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerForReviews = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        userReviews.setLayoutManager(layoutManagerForReviews);

        tutorDesc = findViewById(R.id.tutor_desc);

        if (tutor.getDescription() == null || tutor.getDescription().length() == 0) {
            tutorDesc.setText(getResources().getString(R.string.no_desc_added));
        } else {
            tutorDesc.setText(tutor.getDescription());
        }

        tutorRecoms = findViewById(R.id.tutor_recom);
        String recoms = tutor.getRecommendations() + " users";
        tutorRecoms.setText(recoms);

        if (tutor.getKnows() == null || tutor.getKnows().length() == 0){
            List<String> knows = new ArrayList<>();
            knows.add("No data added");
            KnowsAdapter knowsAdapter = new KnowsAdapter(getApplicationContext(), knows);
            tutorKnows.setAdapter(knowsAdapter);
        } else {
            List<String> knows = new ArrayList<>(Arrays.asList(tutor.getKnows().split(",")));
            KnowsAdapter knowsAdapter = new KnowsAdapter(getApplicationContext(), knows);
            tutorKnows.setAdapter(knowsAdapter);
        }

        final List<Review> reviews = new ArrayList<>();
        final ReviewAdapter reviewAdapter = new ReviewAdapter(getApplicationContext(), reviews, this);
        userReviews.setAdapter(reviewAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviews.clear();
                for (DataSnapshot userReviews : dataSnapshot.getChildren()) {
                    if (userReviews.getKey().split("-")[0].equals(tutorId)) {
                        reviews.add(userReviews.getValue(Review.class));
                    }
                }

                if (!reviews.isEmpty()) {
                    emptyTestimonials.setVisibility(View.GONE);
                    userReviews.setVisibility(View.VISIBLE);

                    reviewAdapter.notifyDataSetChanged();
                    userReviews.invalidate();
                } else {
                    emptyTestimonials.setVisibility(View.VISIBLE);
                    userReviews.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        contactButton = findViewById(R.id.contact_button);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
                sendEmail.setData(Uri.parse("mailto:"));
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[] { tutor.getEmail() });
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, "TutorApp - Question by " + username);
                sendEmail.putExtra(Intent.EXTRA_TEXT, "Write your questions here");

                startActivity(Intent.createChooser(sendEmail, "Send an Email"));
            }
        });

        addReviewBtn = findViewById(R.id.add_review_button);
        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewTutorActivity.this, AddReviewActivity.class);
                intent.putExtra("tutor", tutor);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onReviewSelected(View view, Review review) {
        Intent intent = new Intent(ViewTutorActivity.this, ViewReviewActivity.class);
        intent.putExtra("review", review);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}

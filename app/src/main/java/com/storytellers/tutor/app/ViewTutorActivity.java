package com.storytellers.tutor.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reviewsReference = database.getReference("reviews");
    DatabaseReference reportReference = database.getReference("reports");

    SharedPreferences sharedPreferences;

    String username;
    String reporter;
    boolean alreadyReported = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tutor);

        reporter = mAuth.getCurrentUser().getUid();

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

        if (tutor.getKnows() == null || tutor.getKnows().length() == 0) {
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

        reviewsReference.addValueEventListener(new ValueEventListener() {
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
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        contactButton = findViewById(R.id.contact_button);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
                sendEmail.setData(Uri.parse("mailto:"));
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{tutor.getEmail()});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, "TutorApp - Question by " + username);
                sendEmail.putExtra(Intent.EXTRA_TEXT, "Write your questions here");

                startActivity(Intent.createChooser(sendEmail, "Send an Email"));
            }
        });

        checkIfAlreadyReported();

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

    private void checkIfAlreadyReported() {
        reportReference.child(tutor.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alreadyReported = dataSnapshot.child(reporter).getValue(Integer.class) != null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.id_report_user) {
            if (!alreadyReported) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewTutorActivity.this);
                AlertDialog confirmDialog = alertDialog.setTitle(Html.fromHtml("<font color='#000000'>Report user</font>"))
                        .setMessage("Would you like to report this user for Obscene content?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportReference.child(tutor.getId()).child(reporter).setValue(1);
                                alreadyReported = true;
                                Toast.makeText(getApplicationContext(), "Report submitted", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                confirmDialog.show();
                Button buttonPositive = confirmDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonPositive.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
                Button buttonNegative = confirmDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonNegative.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
            } else {
                Toast.makeText(getApplicationContext(), "You've already reported this user", Toast.LENGTH_LONG).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

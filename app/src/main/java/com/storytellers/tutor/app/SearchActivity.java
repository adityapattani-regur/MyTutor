package com.storytellers.tutor.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements TutorAdapter.TutorAdapterListener{
    RecyclerView searchedTutors;
    ProgressBar loader;
    TextView emptyView;
    List<Tutor> tutors = new ArrayList<>();
    TutorAdapter tutorAdapter;
    EditText searchQuery;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        loader = findViewById(R.id.load_view);
        emptyView = findViewById(R.id.empty_view);
        searchQuery = findViewById(R.id.search_et);

        final String userId = mAuth.getCurrentUser().getUid();

        searchedTutors = findViewById(R.id.search_results);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        searchedTutors.setLayoutManager(layoutManager);

        tutorAdapter = new TutorAdapter(getApplicationContext(), tutors, this);
        searchedTutors.setAdapter(tutorAdapter);

        searchedTutors.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tutors.clear();
                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    if (!users.getKey().equals(userId)) {
                        Tutor tutor = users.getValue(Tutor.class);
                        tutors.add(tutor);
                    }
                }

                if (tutors.size() > 0) {
                    loader.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    searchedTutors.setVisibility(View.VISIBLE);
                    tutorAdapter.notifyDataSetChanged();
                    searchedTutors.invalidate();
                } else {
                    loader.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    searchedTutors.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(searchQuery.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String searchQuery) {
        ArrayList<Tutor> filteredNames = new ArrayList<>();
        searchQuery = searchQuery.toLowerCase();

        //Looping through existing elements
        for (Tutor tutor : tutors) {
            if (tutor.getKnows() != null){
                if ((tutor.getName().toLowerCase().startsWith(searchQuery)) || (tutor.getKnows().toLowerCase().contains(searchQuery))) {
                    filteredNames.add(tutor);
                }
            } else {
                if ((tutor.getName().toLowerCase().startsWith(searchQuery))) {
                    filteredNames.add(tutor);
                }
            }
        }

        //Calling a method of the adapter class and passing the filtered list
        if (filteredNames.size() > 0) {
            loader.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            searchedTutors.setVisibility(View.VISIBLE);
            tutorAdapter.setTutors(filteredNames);
            searchedTutors.invalidate();
        } else {
            loader.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            searchedTutors.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTutorSelected(View view, Tutor tutor) {
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this,
                Pair.create(view.findViewById(R.id.tutor_image), view.findViewById(R.id.tutor_image).getTransitionName()),
                Pair.create(view.findViewById(R.id.tutor_name), view.findViewById(R.id.tutor_name).getTransitionName())).toBundle();
        Intent intent = new Intent(SearchActivity.this, ViewTutorActivity.class);
        intent.putExtra("name", tutor);
        startActivity(intent, bundle);
    }
}

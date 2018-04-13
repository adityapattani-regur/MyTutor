package com.storytellers.tutor.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TutorsView extends Fragment implements TutorAdapter.TutorAdapterListener{
    RecyclerView tutorList;
    ProgressBar loader;
    TextView emptyView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_tutors, container, false);
        tutorList = view.findViewById(R.id.tutors_list);
        loader = view.findViewById(R.id.load_view);
        emptyView = view.findViewById(R.id.empty_view);

        final String userId = mAuth.getCurrentUser().getUid();

        tutorList.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        tutorList.setLayoutManager(layoutManager);

        final List<Tutor> tutors = new ArrayList<>();
        final TutorAdapter tutorAdapter = new TutorAdapter(getContext(), tutors, this);
        tutorList.setAdapter(tutorAdapter);

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
                    tutorList.setVisibility(View.VISIBLE);
                    tutorAdapter.notifyDataSetChanged();
                    tutorList.invalidate();
                } else {
                    loader.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    tutorList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }

    @Override
    public void onTutorSelected(View view, Tutor tutor) {
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                Pair.create(view.findViewById(R.id.tutor_image), view.findViewById(R.id.tutor_image).getTransitionName()),
                Pair.create(view.findViewById(R.id.tutor_name), view.findViewById(R.id.tutor_name).getTransitionName())).toBundle();
        Intent intent = new Intent(getActivity(), ViewTutorActivity.class);
        intent.putExtra("tutor", tutor);
        startActivity(intent, bundle);
    }
}

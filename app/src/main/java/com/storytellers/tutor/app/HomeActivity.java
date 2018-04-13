package com.storytellers.tutor.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.storytellers.tutor.app.Utils.CircleTransform;

import java.util.Objects;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmentManager;
    TutorsView tutors = new TutorsView();
    String currentFragment = tutors.getClass().getSimpleName();
    TextView pageTitle, navBarUsername, navBarEmail;
    ImageView navBarImage;

    SharedPreferences sharedPreferences;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.enter_left));
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        pageTitle = findViewById(R.id.title_appbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layout_for_nav, tutors, tutors.getTag()).commit();
        currentFragment = tutors.getClass().getSimpleName();

        pageTitle.setText(getResources().getText(R.string.tutors));

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        navBarUsername = headerView.findViewById(R.id.nav_bar_name);
        navBarEmail = headerView.findViewById(R.id.nav_bar_email);
        navBarImage = headerView.findViewById(R.id.nav_bar_image);

        sharedPreferences = getSharedPreferences("AppData", MODE_PRIVATE);
        String username = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String image = sharedPreferences.getString("image", null);

        navBarUsername.setText(username);
        navBarEmail.setText(email);
        Picasso.get().load(image)
                .transform(new CircleTransform())
                .error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                .into(navBarImage);

        if (sharedPreferences.getBoolean("firstUse", true)) {
            new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                    .setTarget(toolbar.getChildAt(1))
                    .setPrimaryText("Add your profile data")
                    .setSecondaryText("Click on your image in the menu to change your details")
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                sharedPreferences.edit().putBoolean("firstUse", false).apply();
                            }
                        }
                    }).show();
        }

        navBarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });

        navBarUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });

        navBarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });
    }

    private void openProfileActivity() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.layout_for_nav, tutors, tutors.getTag()).commit();
            currentFragment = tutors.getClass().getSimpleName();
            pageTitle.setText(getResources().getText(R.string.tutors));
        } else if (id == R.id.nav_history) {
            pageTitle.setText(getResources().getText(R.string.history));
        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

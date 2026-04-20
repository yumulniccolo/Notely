package com.example.finalexer2grp2;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.appbar.MaterialToolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);

        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.fragmentFiles).build();

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        EditText toolbarEditText = findViewById(R.id.toolbar_title_edittext);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.fragmentEdit) {
                toolbar.setLogo(null);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarEditText.setVisibility(android.view.View.VISIBLE);
            } else {
                toolbar.setLogo(R.drawable.notelylogo);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                toolbarEditText.setVisibility(android.view.View.GONE);
            }
        });

    }
}
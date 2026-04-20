package com.example.finalexer2grp2;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.appbar.MaterialToolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.navHostFragment);

        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.fragmentFiles).build();

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        toolbar.setNavigationOnClickListener(v -> {

            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.fragmentEdit) {

                Fragment currentFragment =
                        getSupportFragmentManager()
                                .findFragmentById(R.id.navHostFragment)
                                .getChildFragmentManager()
                                .getPrimaryNavigationFragment();

                if (currentFragment instanceof FragmentEdit) {

                    if (((FragmentEdit) currentFragment).hasUnsavedChanges()) {
                        new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle("Exit?")
                                .setMessage("You have unsaved changes. Exit anyway?")
                                .setPositiveButton("Yes", (d, w) -> navController.navigateUp())
                                .setNegativeButton("No", null)
                                .show();
                        return;
                    }
                }
            }

            navController.navigateUp();
        });

        EditText toolbarEditText = findViewById(R.id.toolbar_title_edittext);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            
            if (destId == R.id.fragmentEdit) {
                toolbar.setLogo(null);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarEditText.setVisibility(View.VISIBLE);
                toolbarEditText.setFocusableInTouchMode(true);
                toolbarEditText.setFocusable(true);
                toolbarEditText.setClickable(true);
                toolbarEditText.setCursorVisible(true);
            } else if (destId == R.id.fragmentView) {
                toolbar.setLogo(null);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarEditText.setVisibility(View.VISIBLE);
                toolbarEditText.setFocusable(false);
                toolbarEditText.setFocusableInTouchMode(false);
                toolbarEditText.setClickable(false);
                toolbarEditText.setCursorVisible(false);
            } else {
                toolbar.setLogo(R.drawable.notelylogo);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                toolbarEditText.setVisibility(View.GONE);
            }
            
            invalidateOptionsMenu();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        if (navController != null && navController.getCurrentDestination() != null) {
            int destId = navController.getCurrentDestination().getId();
            boolean showMenu = (destId == R.id.fragmentFiles);
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(showMenu);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        Fragment navHostFragment =
                getSupportFragmentManager()
                        .findFragmentById(R.id.navHostFragment);

        Fragment currentFragment =
                navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();

        if (currentFragment instanceof FragmentFiles) {

            FragmentFiles fragment = (FragmentFiles) currentFragment;

            if (item.getItemId() == R.id.sort_alpha) {
                fragment.sortFiles("alpha");
                return true;
            }

            if (item.getItemId() == R.id.sort_latest) {
                fragment.sortFiles("latest");
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
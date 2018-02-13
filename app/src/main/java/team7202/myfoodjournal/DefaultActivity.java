package team7202.myfoodjournal;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

public class DefaultActivity extends AppCompatActivity
        implements ProfileFragment.OnProfileInteractionListener,
        EditProfileFragment.OnEditProfileListener,
        EditPasswordFragment.OnEditPasswordListener,
        WishlistFragment.OnWishlistInteractionListener,
        FilterMenuDialogFragment.OnFilterInteractionListener,
        MyReviewsFragment.OnMyReviewsInteractionListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        selectNavOption("fragment_myreviews");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Defines open and closed states for drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Incomplete, requires override of onPrepareOptionsMenu
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Listens for open and closed events.
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        // Creates the NavigationView object containing the list of menu options.
        mNavigationView = (NavigationView) findViewById(R.id.navigation);

        // Sets the Home page menu option as selected by default.
        mNavigationView.getMenu().getItem(0).setChecked(true);
        ab.setTitle(mNavigationView.getMenu().getItem(0).getTitle());

        // Sets the username in the navigation header
        View headerView = mNavigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navheader_username);
        navUsername.setText(UsernameSingleton.getInstance().getUsername());

        // Creates listener for events when clicking on navigation drawer options.
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        String layout = getLayoutName(menuItem.getItemId());
                        if (layout.equals("Log Out")) {
                            Intent i = new Intent(DefaultActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            selectNavOption(layout);
                          
                            // Updates selected item and title, then closes the drawer
                            menuItem.setChecked(true);
                            ab.setTitle(menuItem.getTitle());
                            mDrawerLayout.closeDrawers();
                        }
                        return true;
                    }
                }
        );
    }

    private String getLayoutName(int resourceId) {
        String layoutName = "";
        switch(resourceId) {
            case R.id.nav_myreviews:
                layoutName = "fragment_myreviews";
                break;
            case R.id.nav_profile:
                layoutName = "fragment_profile";
                break;
            case R.id.nav_wishlist:
                layoutName = "fragment_wishlist";
                break;
            case R.id.nav_logout:
                layoutName = "Log Out";
                break;
        }
        return layoutName;
    }

    /** Swaps fragments in the default activity. */
    private void selectNavOption(String option) {
        // Create a new fragment and specify the screen to show based on the option selected
        if (option == "fragment_profile") {
            Fragment fragment = ProfileFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option == "fragment_edit_profile") {
            Fragment fragment = EditProfileFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option == "fragment_edit_password") {
            Fragment fragment = EditPasswordFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option == "fragment_wishlist") {
            Fragment fragment = WishlistFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (option == "fragment_myreviews") {
            Fragment fragment = MyReviewsFragment.newInstance(option);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else {
            Fragment fragment = new PageFragment();
            Bundle args = new Bundle();
            args.putString(PageFragment.ARG_MENU_OPTION, option);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_default, menu);
//        return true;
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Pass the event to ActionBarDrawerToggle, if it returns true, then it has
           handled the event.
         */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //methods for the profile interface
    //opens the edit profile screen
    @Override
    public void onEditButtonClicked() {
        Log.d("PROFILE", "Edit profile clicked");

        selectNavOption("fragment_edit_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit Profile");
    }

    //opens the edit password screen
    @Override
    public void onChangePassClicked() {
        Log.d("PROFILE", "Change password clicked");
        selectNavOption("fragment_edit_password");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit Password");
    }

    //methods for the edit profile interface
    //returns to the profile screen
    @Override
    public void onProfileSaveClicked() {
        //TODO make the menuItem be currently selected
        Log.d("PROFILE EDIT", "Save profile button clicked");
        selectNavOption("fragment_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
    }

    //returns to the profile summary screen
    @Override
    public void onProfileCancelClicked() {
        //TODO make the menuItem be currently selected
        Log.d("PROFILE EDIT", "Cancel profile edit button clicked");
        selectNavOption("fragment_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
    }

    //methods for the edit password fragment interface
    //returns to the profile screen
    @Override
    public void onPassSaveClicked() {
        //TODO make the menuItem be currently selected
        Log.d("PROFILE EDIT", "Save password button clicked");
        selectNavOption("fragment_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
    }

    //returns to the profile summary screen
    @Override
    public void onPassCancelClicked() {
        //TODO make the menuItem be currently selected
        Log.d("PROFILE EDIT", "Cancel password edit button clicked");
        selectNavOption("fragment_profile");
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
    }

    @Override
    public void onFilterButtonClicked() {
        Log.d("WISHLIST", "Filters button clicked on Wishlist page");
        FilterMenuDialogFragment filterMenu = new FilterMenuDialogFragment();
        FragmentManager fm = getFragmentManager();
        filterMenu.show(fm, "Filter Menu generated");
    }

    @Override
    public void onRestaurantFieldClicked() {

    }

    @Override
    public void onSortByButtonClicked() {
        Log.d("WISHLIST", "Sort By button clicked on Wishlist page");
        final View anchor = findViewById(R.id.sortby_button);
        PopupMenu popup = new PopupMenu(this, anchor);
        getMenuInflater().inflate(R.menu.sortby_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // TODO: Add code for filtering list of entries
                switch (menuItem.getItemId()) {
                    case R.id.sortby_mostrecent:
                        break;
                    case R.id.sortby_rating:
                        break;
                    case R.id.sortby_restaurant:
                        break;
                    case R.id.sortby_food:
                        break;
                }
                Button sortByButton = (Button) anchor;
                sortByButton.setText("Sort By: \n" + menuItem.getTitle());
                return true;
            }
        });

        popup.show();
    }

    @Override
    public void onFloatingButtonClicked() {
        final View view = findViewById(R.id.fab);
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
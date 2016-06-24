package com.droidvnteam.hexagonrom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.System;

import com.droidvnteam.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NAV_ITEM_ID = "navItemId";
    static final String TAG = MainActivity.class.getSimpleName();


    private DrawerLayout mDrawer;
    private int id;
    private long startTime;
    private boolean secondBack=false;

    @Override
    protected void onStart() {
	    super.onStart();

	    new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
	            try {
                    boolean canGainSu = SuShell.canGainSu(getApplicationContext());
                    return canGainSu;
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.getMessage(), e);
                    Toast.makeText(MainActivity.this, R.string.cannot_get_su_start,
                    Toast.LENGTH_LONG).show();
                    return true; // I want to start the app regardles of having root or not
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                setProgressBarIndeterminateVisibility(false);

                if (!result) {
                    Toast.makeText(MainActivity.this, R.string.cannot_get_su,
                    Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }.execute();
    }

		

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_main, new AboutFragment());
        tx.commit();


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


   }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            /*
            **If back was pressed the first time,only show toast
            **else exit the app provided the user presses back
            **the second time within 2.5 seconds of the first
            */
            if(!secondBack) {
            Toast.makeText(MainActivity.this, R.string.app_exit_toast,
            Toast.LENGTH_SHORT).show();
            secondBack=!secondBack;
            startTime = System.currentTimeMillis();
            } else if( System.currentTimeMillis()-startTime < 2500)
            super.onBackPressed();
            else {
            secondBack=!secondBack;
            onBackPressed();
           }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // update highlighted item in the navigation menu
        item.setChecked(true);
        id = item.getItemId();
        Fragment fragment = null;

        Class fragmentClass;

        switch (id) {
            case R.id.nav_display_animations:
                fragmentClass = DisplayAnimationsActivity.class;
                break;
            case R.id.nav_statusbar:
                fragmentClass = StatusBarFragment.class;
                break;
            case R.id.nav_notif_drawer:
                fragmentClass = NotificationsFragment.class;
                break;
            case R.id.nav_recents:
                fragmentClass = RecentsPanelFragment.class;
                break;
            case R.id.nav_lockscreen:
                fragmentClass = LockscreenFragment.class;
                break;
            case R.id.nav_advanced:
                fragmentClass = MultiShitFragment.class;
                break;
            case R.id.nav_transparency_porn:
                fragmentClass = TransparencyPornFragment.class;
                break;
            case R.id.nav_more:
                fragmentClass = VariousShitFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = AboutFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RelativeLayout contentMain = (RelativeLayout) findViewById(R.id.content_main);
        contentMain.removeAllViewsInLayout();
        contentMain.invalidate();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        item.setChecked(true);
        setTitle(item.getTitle());
        mDrawer.closeDrawers();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, id);
    }
}
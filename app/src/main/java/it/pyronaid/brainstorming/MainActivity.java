package it.pyronaid.brainstorming;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import java.io.File;

import javax.inject.Inject;

import applications.BrainStormingApplications;
import asynctask.CheckAuthTokenTask;
import asynctask.RemoveAuthTokenTask;
import authenticatorStuff.User;
import databaseStuff.BrainStormingSQLiteHelper;
import layoutCustomized.CircleImageView;
import menuFragments.HomeFragment;
import menuFragments.LogInFragment;
import menuFragments.LogOutFragment;
import menuFragments.MyCategoriesFragment;
import menuFragments.MyProfileFragment;
import menuFragments.SynchroFragment;
import authenticatorStuff.AccountManagerUtils;
import validatorStuff.ValidatorInputs;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, LogInFragment.OnFragmentInteractionListener, LogOutFragment.OnFragmentInteractionListener, MyCategoriesFragment.OnFragmentInteractionListener, SynchroFragment.OnFragmentInteractionListener, MyProfileFragment.OnFragmentInteractionListener {
	private DrawerLayout mDrawer;
	private Toolbar toolbar;
	private NavigationView nvDrawer;
	private ActionBarDrawerToggle drawerToggle;

	@Inject
	AccountManagerUtils accountManagerUtils;

	@Inject
	BrainStormingSQLiteHelper brainStormingSQLiteHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Add a property to account manager utils and set one by one ?
		//Understend how refactor the structure
		((BrainStormingApplications)getApplication()).getApplicationComponent().inject(this);

		// Set a Toolbar to replace the ActionBar.
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Find our drawer view
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = setupDrawerToggle();

		// Tie DrawerLayout events to the ActionBarToggle
		mDrawer.addDrawerListener(drawerToggle);

		// Find our drawer view
		nvDrawer = (NavigationView) findViewById(R.id.nvView);
		// Setup drawer view
		setupDrawerContent(nvDrawer);

		if(savedInstanceState == null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			try {
				fragmentManager.beginTransaction().replace(R.id.flContent, HomeFragment.class.newInstance()).commit();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	protected void onStart() {
		new CheckAuthTokenTask(this, brainStormingSQLiteHelper).execute(accountManagerUtils);
		super.onStart();
	}


	private ActionBarDrawerToggle setupDrawerToggle() {
		return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
	}

	private void setupDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						selectDrawerItem(menuItem);
						return true;
					}
				});
	}

	public void selectDrawerItem(MenuItem menuItem) {
		// Create a new fragment and specify the fragment to show based on nav item clicked
		Fragment fragment = null;
		Class fragmentClass;
		switch(menuItem.getItemId()) {
			case R.id.drawer_home_fragment:
				fragmentClass = HomeFragment.class;
				break;
			case R.id.drawer_mycategories_fragment:
				fragmentClass = MyCategoriesFragment.class;
				break;
			case R.id.drawer_logout_fragment:
				//fragmentClass = LogOutFragment.class;
                fragmentClass = HomeFragment.class;
				new RemoveAuthTokenTask(this, brainStormingSQLiteHelper).execute(accountManagerUtils);
                break;
			case R.id.drawer_myprofile_fragment:
				fragmentClass = MyProfileFragment.class;
				break;
			case R.id.drawer_synchro_fragment:
				fragmentClass = SynchroFragment.class;
				break;
			default:
				fragmentClass = HomeFragment.class;
		}

		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

		// Highlight the selected item has been done by NavigationView
		menuItem.setChecked(true);
		// Set action bar title
		setTitle(menuItem.getTitle());
		// Close the navigation drawer
		mDrawer.closeDrawers();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}


	@Override
	public void onFragmentInteraction(Uri uri) {
		Log.i("GIOVANNI",uri.toString());
	}

	public BrainStormingSQLiteHelper getBrainStormingSQLiteHelper() {
		return brainStormingSQLiteHelper;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			trimCache(this);
			// Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
}

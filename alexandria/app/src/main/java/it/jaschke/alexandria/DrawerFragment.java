package it.jaschke.alexandria;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yemyatthu on 7/28/15.
 */
public class DrawerFragment extends Fragment
    implements NavigationView.OnNavigationItemSelectedListener {

  /**
   * Remember the position of the selected item.
   */
  private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

  /**
   * Per the design guidelines, you should show the drawer on launch until the user manually
   * expands it. This shared preference tracks this.
   */
  private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

  private NavigationView mNavigationView;
  private NavigationDrawerCallbacks mCallbacks;
  private View mFragmentContainerView;
  private DrawerLayout mDrawerLayout;
  private int mCurrentSelectedItemId = 0;
  private boolean mUserLearnedDrawer;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mNavigationView = (NavigationView) inflater.inflate(R.layout.fragment_drawer, container, false);
    mNavigationView.setNavigationItemSelectedListener(this);
    if (savedInstanceState != null
        && savedInstanceState.getInt(STATE_SELECTED_POSITION, -1) != -1) {
      mCurrentSelectedItemId = savedInstanceState.getInt(STATE_SELECTED_POSITION);
    } else if (Integer.valueOf(PreferenceManager.
        getDefaultSharedPreferences(getActivity()).getString("pref_startFragment", "-1")) != -1) {
      int pos = Integer.valueOf(PreferenceManager.
          getDefaultSharedPreferences(getActivity()).getString("pref_startFragment", "-1"));
      mCurrentSelectedItemId = mNavigationView.getMenu().getItem(pos).getItemId();
    } else {
      mCurrentSelectedItemId = mNavigationView.getMenu().getItem(0).getItemId();
    }
    mNavigationView.getMenu().findItem(mCurrentSelectedItemId).setChecked(true);
    mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedItemId);
    return mNavigationView;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    // Read in the flag indicating whether or not the user has demonstrated awareness of the
    // drawer. See PREF_USER_LEARNED_DRAWER for details.
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
  }

  public void setUp(int fragmentId, final DrawerLayout drawerLayout, @Nullable Toolbar toolbar) {

    mFragmentContainerView = getActivity().findViewById(fragmentId);
    mDrawerLayout = drawerLayout;
    if (!mUserLearnedDrawer) {
      mDrawerLayout.openDrawer(mFragmentContainerView);
      PreferenceManager.getDefaultSharedPreferences(getActivity())
          .edit()
          .putBoolean(PREF_USER_LEARNED_DRAWER, true)
          .apply();
    }
    if (toolbar != null) {
      toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (drawerLayout.isDrawerOpen(mFragmentContainerView)) {
            drawerLayout.closeDrawer(mFragmentContainerView);
          } else {
            drawerLayout.openDrawer(mFragmentContainerView);
          }
        }
      });
    }
  }

  @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
    //If the same item is selected again, do nothing
    if (mCurrentSelectedItemId == menuItem.getItemId()) {
      mDrawerLayout.closeDrawer(mFragmentContainerView);
      menuItem.setChecked(true);
      return false;
    }
    mCurrentSelectedItemId = menuItem.getItemId();
    mCallbacks.onNavigationDrawerItemSelected(menuItem.getItemId());
    mDrawerLayout.closeDrawer(mFragmentContainerView);
    menuItem.setChecked(true);
    return true;
  }

  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mCallbacks = (DrawerFragment.NavigationDrawerCallbacks) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedItemId);
  }

  @Override public void onDetach() {
    super.onDetach();
    mCallbacks = null;
  }

  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
  public static interface NavigationDrawerCallbacks {
    /**
     * Called when an item in the navigation drawer is selected.
     */
    void onNavigationDrawerItemSelected(int itemId);
  }
}

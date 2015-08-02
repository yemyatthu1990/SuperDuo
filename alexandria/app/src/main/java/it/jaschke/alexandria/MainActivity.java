package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import it.jaschke.alexandria.api.Callback;


public class MainActivity extends AppCompatActivity implements Callback,DrawerFragment.NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;
    private Toolbar toolbar;
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
        }else {
            setContentView(R.layout.activity_main);
        }

        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever,filter);
        title = getTitle();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        DrawerFragment drawerFragment =
            (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if(drawerFragment!=null){
            drawerFragment.setUp(R.id.navigation_drawer,drawerLayout,toolbar);
        }

    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.main, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        //For better navigation and usability, use activity to open another screen
        //rather than fragment for detail view
        Intent intent = new Intent(this,BookDetail.class);
        intent.putExtra(BookDetail.EAN_KEY, ean);
        startActivity(intent);

    }

    @Override public void onNavigationDrawerItemSelected(int itemId) {
        Fragment nexFrag;
        switch (itemId){
            case R.id.book_list_nav_item:
                nexFrag = new ListOfBooks();
                break;
            case R.id.add_book_nav_item:
                nexFrag = new AddBook();
                break;
            default:
                nexFrag = new ListOfBooks();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container,nexFrag).commit();
    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }


    private boolean isTablet() {
        //return (getApplicationContext().getResources().getConfiguration().screenLayout
        //        & Configuration.SCREENLAYOUT_SIZE_MASK)
        //        >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        return false;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()<2){
            finish();
        }
        super.onBackPressed();
    }


}
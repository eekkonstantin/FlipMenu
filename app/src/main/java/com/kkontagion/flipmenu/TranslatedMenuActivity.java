package com.kkontagion.flipmenu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kkontagion.flipmenu.objects.Item;

import java.util.ArrayList;

public class TranslatedMenuActivity extends AppCompatActivity implements ItemsFragment.OnFragmentInteractionListener {

    String jsonData;
    ArrayList<Item> orders = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment frag = null;
            String tag = "";
            switch (item.getItemId()) {
                case R.id.navigation_items:
                    frag = ItemsFragment.newInstance(jsonData);
                    tag = "menu";
                    ((ItemsFragment) frag).collectItems(orders);
                    break;
                case R.id.navigation_cart:
                    frag = OrderFragment.newInstance("a", "b");
                    ((OrderFragment) frag).collectItems(orders);
                    tag = "order";
                    break;
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.content, frag, tag).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translated_menu);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        detectItems();


        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, ItemsFragment.newInstance(jsonData));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gohome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_home:
                Intent home = new Intent(this, MainActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void detectItems() {

    }

    /**
     * Receive Item from Item fragment
     * @param i modified item
     */
    @Override
    public void onItemMod(Item i) {
        Log.d(getLocalClassName(), "onItemMod: " + i);
        orders.add(i);
        OrderFragment orderFragment = (OrderFragment) getFragmentManager().findFragmentByTag("order");
        if (orderFragment != null)
            orderFragment.collectItems(orders);
//        else {

//            orderFragment = new OrderFragment();
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//            // Replace whatever is in the fragment_container view with this fragment,
//            // and add the transaction to the back stack so the user can navigate back
//            transaction.replace(R.id.content, orderFragment, "order");
//            transaction.addToBackStack(null);
//            transaction.commit();
//
//            orderFragment.collectItem(i);
//        }
    }
}

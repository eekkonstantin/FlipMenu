package com.kkontagion.flipmenu;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kkontagion.flipmenu.adapters.ItemAdapter;
import com.kkontagion.flipmenu.objects.Item;

import org.json.JSONObject;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    ArrayList<Item> items;
    ItemAdapter adapter;

    RecyclerView rv;
    TextView tvClear;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvClear = findViewById(R.id.bt_clear);
        rv = findViewById(R.id.rv_items);
        fab = findViewById(R.id.fab_cart);

        if ( savedInstanceState != null && savedInstanceState.containsKey("items"))
            this.items = savedInstanceState.getParcelableArrayList("items");
        else
            setupItems();

        adapter = new ItemAdapter(getBaseContext(), items);
        rv.setAdapter(adapter);

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Item i : items)
                    i.setQuantity(0);
                adapter.notifyDataSetChanged();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart = new Intent(getBaseContext(), OrderActivity.class);
                cart.putExtra("items", collateOrders());
                startActivityForResult(cart, 200);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("items", items);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.items = savedInstanceState.getParcelableArrayList("items");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gocart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_cart:
                Intent cart = new Intent(this, OrderActivity.class);
//                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                cart.putExtra("items", collateOrders());
                startActivityForResult(cart, 200);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupItems() {
        items = new ArrayList<>();

        JSONObject jsonOrig;
        ArrayList<String> textTrans, textOrig;
        try {
            jsonOrig = new JSONObject(getIntent().getStringExtra("detectedJSON"));
        } catch (Exception e) {
            jsonOrig = new JSONObject();
            Log.e(getClass().getSimpleName(), "setupItems: Error in detected JSON");
        }
        textTrans = getIntent().getStringArrayListExtra("translatedText");
        textOrig = getIntent().getStringArrayListExtra("detectedText");
        Log.d(getClass().getSimpleName(), "setupItems ORIG: " + jsonOrig.toString());

        if (textTrans.size() < 2) {
            char orig = 'a';
            char trans = 'z';
            for (int i = 0; i < 26; i++)
                items.add(new Item(i, "" + orig++, "" + trans--));
        } else {
            for (int i = 1; i<textTrans.size(); i++) {
                String[] det = textTrans.get(i).split("///");
                Item me = new Item(i, textTrans.get(i), textOrig.get(i), det[0]);
                if (det.length > 1)
                    me.setDescription(det[1]);
                items.add(me);
            }
        }
    }

    /**
     * Collates menu items with quantity > 0, to be sent to next page.
     * @return
     */
    private ArrayList<Item> collateOrders() {
        ArrayList<Item> ret = new ArrayList<>();

        for (Item i : items) {
            if (i.getQuantity() > 0)
                ret.add(i);
        }

        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_CANCELED)
            checkQty(data);
    }

    private void checkQty(Intent data) {
        ArrayList<Item> orders = data.getParcelableArrayListExtra("orders");
        for (Item i : items) {
            if (orders.contains(i)) { // update qty
                int odx = orders.indexOf(i);
                i.setQuantity(orders.get(odx).getQuantity());
            } else
                i.setQuantity(0);
        }
        adapter.notifyDataSetChanged();
    }
}

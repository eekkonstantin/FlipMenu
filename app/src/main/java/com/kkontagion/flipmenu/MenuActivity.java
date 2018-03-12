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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity implements ItemAdapter.OnViewClickListener {

    ArrayList<Item> items;
    ItemAdapter adapter;

    RecyclerView rv;
    TextView tvClear;
    FloatingActionButton fab;

    JSONArray jsonItems;
    private int lastItem = 0;

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

        adapter = new ItemAdapter(this, items);
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

    private void setupItems() {
        items = new ArrayList<>();

        JSONObject jsonOrig;
        ArrayList<String> textTrans, textOrig;
        Log.d(getClass().getSimpleName(), getIntent().getStringExtra("detectedJSON"));
        try {
            jsonOrig = new JSONObject(getIntent().getStringExtra("detectedJSON"));

            jsonItems = ((JSONObject) jsonOrig.getJSONArray("responses").get(0)).getJSONArray("textAnnotations");
            jsonItems.remove(0);
            Log.e(getClass().getSimpleName(), "setupItems: " + jsonItems.toString() );
        } catch (Exception e) {
            jsonOrig = new JSONObject();
            Log.e(getClass().getSimpleName(), "setupItems: Error in detected JSON");
        }
        textTrans = getIntent().getStringArrayListExtra("translatedText");
        textOrig = getIntent().getStringArrayListExtra("detectedText");
        Log.d(getClass().getSimpleName(), "setupItems ORIG: " + jsonOrig.toString());


        if (textTrans.size() < 1) {
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

                // setup XY
                try {
                    // get Start XY
                    for (int o=lastItem; o<jsonItems.length(); o++) {
                        if (me.getStartXY()[0] > 0 && me.getEndXY()[0] > 0) // both already set. break.
                            break;
                        JSONObject obj;
                        obj = (JSONObject) jsonItems.get(o);
                        String desc = obj.getString("description");
                        if (me.getOriginal().startsWith(desc)) {
                            lastItem = o + me.getName().split(" ").length + me.getDescription().split(" ").length - 1;
                            JSONObject xy = (JSONObject) getXYBounds(obj).get(0);
                            Log.d(getClass().getSimpleName(), "setupItems: " + me.getName() + " start: " + xy.toString());
                            me.setStartXY(new int[] {xy.getInt("x"), xy.getInt("y")});
                            continue;
                        }

                        // get End XY
                        if (me.getOriginal().endsWith(desc)) {
                            lastItem = o;
                            JSONObject xy = (JSONObject) getXYBounds(obj).get(3);

                            Log.d(getClass().getSimpleName(), "setupItems: " + me.getName() + " end: " + xy.toString());
                            me.setEndXY(new int[] {xy.getInt("x"), xy.getInt("y")});
                        }
                    }

                    Log.d(getClass().getSimpleName(), "setupItems: " + me);
                    items.add(me);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error getting boundingPolys.");
                    items.add(me);
                }



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

    private JSONArray getXYBounds(JSONObject obj) throws JSONException {
        return obj.getJSONObject("boundingPoly").getJSONArray("vertices");
    }

    @Override
    public void seeBoundingBox(Item item) {
        Intent i = new Intent(this, SeeItemActivity.class);
        i.putExtra("item", item);
        i.putExtra("imgpath", getIntent().getStringExtra("imgpath"));
        startActivityForResult(i, 10);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gohome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

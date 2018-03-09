package com.kkontagion.flipmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kkontagion.flipmenu.adapters.ItemAdapter;
import com.kkontagion.flipmenu.objects.Item;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    ArrayList<Item> orders;

    RecyclerView rv;
    TextView tvClear, tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: SPEAK
            }
        });

        orders = getIntent().getParcelableArrayListExtra("items");
        rv = findViewById(R.id.rv);
        final ItemAdapter adapter = new ItemAdapter(true, getBaseContext(), orders);
        rv.setAdapter(adapter);

        tvClear = findViewById(R.id.bt_clear);
        tvEmpty = findViewById(R.id.tv_empty);

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orders.clear();
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });

        if (orders.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvClear.setVisibility(View.GONE);
        }
    }

    /**
     * Prepare String for TTS to say.
     * @return
     */
    private String prepareSpeech() {
        StringBuilder sb = new StringBuilder();

        for (Item o : orders) {
            sb.append(o.toSpeech());
            sb.append("\n\n");
        }

        return sb.toString();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                setResult(RESULT_CANCELED, prepareBack());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, prepareBack());
        super.onBackPressed();
    }

    /**
     * Prepare
     * @return
     */
    private Intent prepareBack() {
        Intent newData = new Intent();
        newData.putExtra("orders", orders);
        return newData;
    }
}

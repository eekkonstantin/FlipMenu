package com.kkontagion.flipmenu;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import android.widget.Toast;

import com.kkontagion.flipmenu.adapters.ItemAdapter;
import com.kkontagion.flipmenu.objects.Item;

import java.util.ArrayList;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity implements ItemAdapter.OnViewClickListener {

    ArrayList<Item> orders;
    ItemAdapter adapter;
    TextToSpeech t1;

    RecyclerView rv;
    TextView tvClear, tvEmpty;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        fab = findViewById(R.id.fab);

        orders = getIntent().getParcelableArrayListExtra("items");
        rv = findViewById(R.id.rv);
        adapter = new ItemAdapter(true, this, orders);
        rv.setAdapter(adapter);

        tvClear = findViewById(R.id.bt_clear);
        tvEmpty = findViewById(R.id.tv_empty);



        if (orders.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvClear.setVisibility(View.GONE);
        }

        setupInteractions();
    }

    private void setupInteractions() {
        //bernard tts
        t1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), prepareSpeech(),Toast.LENGTH_SHORT).show();
                t1.speak(prepareSpeech(),TextToSpeech.QUEUE_FLUSH,null,null);
            }
        });
        //bernard tts END

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orders.clear();
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Prepare String for TTS to say.
     */
    private String prepareSpeech() {
        // TODO Bernard: try, if too fast, use this in the for loop instead
        /**
         * mytts.speak(snippet, QUEUE_ADD, null);
         * mytts.playSilentUtterance(2000, QUEUE_ADD, null);
         */
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

    //bernard tts
    @Override
    protected void onDestroy() {
        if(t1 != null) {
            t1.stop();
            t1.shutdown();
            Log.d(getClass().getSimpleName(), "TTS Destroyed");
        }
        super.onDestroy();
    }

    /**
     * Prepare to send order statuses back to Menu page.
     * @return
     */
    private Intent prepareBack() {
        Intent newData = new Intent();
        newData.putExtra("orders", orders);
        return newData;
    }

    @Override
    public void seeBoundingBox(Item item) {
        // Empty function
    }
}

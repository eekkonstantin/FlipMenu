package com.kkontagion.flipmenu;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kkontagion.flipmenu.adapters.HistoryAdapter;
import com.kkontagion.flipmenu.objects.HistoryItem;

import java.io.File;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.OnHistoryClickListener {

    ArrayList<String> imagesLinks;

    ArrayList<HistoryItem> hItems;
    HistoryAdapter adapter;

    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rv = findViewById(R.id.rv_items);


        ArrayList<String> imagesUri = imageRetrieval();
        Log.d("penis", imagesUri.toString());


        // Kon:
        if (imagesUri.isEmpty()) {
            TextView tv = findViewById(R.id.tv_empty);
            tv.setVisibility(View.VISIBLE);
        } else {
            //  1) get SharedPrefs for each image (ref. ConfirmActivity:440), and assign to a HistoryItem with the image
            hItems = new ArrayList<>();
            for (String name : imagesUri) {
                Uri uri = Uri.parse(name);
                hItems.add(new HistoryItem(name, getSharedPreferences(uri.getLastPathSegment().split("\\.")[0], MODE_PRIVATE), this));
            }
            //  2) load into an adapter and display in the RecyclerView with item_history layout.
            adapter = new HistoryAdapter(this, hItems);
            rv.setAdapter(adapter);
        }
    }

    private ArrayList<String> imageRetrieval() {
        imagesLinks = new ArrayList<>();
        File file= new File(android.os.Environment.getExternalStorageDirectory(),"fancaidan/photos");
        File[] listFile;
        if (file.isDirectory())
        {
            listFile = file.listFiles();

            if (listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {

                    imagesLinks.add(listFile[i].getAbsolutePath());

                }
            }
        }


        return imagesLinks;
    }

    @Override
    public void onHistoryClicked(HistoryItem item) {
        Intent i = new Intent(this, ConfirmActivity.class);
        i.putExtra("fromHistory", true);
        i.putExtra("filepath", item.getName());
        i.putExtra("historyItem", item);
        startActivityForResult(i, 90);
    }
}

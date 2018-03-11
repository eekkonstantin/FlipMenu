package com.kkontagion.flipmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        // TODO Bernard: get all images from the save folder, store in an array. Best if can be retrieved as a Drawable, or something Glide likes

        // TODO Kon:
        //  1) get SharedPrefs for each image (ref. ConfirmActivity:440), and assign to a HistoryItem with the image
        //  2) load into an adapter and display in the RecyclerView with item_history layout.
    }
}

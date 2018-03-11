package com.kkontagion.flipmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        ArrayList<String> imagesUri = imageRetrieval();
        Log.d("penis", imagesUri.toString());

        // TODO Kon:
        //  1) get SharedPrefs for each image (ref. ConfirmActivity:440), and assign to a HistoryItem with the image
        //  2) load into an adapter and display in the RecyclerView with item_history layout.
    }

    private ArrayList<String> imageRetrieval() {
        ArrayList<String> imagesLinks = new ArrayList<>();
        File file= new File(android.os.Environment.getExternalStorageDirectory(),"fancaidan/photos");
        File[] listFile;
        if (file.isDirectory())
        {
            listFile = file.listFiles();


            for (int i = 0; i < listFile.length; i++)
            {

                imagesLinks.add(listFile[i].getAbsolutePath());

            }
        }


        return imagesLinks;
    }
}

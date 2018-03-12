package com.kkontagion.flipmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Denzel on 03/12/18.
 */

public class howtouse_Activity extends AppCompatActivity {
    ViewPager viewPager;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howtouse);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        bt = findViewById(R.id.bt_go);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);

        // Kon
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("fromHelp", true);
                startActivityForResult(i, 88);
            }
        });
    }
}

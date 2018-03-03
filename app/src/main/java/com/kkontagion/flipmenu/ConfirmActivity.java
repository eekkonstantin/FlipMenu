package com.kkontagion.flipmenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Intent i = getIntent();
        String filename = i.getStringExtra("filepath");

        ImageView preview = (ImageView) findViewById(R.id.img_preview);
        Glide.with(this).load(filename).into(preview);

    }
}

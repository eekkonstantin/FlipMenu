package com.kkontagion.flipmenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ConfirmActivity extends AppCompatActivity {

    ImageButton btCfm, btReject;

    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        filename = getIntent().getStringExtra("filepath");

        ImageView preview = (ImageView) findViewById(R.id.img_preview);
        Glide.with(this).load(filename).into(preview);

        btCfm = findViewById(R.id.bt_confirm);
        btReject = findViewById(R.id.bt_reject);

        setupActions();
    }

    private void setupActions() {
        btCfm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), TranslatedMenuActivity.class);
                i.putExtra("detectedJSON", "");
                startActivity(i);
            }
        });

        btReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Bernard: delete file
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO Bernard (optional): "Confirm back? Data will be lost" kind of dialog
        // TODO Bernard: delete file
        super.onBackPressed();
    }
}

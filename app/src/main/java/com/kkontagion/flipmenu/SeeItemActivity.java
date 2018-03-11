package com.kkontagion.flipmenu;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.kkontagion.flipmenu.objects.Item;

import java.io.File;

public class SeeItemActivity extends AppCompatActivity {

    Item item;

    ImageView img;
    RelativeLayout box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_item);
        this.item = getIntent().getParcelableExtra("item");
        Log.d(getClass().getSimpleName(), "onCreate: " + item);

        int tbarH = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            tbarH = getResources().getDimensionPixelSize(resourceId);

        img = findViewById(R.id.img);
        Glide.with(this).load(new File(getIntent().getStringExtra("imgpath"))).into(img);

        box = findViewById(R.id.bounding_box);

        if (item.getStartXY() != null && item.getStartXY()[0] > 0) { // has start, MIGHT have end
            box.setX(item.getStartXY()[0]);
            box.setY(tbarH + item.getStartXY()[1]);

            if (item.getEndXY() != null && item.getEndXY()[0] > 0) {
                int width = item.getEndXY()[0] - item.getStartXY()[0];
                int height = item.getEndXY()[1] - item.getStartXY()[1];

                ViewGroup.LayoutParams params = box.getLayoutParams();
                params.height = height;
                params.width = width;
                box.setLayoutParams(params);
                Log.d(getClass().getSimpleName(), "Layouts set: " + width + " by " + height);
            }
        } else if (item.getEndXY() != null && item.getEndXY()[0] > 0) { // no start, has end
            box.setX(item.getEndXY()[0] - getResources().getDimension(R.dimen.bb_w));
            box.setY(tbarH + item.getEndXY()[1] - getResources().getDimension(R.dimen.bb_h));
        } else // neither present, hide
            box.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }
}

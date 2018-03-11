package com.kkontagion.flipmenu.adapters;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kkontagion.flipmenu.R;
import com.kkontagion.flipmenu.objects.HistoryItem;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Kon on 12/3/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
    Context ctx;
    ArrayList<HistoryItem> items;
    OnHistoryClickListener listener;

    public class Holder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvHeader, tvDesc;
        ImageButton btDelete;

        HistoryItem item;
        public Holder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tvHeader = itemView.findViewById(R.id.tv1);
            tvDesc = itemView.findViewById(R.id.tv2);
            btDelete = itemView.findViewById(R.id.bt_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onHistoryClicked(item);
                }
            });
        }
    }

    public HistoryAdapter(Activity ctx, ArrayList<HistoryItem> items) {
        this.ctx = ctx;
        this.listener = (OnHistoryClickListener) ctx;
        this.items = items;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        final HistoryItem item = items.get(position);

        holder.item = item;
        Glide.with(ctx).load(item.getName()).into(holder.img);
        Log.d(getClass().getSimpleName(), "onBindViewHolder: " + item.getLocation() + ", " + item.getDateTime());
        holder.tvHeader.setText(item.getLocation());
        holder.tvDesc.setText(item.getDateTime());

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.remove(position);

                // clear data
                String prefName = Uri.parse(item.getName()).getLastPathSegment().split("\\.")[0];
                ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().apply();

                // delete item
                File deleteImg = new File(item.getName());
                File deletePrefs = new File("/data/data/" + ctx.getPackageName() + "/shared_prefs/" + prefName + ".xml");
                if (deleteImg.exists() && deletePrefs.exists()) {
                    if (deleteImg.delete() && deletePrefs.delete()) {
                        System.out.println("Image & Data Deleted :" + item.getName());
                    } else {
                        System.out.println("Image & Data not Deleted :" + item.getName());
                    }
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnHistoryClickListener {
        void onHistoryClicked(HistoryItem item);
    }
}

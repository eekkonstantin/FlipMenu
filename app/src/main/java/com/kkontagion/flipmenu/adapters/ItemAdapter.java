package com.kkontagion.flipmenu.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkontagion.flipmenu.R;
import com.kkontagion.flipmenu.objects.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kon on 3/3/2018.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    Context ctx;
    OnViewClickListener listener;
    ArrayList<Item> items;
    boolean enableClear = false;

    boolean hasPadding = false;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvMore, tvLess;
        TextView tvTrans, tvOrig;
        ImageView img;
        ImageButton btAction;
        EditText etQty;
        Item item;

        public ItemViewHolder(View v) {
            super(v);
            tvTrans = v.findViewById(R.id.tv1);
            tvOrig = v.findViewById(R.id.tv2);
            img = v.findViewById(R.id.img);

            tvLess = v.findViewById(R.id.tv_less);
            etQty = v.findViewById(R.id.et_qty);
            tvMore = v.findViewById(R.id.tv_more);

            btAction = v.findViewById(R.id.bt_action);

            tvLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.getQuantity() > 0) {
                        item.dec();
                        etQty.setText(item.getQuantity() + "");
                    }
                }
            });

            tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.inc();
                    etQty.setText(item.getQuantity() + "");
                }
            });

            if (enableClear)
                btAction.setImageResource(R.drawable.ic_cancel);

            etQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0
                            && Integer.parseInt(editable.toString()) < 0) { // no data, set to zero
                        etQty.setText("0");
                        item.setQuantity(0);
                    }
                }
            });

            etQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    Log.d(getClass().getSimpleName(), "onFocusChange: changed");
                    if (!hasFocus && etQty.length() < 1) {
                        etQty.setText("0");
                        item.setQuantity(0);
                    }
                }
            });

            btAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (enableClear) { // Remove item.
                        items.remove(items.indexOf(item));
                        notifyDataSetChanged();
                    } else { // TODO: Show in original image.
                        listener.seeBoundingBox(item);
                    }
                }
            });
        }
    }

    public ItemAdapter(Activity ctx, ArrayList<Item> items) {
        this.ctx = ctx;
        this.items = items;
        this.listener = (OnViewClickListener) ctx;
    }
    public ItemAdapter(Activity ctx, ArrayList<Item> items, boolean hasPadding) {
        this.ctx = ctx;
        this.items = items;
        this.hasPadding = hasPadding;
        this.listener = (OnViewClickListener) ctx;
    }
    public ItemAdapter(boolean enableClear, Activity ctx, ArrayList<Item> items) {
        this.ctx = ctx;
        this.items = items;
        this.enableClear = enableClear;
        this.listener = (OnViewClickListener) ctx;
    }
    public ItemAdapter(boolean enableClear, Activity ctx, ArrayList<Item> items, boolean hasPadding) {
        this.ctx = ctx;
        this.items = items;
        this.hasPadding = hasPadding;
        this.enableClear = enableClear;
        this.listener = (OnViewClickListener) ctx;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = items.get(position);
        if (enableClear) // on summary page
            holder.tvOrig.setText(item.getOriginal().split("///")[0]);
        else
            holder.tvOrig.setText(item.getDescription());
        holder.tvTrans.setText(item.getName());
        holder.etQty.setText(item.getQuantity() + "");
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnViewClickListener {
        void seeBoundingBox(Item item);
    }
}

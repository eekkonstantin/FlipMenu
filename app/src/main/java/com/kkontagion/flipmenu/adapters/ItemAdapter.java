package com.kkontagion.flipmenu.adapters;

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
    ArrayList<Item> items;
    boolean enableClear = false;

    OnItemModListener itemModListener;

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
                        modItem(item);
                    }
                }
            });

            tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.inc();
                    etQty.setText(item.getQuantity() + "");
                    modItem(item);
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
        }
    }

    public ItemAdapter(Context ctx, ArrayList<Item> items, OnItemModListener itemModListener) {
        this.ctx = ctx;
        this.items = items;
        this.itemModListener = itemModListener;
    }
    public ItemAdapter(Context ctx, ArrayList<Item> items, OnItemModListener itemModListener, boolean enableClear) {
        this.ctx = ctx;
        this.items = items;
        this.itemModListener = itemModListener;
        this.enableClear = enableClear;
    }

    public void modItem(Item item) {
        itemModListener.onItemMod(item);
    }

    public interface OnItemModListener {
        void onItemMod(Item i);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.tvOrig.setText(item.getOriginal());
        holder.tvTrans.setText(item.getTranslated());
        holder.etQty.setText(item.getQuantity() + "");
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

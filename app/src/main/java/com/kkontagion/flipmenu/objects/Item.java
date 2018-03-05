package com.kkontagion.flipmenu.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Kon on 4/3/2018.
 */

public class Item implements Serializable {
    String original, translated;
    int id, quantity;

    public Item(int id, String translated, String original) {
        this.id = id;
        this.original = original;
        this.translated = translated;
        this.quantity = 0;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String translated) {
        this.translated = translated;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void inc() {
        this.quantity++;
    }

    public void dec() {
        this.quantity--;
    }

    @Override
    public String toString() {
        return "ID " + id + ": " + translated + "(" + original + ") - " + quantity;
    }
}

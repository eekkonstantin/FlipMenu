package com.kkontagion.flipmenu.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by Kon on 4/3/2018.
 */

public class Item implements Parcelable {
    String original, translated;
    String name, description;
    int id, quantity;

    public Item(int id, String translated, String original) {
        this.id = id;
        this.original = original;
        this.translated = translated;
        this.quantity = 0;
        this.name = translated;
        this.description = original;
    }
    public Item(int id, String translated, String original, String name) {
        this.id = id;
        this.original = original;
        this.translated = translated;
        this.quantity = 0;
        this.name = name;
        this.description = original;
    }
    public Item(int id, String translated, String original, String name, String description) {
        this.id = id;
        this.original = original;
        this.translated = translated;
        this.quantity = 0;
        this.name = name;
        this.description = description;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(quantity);
        parcel.writeString(original);
        parcel.writeString(translated);
        parcel.writeString(name);
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<Item> CREATOR
            = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        this.id = in.readInt();
        this.quantity = in.readInt();
        this.original = in.readString();
        this.translated = in.readString();
        this.name = in.readString();
        this.description = in.readString();
    }

    public int getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String toSpeech() {
        String[] sep = original.split("///");
        return quantity + " " + sep[0];
    }

    @Override
    public String toString() {
        return "ID " + id + ": " + translated + "(" + original + ") - " + quantity;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.id == ((Item) obj).getId()) || super.equals(obj);
    }
}

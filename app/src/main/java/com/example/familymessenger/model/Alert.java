package com.example.familymessenger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Alert implements Parcelable {

    String title;
    String description;
    String family;
    String owner;
    int priority;

    public Alert() {
    }

    public Alert(String title, String description, String family, String owner, int priority) {
        this.title = title;
        this.description = description;
        this.family = family;
        this.owner = owner;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    protected Alert(Parcel in) {
        title = in.readString();
        description = in.readString();
        family = in.readString();
        owner = in.readString();
        priority = in.readInt();
    }

    public static final Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel in) {
            return new Alert(in);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(family);
        dest.writeString(owner);
        dest.writeInt(priority);
    }
}

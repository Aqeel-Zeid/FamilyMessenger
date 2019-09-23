package com.example.familymessenger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Family implements Parcelable {

    private String familyName;
    private  String familyPassword;


    public Family() {
    }

    public Family(String familyName, String familyPassword) {
        this.familyName = familyName;
        this.familyPassword = familyPassword;
    }

    protected Family(Parcel in) {
        familyName = in.readString();
        familyPassword = in.readString();
    }

    public static final Creator<Family> CREATOR = new Creator<Family>() {
        @Override
        public Family createFromParcel(Parcel in) {
            return new Family(in);
        }

        @Override
        public Family[] newArray(int size) {
            return new Family[size];
        }
    };

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyPassword() {
        return familyPassword;
    }

    public void setFamilyPassword(String familyPassword) {
        this.familyPassword = familyPassword;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(familyName);
        parcel.writeString(familyPassword);
    }
}

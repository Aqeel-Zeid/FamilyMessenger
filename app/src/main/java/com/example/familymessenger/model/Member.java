package com.example.familymessenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Member implements Parcelable {

    private String name;
    private String nickName;
    private String password;
    private String phone;
    private String profileImage = "DEFAULT.jpg";

    public Member() {
    }

    public Member(String name, String nickName, String password,  String phone) {
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.phone = phone;
        this.profileImage = "DEFAULT.jpg";
    }

    protected Member(Parcel in) {
        name = in.readString();
        nickName = in.readString();
        password = in.readString();
        phone = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };


    public String getName() {
        return name;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(nickName);
        parcel.writeString(password);
        parcel.writeString(phone);
        parcel.writeString(profileImage);
    }
}

package com.udacity.android.example.popularmoviess2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie model object
 * Created by carlos .
 */

public class Trailer implements Parcelable {
    private String image;
    private String link;

    public Trailer(Parcel in) {
        image = in.readString();
        link = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public Trailer(String image, String link) {
        this.image = image;
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image);
        parcel.writeString(link);
    }
}

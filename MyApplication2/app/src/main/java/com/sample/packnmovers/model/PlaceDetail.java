package com.sample.packnmovers.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;


public class PlaceDetail {

    private String id;
    private Uri websiteUri;
    private String name;
    private String address;
    private String phoneNumber;
    private LatLng latlng;
    private float rating;
    private String attributions;

    public PlaceDetail() {

    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    @Override
    public String toString() {
        return "PlaceDetail{" + "name='" + name + '\'' + ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' + ", id='" + id + '\'' + ", websiteUri=" + websiteUri + ", latlng=" + latlng +
                ", rating=" + rating + ", attributions='" + attributions + '\'' + '}';
    }
}

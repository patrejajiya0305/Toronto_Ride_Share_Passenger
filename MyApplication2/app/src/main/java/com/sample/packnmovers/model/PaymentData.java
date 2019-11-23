package com.sample.packnmovers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class PaymentData extends RealmObject {


    @SerializedName("cardholdername")
    @Expose
    private String cardholdername;
    @SerializedName("cardno")
    @Expose
    private String cardno;
    @SerializedName("expiry")
    @Expose
    private String expiry;
    @SerializedName("cvv")
    @Expose
    private String cvv;
    @SerializedName("id")
    @Expose
    private String id;





    public PaymentData(){

    }

    public String getCardholdername() {
        return cardholdername;
    }

    public void setCardholdername(String cardholdername) {
        this.cardholdername = cardholdername;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

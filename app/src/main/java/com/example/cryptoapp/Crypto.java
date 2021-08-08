package com.example.cryptoapp;

import android.os.Parcelable;

import java.io.Serializable;

public class Crypto implements Serializable {
    public String Cname;
    public String Cabbreviation;
    public boolean isFavourite;

    public Crypto() {
    }

    public Crypto(String Cname, String Cabbreviation, boolean isFavourite) {
        this.Cname = Cname;
        this.Cabbreviation = Cabbreviation;
        this.isFavourite = isFavourite;
    }

    public String getCryptoName() {
        return Cname;
    }

    public String getCryptoAbbreviation() {
        return Cabbreviation;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setCryptoName(String Cname) {
        this.Cname = Cname;
    }

    public void setCryptoAbbreviation(String Cabbreviation) {
        this.Cabbreviation = Cabbreviation;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}

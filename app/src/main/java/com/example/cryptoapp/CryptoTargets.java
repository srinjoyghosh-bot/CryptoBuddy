package com.example.cryptoapp;

public class CryptoTargets {
   public String cryptoName;
   public String asset_Id;
   public String asset_Quote;
   public double targetPrice;
   public double stopLossPrice;

    public CryptoTargets() {
    }

    public CryptoTargets(String cryptoName, String asset_Id, String asset_Quote, double targetPrice, double stopLossPrice) {
        this.cryptoName = cryptoName;
        this.asset_Id = asset_Id;
        this.asset_Quote = asset_Quote;
        this.targetPrice = targetPrice;
        this.stopLossPrice = stopLossPrice;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public String getAsset_Id() {
        return asset_Id;
    }

    public String getAsset_Quote() {
        return asset_Quote;
    }

    public double getTargetPrice() {
        return targetPrice;
    }

    public double getStopLossPrice() {
        return stopLossPrice;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public void setAsset_Id(String asset_Id) {
        this.asset_Id = asset_Id;
    }

    public void setAsset_Quote(String asset_Quote) {
        this.asset_Quote = asset_Quote;
    }

    public void setTargetPrice(double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public void setStopLossPrice(double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }
}

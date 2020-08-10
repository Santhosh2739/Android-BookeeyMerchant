package ycash.wallet.json.pojo;

import ycash.wallet.json.pojo.generic.GenericResponse;

/**
 * Created by 10030 on 11/25/2016.
 */
public class MerchantInfoResponse extends GenericResponse {


    private String merchantName;
    private String mobileNumber;
    private double merchant_balance;
    private String branch;
    private String merchantEmailId;

    public String getMerchantEmailId() {
        return merchantEmailId;
    }

    public void setMerchantEmailId(String merchantEmailId) {
        this.merchantEmailId = merchantEmailId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public double getMerchant_balance() {
        return merchant_balance;
    }

    public void setMerchant_balance(double merchant_balance) {
        this.merchant_balance = merchant_balance;
    }
}
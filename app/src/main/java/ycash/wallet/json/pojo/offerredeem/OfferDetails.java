package ycash.wallet.json.pojo.offerredeem;

import java.util.Date;

public class OfferDetails {
    private String id;
    private Long offerId;
    private Date offerStartDate;
    private Date offerEndDate;
    private String offerName;
    private String bannerText;

    private String offerStartTime;
    private String offerEndTime;
    private String group_name;
    private String merchant_name;
    private String merchantRefNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Date getOfferStartDate() {
        return offerStartDate;
    }

    public void setOfferStartDate(Date offerStartDate) {
        this.offerStartDate = offerStartDate;
    }

    public Date getOfferEndDate() {
        return offerEndDate;
    }

    public void setOfferEndDate(Date offerEndDate) {
        this.offerEndDate = offerEndDate;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getBannerText() {
        return bannerText;
    }

    public void setBannerText(String bannerText) {
        this.bannerText = bannerText;
    }

    public String getOfferStartTime() {
        return offerStartTime;
    }

    public void setOfferStartTime(String offerStartTime) {
        this.offerStartTime = offerStartTime;
    }

    public String getOfferEndTime() {
        return offerEndTime;
    }

    public void setOfferEndTime(String offerEndTime) {
        this.offerEndTime = offerEndTime;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getMerchantRefNumber() {
        return merchantRefNumber;
    }

    public void setMerchantRefNumber(String merchantRefNumber) {
        this.merchantRefNumber = merchantRefNumber;
    }

    public String getMerchant_branch() {
        return merchant_branch;
    }

    public OfferDetails(String id, Long offerId, Date offerStartDate, Date offerEndDate, String offerName, String bannerText, String offerStartTime, String offerEndTime, String group_name, String merchant_name, String merchantRefNumber, String merchant_branch) {
        this.id = id;
        this.offerId = offerId;
        this.offerStartDate = offerStartDate;
        this.offerEndDate = offerEndDate;
        this.offerName = offerName;
        this.bannerText = bannerText;
        this.offerStartTime = offerStartTime;
        this.offerEndTime = offerEndTime;
        this.group_name = group_name;
        this.merchant_name = merchant_name;
        this.merchantRefNumber = merchantRefNumber;
        this.merchant_branch = merchant_branch;
    }

    public void setMerchant_branch(String merchant_branch) {
        this.merchant_branch = merchant_branch;
    }

    private String merchant_branch;



}

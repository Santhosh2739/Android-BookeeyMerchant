package ycash.wallet.json.pojo.pushnotification;

import ycash.wallet.json.pojo.generic.GenericRequest;

/**
 * Created by munireddy on 5/20/2016.
 */
public class SendVersionNumber extends GenericRequest {
    private String imeiNumber;

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getMerchantRefNumber() {
        return merchantRefNumber;
    }

    public void setMerchantRefNumber(String merchantRefNumber) {
        this.merchantRefNumber = merchantRefNumber;
    }

    public String getGCMRegistrationId() {
        return GCMRegistrationId;
    }

    public void setGCMRegistrationId(String GCMRegistrationId) {
        this.GCMRegistrationId = GCMRegistrationId;
    }

    private String merchantRefNumber;
    private String GCMRegistrationId;
    private String typeofAction;
    private String batteryStatus;

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    private String currentLocation;

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public String getTypeofAction() {
        return typeofAction;
    }

    public void setTypeofAction(String typeofAction) {
        this.typeofAction = typeofAction;
    }

    public double getCurrentVersionNumber() {
        return currentVersionNumber;
    }

    public void setCurrentVersionNumber(double currentVersionNumber) {
        this.currentVersionNumber = currentVersionNumber;
    }

    private double currentVersionNumber;
}

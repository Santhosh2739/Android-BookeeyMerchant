package ycash.wallet.json.pojo.merchantlogin;

import coreframework.utils.TransType;
import ycash.wallet.json.pojo.generic.GenericRequest;

public class MerchantLoginRequest extends GenericRequest {
	
	public MerchantLoginRequest(){
		super.setG_transType(TransType.LOGIN_MERCHANT.name());
	}
	
	private String deviceIdNumber;
    private String ipAddress;
    private int deviceType;
	private String lat;
	private String lon;
	private String gcmRegistrationId;
	private String gcmId;
	private String walletApplicationVersion;
	private String deviceOsVersionDetails1;
	private String deviceOsVersionDetails2;
	private String imeiNumber;

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public String getGcmRegistrationId() {
		return gcmRegistrationId;
	}

	public void setGcmRegistrationId(String gcmRegistrationId) {
		this.gcmRegistrationId = gcmRegistrationId;
	}

	public String getDeviceIdNumber() {
		return deviceIdNumber;
	}
	public void setDeviceIdNumber(String deviceIdNumber) {
		this.deviceIdNumber = deviceIdNumber;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getGcmId() {
		return gcmId;
	}
	public void setGcmId(String gcmId) {
		this.gcmId = gcmId;
	}
	public String getWalletApplicationVersion() {
		return walletApplicationVersion;
	}
	public void setWalletApplicationVersion(String walletApplicationVersion) {
		this.walletApplicationVersion = walletApplicationVersion;
	}
	public String getDeviceOsVersionDetails1() {
		return deviceOsVersionDetails1;
	}
	public void setDeviceOsVersionDetails1(String deviceOsVersionDetails1) {
		this.deviceOsVersionDetails1 = deviceOsVersionDetails1;
	}
	public String getDeviceOsVersionDetails2() {
		return deviceOsVersionDetails2;
	}
	public void setDeviceOsVersionDetails2(String deviceOsVersionDetails2) {
		this.deviceOsVersionDetails2 = deviceOsVersionDetails2;
	} 
}

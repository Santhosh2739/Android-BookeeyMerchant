package ycash.wallet.json.pojo.paytomerchant;


import coreframework.utils.TransType;
import ycash.wallet.json.pojo.generic.GenericRequest;

public class PayToMerchantRequest extends GenericRequest{

	private String customerId;
	private double amount;
	private String barcodeData;
	private long clientDate;
	private long barcodeGeneratedDate;
	
	public long getClientDate() {
		return clientDate;
	}
	public void setClientDate(long clientDate) {
		this.clientDate = clientDate;
	}
	public long getBarcodeGeneratedDate() {
		return barcodeGeneratedDate;
	}
	public void setBarcodeGeneratedDate(long barcodeGeneratedDate) {
		this.barcodeGeneratedDate = barcodeGeneratedDate;
	}
	public PayToMerchantRequest(){
		setG_transType(TransType.PAY_TO_MERCHANT.name());
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getBarcodeData() {
		return barcodeData;
	}
	public void setBarcodeData(String barcodeData) {
		this.barcodeData = barcodeData;
	}
}
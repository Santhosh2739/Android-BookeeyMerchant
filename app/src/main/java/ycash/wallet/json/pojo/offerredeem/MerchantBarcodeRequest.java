package ycash.wallet.json.pojo.offerredeem;


import ycash.wallet.json.pojo.generic.GenericRequest;

public class MerchantBarcodeRequest extends GenericRequest {
	
	private Long offerId;
	private String customerId;

	public String getMer_reference() {
		return merchantRefNumber;
	}

	public void setMer_reference(String mer_reference) {
		this.merchantRefNumber = mer_reference;
	}

	private String merchantRefNumber;


	public Long getOfferId() {
		return offerId;
	}
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}

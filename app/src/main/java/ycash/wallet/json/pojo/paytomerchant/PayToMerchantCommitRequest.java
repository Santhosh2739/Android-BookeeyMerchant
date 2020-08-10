package ycash.wallet.json.pojo.paytomerchant;


import coreframework.utils.TransType;
import ycash.wallet.json.pojo.generic.GenericRequest;

public class PayToMerchantCommitRequest extends GenericRequest{
	
	public PayToMerchantCommitRequest(){
		setG_transType(TransType.PAY_TO_MERCHANT_COMMIT_REQUEST.name());
	}
	
	private String transactionId;
	private int accept_code;
	private long clientDate;
	private Long offerId;

	public Long getOfferId() {
		return offerId;
	}

	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public int getAccept_code() {
		return accept_code;
	}
	public void setAccept_code(int accept_code) {
		this.accept_code = accept_code;
	}
	public long getClientDate() {
		return clientDate;
	}
	public void setClientDate(long clientDate) {
		this.clientDate = clientDate;
	}
}

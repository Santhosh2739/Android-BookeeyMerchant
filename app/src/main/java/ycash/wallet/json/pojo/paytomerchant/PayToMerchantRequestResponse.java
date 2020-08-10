package ycash.wallet.json.pojo.paytomerchant;


import coreframework.utils.TransType;
import ycash.wallet.json.pojo.generic.GenericResponse;

public class PayToMerchantRequestResponse extends GenericResponse {

	private String transactionId;
	private double mer_balance;
	private double processingfee;
	private double total;
	private long serverTime;
	private String customerMobileNumber;

	//offer
	private Long offerId;
	private double discountPrice;
	private double discount;
	private double afterDiscountAmount;
	private String offerDescription;

	public String getOfferDescription() {
		return offerDescription;
	}

	public void setOfferDescription(String offerDescription) {
		this.offerDescription = offerDescription;
	}


	public Long getOfferId() {
		return offerId;
	}

	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getAfterDiscountAmount() {
		return afterDiscountAmount;
	}

	public void setAfterDiscountAmount(double afterDiscountAmount) {
		this.afterDiscountAmount = afterDiscountAmount;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	private double totalPrice;


    public double getMerchant_balance() {
        return merchant_balance;
    }

    public void setMerchant_balance(double merchant_balance) {
        this.merchant_balance = merchant_balance;
    }

    private double merchant_balance;

	public double getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(double txnAmount) {
		this.txnAmount = txnAmount;
	}

	private String customerId;

	public String getCustomerMobileNumber() {
		return customerMobileNumber;
	}

	public void setCustomerMobileNumber(String customerMobileNumber) {
		this.customerMobileNumber = customerMobileNumber;
	}

	private double txnAmount;
	
//	public PayToMerchantRequestResponse(int g_status, String g_status_description,
//			String g_errorDescription, String g_response_trans_type, String transactionId,
//			double mer_balance, double processingfee, double total,
//			long serverTime, String customerId) {
//		super(g_status, g_status_description, g_errorDescription, g_response_trans_type);
//		this.transactionId = transactionId;
//		this.mer_balance = mer_balance;
//		this.processingfee = processingfee;
//		this.total = total;
//		this.serverTime = serverTime;
//		this.customerId = customerId;
//	}
	public PayToMerchantRequestResponse(){
		setG_response_trans_type(TransType.PAY_TO_MERCHANT_RESPONSE.name());
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public double getProcessingfee() {
		return processingfee;
	}
	public void setProcessingfee(double processingfee) {
		this.processingfee = processingfee;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public long getServerTime() {
		return serverTime;
	}
	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}
	public double getMer_balance() {
		return mer_balance;
	}
	public void setMer_balance(double mer_balance) {
		this.mer_balance = mer_balance;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}	
}

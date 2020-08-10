package ycash.wallet.json.pojo.paytomerchant;


import coreframework.utils.TransType;
import ycash.wallet.json.pojo.generic.GenericResponse;

public class PayToMerchantCommitRequestResponse extends GenericResponse{
	
	private String transactionId;
	private double balance;
	private double processingfee;
	private double total;
	private long serverTime;
    private double merchant_balance;
    private String customerMobileNumber;



	public String getMdrCommission() {
		return mdrCommission;
	}

	public void setMdrCommission(String mdrCommission) {
		this.mdrCommission = mdrCommission;
	}



	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	private String mdrCommission;
	private String paymentType;

	private double discountPrice;
	private double totalPrice;

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public double getTotalAmountDebittedFromCust() {
        return totalAmountDebittedFromCust;
    }

    public void setTotalAmountDebittedFromCust(double totalAmountDebittedFromCust) {
        this.totalAmountDebittedFromCust = totalAmountDebittedFromCust;
    }

    public double getTotalAmountCreditedToMerchant() {
        return totalAmountCreditedToMerchant;
    }

    public void setTotalAmountCreditedToMerchant(double totalAmountCreditedToMerchant) {
        this.totalAmountCreditedToMerchant = totalAmountCreditedToMerchant;
    }

    private double totalAmountDebittedFromCust;
    private double totalAmountCreditedToMerchant;

	public String getCustomerMobileNumber() {
		return customerMobileNumber;
	}

	public void setCustomerMobileNumber(String customerMobileNumber) {
		this.customerMobileNumber = customerMobileNumber;
	}

    public double getMerchant_balance() {
        return merchant_balance;
    }

    public void setMerchant_balance(double merchant_balance) {
        this.merchant_balance = merchant_balance;
    }



	public double getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(double txnAmount) {
		this.txnAmount = txnAmount;
	}

	private long serverTimeL2;
	private String customerId;
	private double txnAmount;
	
//	public PayToMerchantCommitRequestResponse(int g_status, String g_status_description,
//			String g_errorDescription, String g_response_trans_type, String transactionId,
//			double balance, double processingfee, double total,
//			long serverTime, long serverTimeL2, String customerId) {
//		super(g_status, g_status_description, g_errorDescription, g_response_trans_type);
//		this.transactionId = transactionId;
//		this.balance = balance;
//		this.processingfee = processingfee;
//		this.total = total;
//		this.serverTime = serverTime;
//		this.serverTimeL2 = serverTimeL2;
//		this.customerId = customerId;
//	}
	
	public PayToMerchantCommitRequestResponse(){
		setG_response_trans_type(TransType.PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE.name());
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
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
	public long getServerTimeL2() {
		return serverTimeL2;
	}
	public void setServerTimeL2(long serverTimeL2) {
		this.serverTimeL2 = serverTimeL2;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}

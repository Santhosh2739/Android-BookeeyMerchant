package ycash.wallet.json.pojo.sendmoney;


import ycash.wallet.json.pojo.generic.GenericResponse;

/**
 * 
 * @author mohit
 *
 */
public class SendMoneyCommitRequestResponse extends GenericResponse{
	
	private String transactionId;
	private double balance;
	private double processingfee;
	private double total;
	private long serverTime;
	private long serverTimeL2;
	private String recipientName;
	private String recipientMobileNumber;
	
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
	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getRecipientMobileNumber() {
		return recipientMobileNumber;
	}
	public void setRecipientMobileNumber(String recipientMobileNumber) {
		this.recipientMobileNumber = recipientMobileNumber;
	}
}

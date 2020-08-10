package ycash.wallet.json.pojo.sendmoney;


import ycash.wallet.json.pojo.generic.GenericResponse;

/**
 * 
 * @author mohit
 *
 */
public class SendMoneyRequestResponse extends GenericResponse{
	
	private String transactionId;
	private double balance;
	private double processingfee;
	private double total;
	private long serverTime;
	private String recipientName;
	private String recipientMobileNumber;
	
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
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}

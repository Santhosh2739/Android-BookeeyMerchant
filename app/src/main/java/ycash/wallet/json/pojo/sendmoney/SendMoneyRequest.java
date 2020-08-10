package ycash.wallet.json.pojo.sendmoney;


import ycash.wallet.json.pojo.generic.GenericRequest;

/**
 * 
 * @author mohit
 *
 */
public class SendMoneyRequest extends GenericRequest{
	
	private String recipientName;
	private String recipientMobileNumber;
	private double amount;
	private int requestReferenceNumber;
	private boolean isSelectedFromPhoneBook = false;
	private boolean isSelectedFromAssistedList = false;
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
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getRequestReferenceNumber() {
		return requestReferenceNumber;
	}
	public void setRequestReferenceNumber(int requestReferenceNumber) {
		this.requestReferenceNumber = requestReferenceNumber;
	}
	public boolean isSelectedFromPhoneBook() {
		return isSelectedFromPhoneBook;
	}
	public void setSelectedFromPhoneBook(boolean isSelectedFromPhoneBook) {
		this.isSelectedFromPhoneBook = isSelectedFromPhoneBook;
	}
	public boolean isSelectedFromAssistedList() {
		return isSelectedFromAssistedList;
	}
	public void setSelectedFromAssistedList(boolean isSelectedFromAssistedList) {
		this.isSelectedFromAssistedList = isSelectedFromAssistedList;
	}
}

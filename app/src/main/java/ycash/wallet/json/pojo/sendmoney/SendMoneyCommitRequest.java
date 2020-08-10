package ycash.wallet.json.pojo.sendmoney;


import ycash.wallet.json.pojo.generic.GenericRequest;

/**
 * 
 * @author mohit
 *
 */
public class SendMoneyCommitRequest extends GenericRequest{
	
	private String transactionId;
	private int accept_code;
	
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
}

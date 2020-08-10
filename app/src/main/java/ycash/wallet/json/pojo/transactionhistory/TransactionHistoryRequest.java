package ycash.wallet.json.pojo.transactionhistory;


import ycash.wallet.json.pojo.generic.GenericRequest;

public class TransactionHistoryRequest extends GenericRequest{

	private int from;
//	private int count;
	
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
//	public int getCount() {
//		return count;
//	}
//	public void setCount(int count) {

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
//		this.count = count;
//	}

	private String tranType;
	private String mobileNo;

}

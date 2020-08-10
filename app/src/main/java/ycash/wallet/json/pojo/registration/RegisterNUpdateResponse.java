package ycash.wallet.json.pojo.registration;

import java.io.Serializable;



/**
 * @author poongodi
 */
public class RegisterNUpdateResponse implements Serializable {
	
	private static final long serialVersionUID = 1832859276676989814L;

	public RegisterNUpdateResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public RegisterNUpdateResponse(String walletCustomerId, String customerId,
			String mobileNumber) {
		super();
		this.walletCustomerId = walletCustomerId;
		this.customerId = customerId;
		this.mobileNumber = mobileNumber;
	}

	private String walletCustomerId;
	
	private String customerId;
	
	private String mobileNumber;
	
	private String response_Code;
	
	private String response_Description;
	
	
	public String getResponse_Code() {
		return response_Code;
	}


	public void setResponse_Code(String response_Code) {
		this.response_Code = response_Code;
	}


	public String getWalletCustomerId() {
		return walletCustomerId;
	}


	public void setWalletCustomerId(String walletCustomerId) {
		this.walletCustomerId = walletCustomerId;
	}



	public String getCustomerId() {
		return customerId;
	}



	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}



	public String getMobileNumber() {
		return mobileNumber;
	}



	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}



	public String getResponse_Description() {
		return response_Description;
	}



	public void setResponse_Description(String response_Description) {
		this.response_Description = response_Description;
	}



//	@Override
//	public String toString() {
//		return new ToStringBuilder(this)
//        .append("walletCustomerId", walletCustomerId)
//        .append("customerId", customerId)
//        .append("mobileNumber", mobileNumber)
//        .toString();
//	}
	
}

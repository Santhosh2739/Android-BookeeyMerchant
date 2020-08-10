package ycash.wallet.json.pojo.merchantlogin;

import ycash.wallet.json.pojo.generic.GenericResponse;

public class MerchantLoginRequestResponse extends GenericResponse{
	
//	public MerchantLoginRequestResponse(int g_status, String g_status_description,
//			String g_errorDescription,String g_response_trans_type, boolean isDualMode, String enc_key,
//			String mac_key, int sequence, String merchantName, String oauth_2_0_client_token) {
//		super(g_status, g_status_description, g_errorDescription, g_response_trans_type);
//		this.isDualMode = isDualMode;
//		this.enc_key = enc_key;
//		this.mac_key = mac_key;
//		this.sequence = sequence;
//		this.merchantName = merchantName;
//		this.oauth_2_0_client_token = oauth_2_0_client_token;
//	}
	private boolean isDualMode = false;
	private String enc_key;
	private String mac_key;
	private String oauth_2_0_client_token;
	private int sequence;
	private String merchantName;
    private String merchantContactPersonName;

	public boolean isMerchantExtraFields() {
		return merchantExtraFields;
	}

	public void setMerchantExtraFields(boolean merchantExtraFields) {
		this.merchantExtraFields = merchantExtraFields;
	}

	private boolean merchantExtraFields;

    public String getMerchantContactPersonName() {
        return merchantContactPersonName;
    }

    public void setMerchantContactPersonName(String merchantContactPersonName) {
        this.merchantContactPersonName = merchantContactPersonName;
    }

    public String getMerchantAddress() {
		return merchantAddress;
	}

	public void setMerchantAddress(String merchantAddress) {
		this.merchantAddress = merchantAddress;
	}

	private String merchantAddress;

	public String getMerchantLogo() {
		return merchantLogo;
	}

	public void setMerchantLogo(String merchantLogo) {
		this.merchantLogo = merchantLogo;
	}

	private String merchantLogo;
	
	public String getOauth_2_0_client_token() {
		return oauth_2_0_client_token;
	}
	public void setOauth_2_0_client_token(String oauth_2_0_client_token) {
		this.oauth_2_0_client_token = oauth_2_0_client_token;
	}
	public boolean isDualMode() {
		return isDualMode;
	}
	public void setDualMode(boolean isDualMode) {
		this.isDualMode = isDualMode;
	}
	public String getEnc_key() {
		return enc_key;
	}
	public void setEnc_key(String enc_key) {
		this.enc_key = enc_key;
	}
	public String getMac_key() {
		return mac_key;
	}
	public void setMac_key(String mac_key) {
		this.mac_key = mac_key;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
}

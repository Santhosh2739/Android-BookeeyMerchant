package ycash.wallet.json.pojo.generic;

import wallet.ooredo.com.live.forceface.TransTypeInterface;

/**
 * 
 * @author mohit
 *
 */
public class GenericResponse implements TransTypeInterface {
	
	private int g_status;
	private String g_status_description;
	private String g_errorDescription;
	private String g_response_trans_type;
	private String g_servertime;
	private String typeOfMerchant;

	public String getTypeOfMerchant() {
		return typeOfMerchant;
	}

	public void setTypeOfMerchant(String typeOfMerchant) {
		this.typeOfMerchant = typeOfMerchant;
	}

	public String getG_servertime() {
		return g_servertime;
	}

	public void setG_servertime(String g_servertime) {
		this.g_servertime = g_servertime;
	}

	public int getG_status() {
		return g_status;
	}
	public void setG_status(int g_status) {
		this.g_status = g_status;
	}
	public String getG_status_description() {
		return g_status_description;
	}
	public void setG_status_description(String g_status_description) {
		this.g_status_description = g_status_description;
	}
	public String getG_errorDescription() {
		return g_errorDescription;
	}
	public void setG_errorDescription(String g_errorDescription) {
		this.g_errorDescription = g_errorDescription;
	}
	public String getG_response_trans_type() {
		return g_response_trans_type;
	}
	public void setG_response_trans_type(String g_response_trans_type) {
		this.g_response_trans_type = g_response_trans_type;
	}

	@Override
	public String toString() {
		return "GenericResponse{" +
				"g_status=" + g_status +
				", g_status_description='" + g_status_description + '\'' +
				", g_errorDescription='" + g_errorDescription + '\'' +
				", g_response_trans_type='" + g_response_trans_type + '\'' +
				", g_servertime='" + g_servertime + '\'' +
				", typeOfMerchant='" + typeOfMerchant + '\'' +
				'}';
	}
}

package ycash.wallet.json.pojo.generic;


/**
 * 
 * @author mohit
 *
 */
public class GenericRequest {
	
	private String g_userId;
	private String g_password;
	private String g_oauth_2_0_client_token;
	private int g_security_counter;
	private String g_transType;
	
	public String getG_userId() {
		return g_userId;
	}
	public void setG_userId(String g_userId) {
		this.g_userId = g_userId;
	}
	public String getG_password() {
		return g_password;
	}
	public void setG_password(String g_password) {
		this.g_password = g_password;
	}
	public String getG_oauth_2_0_client_token() {
		return g_oauth_2_0_client_token;
	}
	public void setG_oauth_2_0_client_token(String g_oauth_2_0_client_token) {
		this.g_oauth_2_0_client_token = g_oauth_2_0_client_token;
	}
	public int getG_security_counter() {
		return g_security_counter;
	}
	public void setG_security_counter(int g_security_counter) {
		this.g_security_counter = g_security_counter;
	}
	public String getG_transType() {
		return g_transType;
	}
	public void setG_transType(String g_transType) {
		this.g_transType = g_transType;
	}
}

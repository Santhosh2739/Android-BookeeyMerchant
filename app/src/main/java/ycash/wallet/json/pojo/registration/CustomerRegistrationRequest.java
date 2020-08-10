package ycash.wallet.json.pojo.registration;

import java.io.Serializable;
import java.sql.Blob;

/**
 * @author Diwakar
 */
public class CustomerRegistrationRequest {

	private String agentId;
	private String orgiginating_Country;
	private String customer_Almulla_Ref;
	
	private String customer_Civil_Id;
	
	private String customer_Civil_Id_Expiry_Date;
	
	private String customer_FirstName;

	private String customer_LastName;
	
	private String customer_MobileNumber;

//	OPTIONAL
	private String customer_Email;
	
//	OPTIONAL
	private Blob customer_Civil_Id_Copy;
	
//	private List<BeneficiaryDetailReq> beneficiaryDetails = new ArrayList<BeneficiaryDetailReq>();
	private String beneficiary_Almulla_Ref;
	
	private String beneficiary_FirstName;
	
//	OPTIONAL
	private String beneficiary_LastName;
	
	private String language;
	
	private String MSISDN;
	private String Device_ID_number;
	private String Civil_ID_Image;
	private Blob civilIDImage;
	private String PIN;
	
	public Blob getCivilIDImage() {
		return civilIDImage;
	}

	public void setCivilIDImage(Blob civilIDImage) {
		this.civilIDImage = civilIDImage;
	}

	public String getMSISDN() {
		return MSISDN;
	}

	public void setMSISDN(String mSISDN) {
		MSISDN = mSISDN;
	}

	public String getDevice_ID_number() {
		return Device_ID_number;
	}

	public void setDevice_ID_number(String device_ID_number) {
		Device_ID_number = device_ID_number;
	}

	public String getCivil_ID_Image() {
		return Civil_ID_Image;
	}

	public void setCivil_ID_Image(String civil_ID_Image) {
		Civil_ID_Image = civil_ID_Image;
	}

	public String getPIN() {
		return PIN;
	}

	public void setPIN(String pIN) {
		PIN = pIN;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAgentId() {
		return agentId;
	}

	/*public List<BeneficiaryDetailReq> getBeneficiaryDetails() {
		return beneficiaryDetails;
	}

	public void setBeneficiaryDetails(List<BeneficiaryDetailReq> beneficiaryDetails) {
		this.beneficiaryDetails = beneficiaryDetails;
	}*/

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getOrgiginating_Country() {
		return orgiginating_Country;
	}

	public void setOrgiginating_Country(String orgiginating_Country) {
		this.orgiginating_Country = orgiginating_Country;
	}

	public String getCustomer_Almulla_Ref() {
		return customer_Almulla_Ref;
	}

	public void setCustomer_Almulla_Ref(String customer_Almulla_Ref) {
		this.customer_Almulla_Ref = customer_Almulla_Ref;
	}

	public String getCustomer_Civil_Id() {
		return customer_Civil_Id;
	}

	public void setCustomer_Civil_Id(String customer_Civil_Id) {
		this.customer_Civil_Id = customer_Civil_Id;
	}

	public String getCustomer_Civil_Id_Expiry_Date() {
		return customer_Civil_Id_Expiry_Date;
	}

	public void setCustomer_Civil_Id_Expiry_Date(
			String customer_Civil_Id_Expiry_Date) {
		this.customer_Civil_Id_Expiry_Date = customer_Civil_Id_Expiry_Date;
	}

	public String getCustomer_FirstName() {
		return customer_FirstName;
	}

	public void setCustomer_FirstName(String customer_FirstName) {
		this.customer_FirstName = customer_FirstName;
	}

	public String getCustomer_LastName() {
		return customer_LastName;
	}

	public void setCustomer_LastName(String customer_LastName) {
		this.customer_LastName = customer_LastName;
	}

	public String getCustomer_MobileNumber() {
		return customer_MobileNumber;
	}

	public void setCustomer_MobileNumber(String customer_MobileNumber) {
		this.customer_MobileNumber = customer_MobileNumber;
	}

	public String getCustomer_Email() {
		return customer_Email;
	}

	public void setCustomer_Email(String customer_Email) {
		this.customer_Email = customer_Email;
	}

	public Blob getCustomer_Civil_Id_Copy() {
		return customer_Civil_Id_Copy;
	}

	public void setCustomer_Civil_Id_Copy(Blob customer_Civil_Id_Copy) {
		this.customer_Civil_Id_Copy = customer_Civil_Id_Copy;
	}

	
	public String getBeneficiary_Almulla_Ref() {
		return beneficiary_Almulla_Ref;
	}

	public void setBeneficiary_Almulla_Ref(String beneficiary_Almulla_Ref) {
		this.beneficiary_Almulla_Ref = beneficiary_Almulla_Ref;
	}

	public String getBeneficiary_FirstName() {
		return beneficiary_FirstName;
	}

	public void setBeneficiary_FirstName(String beneficiary_FirstName) {
		this.beneficiary_FirstName = beneficiary_FirstName;
	}

	public String getBeneficiary_LastName() {
		return beneficiary_LastName;
	}

	public void setBeneficiary_LastName(String beneficiary_LastName) {
		this.beneficiary_LastName = beneficiary_LastName;
	}
	
}

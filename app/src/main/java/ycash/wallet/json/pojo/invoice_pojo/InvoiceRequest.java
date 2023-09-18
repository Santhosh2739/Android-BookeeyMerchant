package ycash.wallet.json.pojo.invoice_pojo;

import java.util.Arrays;

import ycash.wallet.json.pojo.generic.GenericRequest;

public class InvoiceRequest extends GenericRequest {

    private String invoiceNo;
    private String mobileNo;
    private String sendSMSto;
    private double amount;
    private String merchantRefNumber;
    private boolean isMerchantRequest;
    private String description;
    private String custName;
    private String custEmailId;
    private String language;
    private boolean isSMSSent;
    public byte[] getArabicDescription() {
        return arabicDescription;
    }

    public void setArabicDescription(byte[] arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    private byte[] arabicDescription;

    public byte[] getArabicCustomerName() {
        return arabicCustomerName;
    }

    public void setArabicCustomerName(byte[] arabicCustomerName) {
        this.arabicCustomerName = arabicCustomerName;
    }

    private byte[] arabicCustomerName;

    public String getClientImage() {
        return clientImage;
    }

    public void setClientImage(String clientImage) {
        this.clientImage = clientImage;
    }

    //for hospital
    private String medFileNo;
    private String accountNo;
    private String civilId;
    private String nurseId;


    private String clientImage;


    public String getMedFileNo() {
        return medFileNo;
    }

    public void setMedFileNo(String medFileNo) {
        this.medFileNo = medFileNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getCivilId() {
        return civilId;
    }

    public void setCivilId(String civilId) {
        this.civilId = civilId;
    }

    public String getNurseId() {
        return nurseId;
    }

    public void setNurseId(String nurseId) {
        this.nurseId = nurseId;
    }


    public String getInvoiceLink() {
        return invoiceLink;
    }

    public void setInvoiceLink(String invoiceLink) {
        this.invoiceLink = invoiceLink;
    }

    private String invoiceLink;

    public String getSendSMSto() {
        return sendSMSto;
    }

    public void setSendSMSto(String sendSMSto) {
        this.sendSMSto = sendSMSto;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustEmailId() {
        return custEmailId;
    }

    public void setCustEmailId(String custEmailId) {
        this.custEmailId = custEmailId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMerchantRequest() {
        return isMerchantRequest;
    }

    public void setMerchantRequest(boolean merchantRequest) {
        isMerchantRequest = merchantRequest;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMerchantRefNumber() {
        return merchantRefNumber;
    }

    public void setMerchantRefNumber(String merchantRefNumber) {
        this.merchantRefNumber = merchantRefNumber;
    }

    public boolean isSMSSent() {
        return isSMSSent;
    }

    public void setSMSSent(boolean SMSSent) {
        isSMSSent = SMSSent;
    }

    @Override
    public String toString() {
        return "InvoiceRequest{" +
                "invoiceNo='" + invoiceNo + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", amount=" + amount +
                ", merchantRefNumber='" + merchantRefNumber + '\'' +
                ", isMerchantRequest=" + isMerchantRequest +
                ", description='" + description + '\'' +
                ", custName='" + custName + '\'' +
                ", custEmailId='" + custEmailId + '\'' +
                ", language='" + language + '\'' +
                ", isSMSSent=" + isSMSSent +
                ", arabicDescription=" + Arrays.toString(arabicDescription) +
                ", arabicCustomerName=" + Arrays.toString(arabicCustomerName) +
                ", medFileNo='" + medFileNo + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", civilId='" + civilId + '\'' +
                ", nurseId='" + nurseId + '\'' +
                ", clientImage='" + clientImage + '\'' +
                ", invoiceLink='" + invoiceLink + '\'' +
                '}';
    }
}

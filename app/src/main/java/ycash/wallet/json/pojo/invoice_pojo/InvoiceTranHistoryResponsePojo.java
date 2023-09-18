package ycash.wallet.json.pojo.invoice_pojo;

import java.util.Arrays;

import ycash.wallet.json.pojo.generic.GenericResponse;
public class InvoiceTranHistoryResponsePojo extends GenericResponse {
    private String transactionId;
    private long serverTime;
    private String operatorName;
    private String recipientMobileNumber;
    private String rechargeAmt;
    private double denominationinKWD;
    private String paymentRefId;
    private double senderbalanceAfeter;
    private String tranType;
    private String rechargeCode;
    private String serialNo;
    private String billType;
    private Byte paymentStatus;
    private String branch;
    private String receiverWalletId;
    private String attacjmentLink;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getReceiverWalletId() {
        return receiverWalletId;
    }

    public void setReceiverWalletId(String receiverWalletId) {
        this.receiverWalletId = receiverWalletId;
    }

    public String getAttacjmentLink() {
        return attacjmentLink;
    }

    public void setAttacjmentLink(String attacjmentLink) {
        this.attacjmentLink = attacjmentLink;
    }

    public String getTotalAmountCreditedToMerchant() {
        return totalAmountCreditedToMerchant;
    }

    public void setTotalAmountCreditedToMerchant(String totalAmountCreditedToMerchant) {
        this.totalAmountCreditedToMerchant = totalAmountCreditedToMerchant;
    }

    private String totalAmountCreditedToMerchant;

    private String mdrCommission;

    public String getMdrCommission() {
        return mdrCommission;
    }

    public void setMdrCommission(String mdrCommission) {
        this.mdrCommission = mdrCommission;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    private String paymentType;

    public byte[] getArabicCustomerName() {
        return arabicCustomerName;
    }

    public void setArabicCustomerName(byte[] arabicCustomerName) {
        this.arabicCustomerName = arabicCustomerName;
    }

    public byte[] getArabicDescription() {
        return arabicDescription;
    }

    public void setArabicDescription(byte[] arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    private String reason;
    private String description;
    private String customerName;

    private byte[] arabicCustomerName;
    private byte[] arabicDescription;

    //offers
    private Long offerId;
    private double discountPrice;
    private double totalPrice;
    private String offerDescription;

    //invoice link
    private String invoiceLink;

    //hospital merchant
    private String medFileNo;
    private String accountNo;
    private String civilId;
    private String nurseId;

    public String getInvoiceLink() {
        return invoiceLink;
    }

    public void setInvoiceLink(String invoiceLink) {
        this.invoiceLink = invoiceLink;
    }


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

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    private String emailId;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getRecipientMobileNumber() {
        return recipientMobileNumber;
    }

    public void setRecipientMobileNumber(String recipientMobileNumber) {
        this.recipientMobileNumber = recipientMobileNumber;
    }

    public String getRechargeAmt() {
        return rechargeAmt;
    }

    public void setRechargeAmt(String rechargeAmt) {
        this.rechargeAmt = rechargeAmt;
    }

    public double getDenominationinKWD() {
        return denominationinKWD;
    }

    public void setDenominationinKWD(double denominationinKWD) {
        this.denominationinKWD = denominationinKWD;
    }

    public String getPaymentRefId() {
        return paymentRefId;
    }

    public void setPaymentRefId(String paymentRefId) {
        this.paymentRefId = paymentRefId;
    }

    public double getSenderbalanceAfeter() {
        return senderbalanceAfeter;
    }

    public void setSenderbalanceAfeter(double senderbalanceAfeter) {
        this.senderbalanceAfeter = senderbalanceAfeter;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getRechargeCode() {
        return rechargeCode;
    }

    public void setRechargeCode(String rechargeCode) {
        this.rechargeCode = rechargeCode;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public Byte getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Byte paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "InvoiceTranHistoryResponsePojo{" +
                "transactionId='" + transactionId + '\'' +
                ", serverTime=" + serverTime +
                ", operatorName='" + operatorName + '\'' +
                ", recipientMobileNumber='" + recipientMobileNumber + '\'' +
                ", rechargeAmt='" + rechargeAmt + '\'' +
                ", denominationinKWD=" + denominationinKWD +
                ", paymentRefId='" + paymentRefId + '\'' +
                ", senderbalanceAfeter=" + senderbalanceAfeter +
                ", tranType='" + tranType + '\'' +
                ", rechargeCode='" + rechargeCode + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", billType='" + billType + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", totalAmountCreditedToMerchant='" + totalAmountCreditedToMerchant + '\'' +
                ", mdrCommission='" + mdrCommission + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", reason='" + reason + '\'' +
                ", description='" + description + '\'' +
                ", customerName='" + customerName + '\'' +
                ", arabicCustomerName=" + Arrays.toString(arabicCustomerName) +
                ", arabicDescription=" + Arrays.toString(arabicDescription) +
                ", offerId=" + offerId +
                ", discountPrice=" + discountPrice +
                ", totalPrice=" + totalPrice +
                ", offerDescription='" + offerDescription + '\'' +
                ", invoiceLink='" + invoiceLink + '\'' +
                ", medFileNo='" + medFileNo + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", civilId='" + civilId + '\'' +
                ", nurseId='" + nurseId + '\'' +
                ", emailId='" + emailId + '\'' +
                '}';
    }
}

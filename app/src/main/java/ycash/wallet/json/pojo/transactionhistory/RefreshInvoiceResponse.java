package ycash.wallet.json.pojo.transactionhistory;

public class RefreshInvoiceResponse {
    public int id;
    public double amount;
    public double totalAmountDebittedFromCust;
    public double totalAmountCreditedToMerchant;
    public String clientRequestDate;
    public String clientConfirmationDate;
    public int tranStatus;
    public int walletTranStatus;
    public String transactionRefNo;
    public String merchantRefNumber;
    public String mobileNumber;
    public double cdrCommission;
    public double mdrCommission;
    public String invoiceNo;
    public int paymentStatus;
    public String merchantName;
    public boolean isMerchantRequest;
    public String description;
    public String customerName;
    public String language;
    public int views;
    public String arabicDescription;
    public String invoicelink;
    public double totalTxnAmt;

    @Override
    public String toString() {
        return "RefreshInvoiceResponse{" +
                "id=" + id +
                ", amount=" + amount +
                ", totalAmountDebittedFromCust=" + totalAmountDebittedFromCust +
                ", totalAmountCreditedToMerchant=" + totalAmountCreditedToMerchant +
                ", clientRequestDate='" + clientRequestDate + '\'' +
                ", clientConfirmationDate='" + clientConfirmationDate + '\'' +
                ", tranStatus=" + tranStatus +
                ", walletTranStatus=" + walletTranStatus +
                ", transactionRefNo='" + transactionRefNo + '\'' +
                ", merchantRefNumber='" + merchantRefNumber + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", cdrCommission=" + cdrCommission +
                ", mdrCommission=" + mdrCommission +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", merchantName='" + merchantName + '\'' +
                ", isMerchantRequest=" + isMerchantRequest +
                ", description='" + description + '\'' +
                ", customerName='" + customerName + '\'' +
                ", language='" + language + '\'' +
                ", views=" + views +
                ", arabicDescription='" + arabicDescription + '\'' +
                ", invoicelink='" + invoicelink + '\'' +
                ", totalTxnAmt=" + totalTxnAmt +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTotalAmountDebittedFromCust() {
        return totalAmountDebittedFromCust;
    }

    public void setTotalAmountDebittedFromCust(double totalAmountDebittedFromCust) {
        this.totalAmountDebittedFromCust = totalAmountDebittedFromCust;
    }

    public double getTotalAmountCreditedToMerchant() {
        return totalAmountCreditedToMerchant;
    }

    public void setTotalAmountCreditedToMerchant(double totalAmountCreditedToMerchant) {
        this.totalAmountCreditedToMerchant = totalAmountCreditedToMerchant;
    }

    public String getClientRequestDate() {
        return clientRequestDate;
    }

    public void setClientRequestDate(String clientRequestDate) {
        this.clientRequestDate = clientRequestDate;
    }

    public String getClientConfirmationDate() {
        return clientConfirmationDate;
    }

    public void setClientConfirmationDate(String clientConfirmationDate) {
        this.clientConfirmationDate = clientConfirmationDate;
    }

    public int getTranStatus() {
        return tranStatus;
    }

    public void setTranStatus(int tranStatus) {
        this.tranStatus = tranStatus;
    }

    public int getWalletTranStatus() {
        return walletTranStatus;
    }

    public void setWalletTranStatus(int walletTranStatus) {
        this.walletTranStatus = walletTranStatus;
    }

    public String getTransactionRefNo() {
        return transactionRefNo;
    }

    public void setTransactionRefNo(String transactionRefNo) {
        this.transactionRefNo = transactionRefNo;
    }

    public String getMerchantRefNumber() {
        return merchantRefNumber;
    }

    public void setMerchantRefNumber(String merchantRefNumber) {
        this.merchantRefNumber = merchantRefNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public double getCdrCommission() {
        return cdrCommission;
    }

    public void setCdrCommission(double cdrCommission) {
        this.cdrCommission = cdrCommission;
    }

    public double getMdrCommission() {
        return mdrCommission;
    }

    public void setMdrCommission(double mdrCommission) {
        this.mdrCommission = mdrCommission;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public boolean isMerchantRequest() {
        return isMerchantRequest;
    }

    public void setMerchantRequest(boolean merchantRequest) {
        isMerchantRequest = merchantRequest;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getArabicDescription() {
        return arabicDescription;
    }

    public void setArabicDescription(String arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    public String getInvoicelink() {
        return invoicelink;
    }

    public void setInvoicelink(String invoicelink) {
        this.invoicelink = invoicelink;
    }

    public double getTotalTxnAmt() {
        return totalTxnAmt;
    }

    public void setTotalTxnAmt(double totalTxnAmt) {
        this.totalTxnAmt = totalTxnAmt;
    }
}

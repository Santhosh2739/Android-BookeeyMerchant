package ycash.wallet.json.pojo.invoice_pojo;

import ycash.wallet.json.pojo.generic.GenericRequest;

public class InvoiceExpiry extends GenericRequest {

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    private String invoiceNo;
}

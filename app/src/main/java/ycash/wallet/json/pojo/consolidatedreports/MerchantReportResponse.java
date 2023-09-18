package ycash.wallet.json.pojo.consolidatedreports;


import ycash.wallet.json.pojo.generic.GenericResponse;

public class MerchantReportResponse extends GenericResponse {

    public MerchantReportResponse() {

    }

    private int txnCount;
    private double sumOfTxnAmount;
    private double sumOfAmtToBeCredited;

    public int getTxnCount() {
        return txnCount;
    }

    public void setTxnCount(int txnCount) {
        this.txnCount = txnCount;
    }

    public double getSumOfTxnAmount() {
        return sumOfTxnAmount;
    }

    public void setSumOfTxnAmount(double sumOfTxnAmount) {
        this.sumOfTxnAmount = sumOfTxnAmount;
    }

    public double getSumOfAmtToBeCredited() {
        return sumOfAmtToBeCredited;
    }

    public void setSumOfAmtToBeCredited(double sumOfAmtToBeCredited) {
        this.sumOfAmtToBeCredited = sumOfAmtToBeCredited;
    }

}

package ycash.wallet.json.pojo.transactionhistory;

public class RefreshTransactionRequest {

    private String transactionRefNo;

    @Override
    public String toString() {
        return "RefreshTransactionRequest{" +
                "transactionRefNo='" + transactionRefNo + '\'' +
                '}';
    }

    public String getTransactionRefNo() {
        return transactionRefNo;
    }

    public void setTransactionRefNo(String transactionRefNo) {
        this.transactionRefNo = transactionRefNo;
    }
}

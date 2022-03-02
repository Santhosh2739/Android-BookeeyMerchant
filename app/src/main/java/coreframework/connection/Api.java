package coreframework.connection;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ycash.wallet.json.pojo.transactionhistory.RefreshInvoiceResponse;
import ycash.wallet.json.pojo.transactionhistory.RefreshTransactionRequest;
public interface Api {
    @POST("knetBmerchantinvocieRequery")
    Call<RefreshInvoiceResponse> refreshTransactionData(@Body RefreshTransactionRequest param);
}

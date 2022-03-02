package coreframework.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import coreframework.connection.Config;
import coreframework.connection.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceTranHistoryResponsePojo;
import ycash.wallet.json.pojo.transactionhistory.RefreshInvoiceResponse;
import ycash.wallet.json.pojo.transactionhistory.RefreshTransactionRequest;
public class RefreshTransactionController {

    public static RefreshTransactionController refreshTransactionController = new RefreshTransactionController();
    private Activity activity;
    private PaymentResponse paymentResponse;

    public static RefreshTransactionController getInstance() {
        return refreshTransactionController;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(PaymentResponse paymentResponse) {
        this.paymentResponse = paymentResponse;
    }
    public void initiatePayment(RefreshTransactionRequest data) {
        Call<RefreshInvoiceResponse> apiCall = RetrofitClient.getInstance(Config.ADDRESS_BASE).getApi().refreshTransactionData(data);
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        // Log.e("theme", theme.toString());
        apiCall.enqueue(new Callback<RefreshInvoiceResponse>() {
            @Override
            public void onResponse(@NotNull Call<RefreshInvoiceResponse> call, @NotNull Response<RefreshInvoiceResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    RefreshInvoiceResponse responseData = response.body();
                    if (responseData != null && responseData.getPaymentStatus() == 2 && responseData.getTranStatus() == 2) {
                            (RefreshTransactionController.getInstance()).getPaymentResponse().paymentResponse("CAPTURED");
                    }
                    Log.e("Response if", response.body().toString());
                } else {
                    (RefreshTransactionController.getInstance()).getPaymentResponse().paymentResponse("PENDING");
                }
            }

            @Override
            public void onFailure(@NotNull Call<RefreshInvoiceResponse> call, @NotNull Throwable t) {
                Log.e("responseData Failed : ", t.getMessage());
                progressDialog.dismiss();
                (RefreshTransactionController.getInstance()).getPaymentResponse().paymentResponse("PENDING");
            }
        });
    }


    public interface PaymentResponse {
        void paymentResponse(String response);
    }
}

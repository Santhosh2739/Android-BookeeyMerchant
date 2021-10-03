package wallet.ooredo.com.live.transactionhistory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Date;

import coreframework.database.CustomSharedPreferences;
import coreframework.processing.InvoiceExpiryProcessing;
import coreframework.processing.ReminderProcessing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import coreframework.utils.PriceFormatter;
import coreframework.utils.TimeUtils;
import coreframework.utils.TransType;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.forceface.TransTypeInterface;
import wallet.ooredo.com.live.utils.PrintManagerSDK;
import wangpos.sdk4.libbasebinder.Printer;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceExpiry;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceRequest;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceTranHistoryResponsePojo;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantCommitRequestResponse;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantRequestResponse;
/**
 * Created by mohit on 7/1/16.
 */
public class TransactionHistoryDisplayActivity extends GenericActivity implements YPCHeadlessCallback {
    private static final int MESSAGE_PRINT = 0x00;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_PRINT) {
                String message = (String) msg.obj;
                showMessage(message);
            }
        }

        ;
    };
    int status = 0;
    public boolean bthreadrunning = false;
    String selectedLanguage = "en";
    TableRow payment_after_bal_tr, date_tr, payment_confirm_fullname_row, payment_confirm_emailid_row, payment_confirm_desc_row, payment_confirm_reason_row,
            payment_confirm_total_amt_row,
            payment_confirm_discount_amt_row,
            payment_confirm_offerID_row,
            payment_confirm_invoice_tr,
            payment_confirm_med_no_tr,
            payment_confirm_civilID_tr,
            payment_confirm_nurseID_tr,
            payment_confirm_accNo_tr,
            payment_confirm_payment_method_row,
            payment_confirm_fee_row,
            payment_confirm_due_amount_to_merchant_row,
            payment_merchantID,
    payment_branch;
    String typeOfResponse;
    String time_zone_str = "";
    private ImageView ypcm_close, payment_success_img_id, payment_attachment_img;
    private TextView payment_confirm_status, payment_confirm_txn_id, payment_confirm_date_id, payment_confirm_total_payment_id, payment_confirm_wallet_number_id,
            payment_confirm_merchant_balance, payment_confirm_credited_amount,
            payment_confirm_emaild_id, payment_confirm_reason_id,
            payment_confirm_total_amt_value,
            payment_confirm_discount_amt_value, payment_confirm_offerID_value,
            payment_confirm_invoice_link_text,
            payment_confirm_med_no_text,
            payment_confirm_civilID_text,
            payment_confirm_nurseID_text,
            payment_merchantID_text,
            payment_branch_text,
            payment_confirm_accNo_text,
            payment_confirm_fee_value,
            payment_confirm_payment_method_value,
            payment_attachment_img_text,
            payment_confirm_due_amount_to_merchant_value;
    private Button invoice_reminder_btn, invoice_close_btn, invoice_share_btn, txn_history_print_btn, invoice_expire_btn;
    private Printer mPrinter;
    private Message message = null;
    private boolean bloop = false;
    LinearLayout payment_attachment_img_div;
    private PayToMerchantCommitRequestResponse payToMerchantCommitRequestResponse = null;
    private InvoiceTranHistoryResponsePojo tran_invoice = null;
    private EditText payment_confirm_fullname_id, payment_confirm_desc_id;
    Bitmap bmImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if(selectedLanguage.equals("ar"))
            setContentView(R.layout.payment_confirmation_new_right);
        else
            setContentView(R.layout.payment_confirmation_new);
        new Thread() {
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }.start();
        payment_confirm_status = (TextView) findViewById(R.id.payment_confirm_status);
        payment_confirm_txn_id = (TextView) findViewById(R.id.payment_confirm_txn_id);
        payment_confirm_date_id = (TextView) findViewById(R.id.payment_confirm_date_id);
        payment_confirm_total_payment_id = (TextView) findViewById(R.id.payment_confirm_total_payment_id);
        payment_confirm_wallet_number_id = (TextView) findViewById(R.id.payment_confirm_wallet_number_id);
        payment_success_img_id = (ImageView) findViewById(R.id.payment_success_img_id);
        payment_confirm_merchant_balance = (TextView) findViewById(R.id.payment_confirm_merchant_balance);
        payment_merchantID_text = (TextView) findViewById(R.id.payment_merchantID_text);
        payment_branch_text = (TextView) findViewById(R.id.payment_branch_text);
        payment_attachment_img_div = findViewById(R.id.payment_attachment_img_div);
        payment_confirm_credited_amount = (TextView) findViewById(R.id.payment_confirm_credited_amount);
        payment_after_bal_tr = (TableRow) findViewById(R.id.payment_after_bal_tr);
        invoice_reminder_btn = (Button) findViewById(R.id.invoice_reminder_btn);
        invoice_share_btn = (Button) findViewById(R.id.invoice_share_btn);
        invoice_expire_btn = (Button) findViewById(R.id.invoice_expire_btn);
        invoice_close_btn = (Button) findViewById(R.id.invoice_close_btn);
        payment_confirm_fullname_row = (TableRow) findViewById(R.id.payment_confirm_fullname_row);
        payment_confirm_emailid_row = (TableRow) findViewById(R.id.payment_confirm_emailid_row);
        payment_confirm_desc_row = (TableRow) findViewById(R.id.payment_confirm_desc_row);
        payment_confirm_reason_row = (TableRow) findViewById(R.id.payment_confirm_reason_row);
        payment_confirm_fullname_id = (EditText) findViewById(R.id.payment_confirm_fullname_id);
        payment_confirm_emaild_id = (TextView) findViewById(R.id.payment_confirm_emaild_id);
        payment_confirm_desc_id = (EditText) findViewById(R.id.payment_confirm_desc_id);
        payment_confirm_reason_id = (TextView) findViewById(R.id.payment_confirm_reason_id);
        payment_merchantID = (TableRow) findViewById(R.id.payment_merchantID);
        payment_branch = (TableRow) findViewById(R.id.payment_branch);
        //print
        txn_history_print_btn = (Button) findViewById(R.id.txn_history_print_btn);
        //For P2M offres
        payment_confirm_total_amt_row = (TableRow) findViewById(R.id.payment_confirm_total_amt_row);
        payment_confirm_discount_amt_row = (TableRow) findViewById(R.id.payment_confirm_discount_amt_row);
        payment_confirm_offerID_row = (TableRow) findViewById(R.id.payment_confirm_offerID_row);
        payment_confirm_total_amt_value = (TextView) findViewById(R.id.payment_confirm_total_amt_value);
        payment_confirm_discount_amt_value = (TextView) findViewById(R.id.payment_confirm_discount_amt_value);
        payment_confirm_offerID_value = (TextView) findViewById(R.id.payment_confirm_offerID_value);
        payment_confirm_payment_method_value = (TextView) findViewById(R.id.payment_confirm_payment_method_value);
        payment_confirm_fee_value = (TextView) findViewById(R.id.payment_confirm_fee_value);
        payment_confirm_due_amount_to_merchant_value = (TextView) findViewById(R.id.payment_confirm_due_amount_to_merchant_value);
        payment_confirm_payment_method_row = (TableRow) findViewById(R.id.payment_confirm_payment_method_row);
        payment_confirm_fee_row = (TableRow) findViewById(R.id.payment_confirm_fee_row);
        payment_confirm_due_amount_to_merchant_row = (TableRow) findViewById(R.id.payment_confirm_due_amount_to_merchant_row);
        //for invoice newly added
        payment_confirm_invoice_tr = (TableRow) findViewById(R.id.payment_confirm_invoice_tr);
        payment_confirm_invoice_link_text = (TextView) findViewById(R.id.payment_confirm_invoice_link_text);
        //payment_confirm_invoice_link_text.setTextIsSelectable(true);
        final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.payment_confirm_body_id);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_LEFT);
            }
        });
        payment_confirm_invoice_link_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the Operation System SDK version as an int
                int sdkVer = android.os.Build.VERSION.SDK_INT;
                //For Older Android SDK versions
                if (sdkVer < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    @SuppressWarnings("deprecation")
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(payment_confirm_invoice_link_text.getText().toString());
                }
                //For Newer Versions
                else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Message", payment_confirm_invoice_link_text.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(TransactionHistoryDisplayActivity.this, "Link Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        payment_attachment_img_div = findViewById(R.id.payment_attachment_img_div);
        payment_attachment_img = findViewById(R.id.payment_attachment_img);
        payment_attachment_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment_attachment_img_text.performClick();
            }
        });
        payment_attachment_img_text = findViewById(R.id.payment_attachment_img_text);

        //for hospital merchant newly added
        payment_confirm_med_no_tr = (TableRow) findViewById(R.id.payment_confirm_med_no_tr);
        payment_confirm_civilID_tr = (TableRow) findViewById(R.id.payment_confirm_civilID_tr);
        payment_confirm_nurseID_tr = (TableRow) findViewById(R.id.payment_confirm_nurseID_tr);
        payment_confirm_accNo_tr = (TableRow) findViewById(R.id.payment_confirm_accNo_tr);
        payment_confirm_med_no_text = (TextView) findViewById(R.id.payment_confirm_med_no_text);
        payment_confirm_civilID_text = (TextView) findViewById(R.id.payment_confirm_civilID_text);
        payment_confirm_nurseID_text = (TextView) findViewById(R.id.payment_confirm_nurseID_text);
        payment_confirm_accNo_text = (TextView) findViewById(R.id.payment_confirm_accNo_text);
        date_tr = (TableRow) findViewById(R.id.date_tr);
        String json = getIntent().getStringExtra("transaction");
        typeOfResponse = getIntent().getStringExtra("type");
        TransType type = TransType.valueOf(typeOfResponse);
        switch (type) {
            case PAY_TO_MERCHANT_RESPONSE:
                loadDetails(new Gson().fromJson(json, PayToMerchantRequestResponse.class));
                break;
            case PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE:
                loadDetails(new Gson().fromJson(json, PayToMerchantCommitRequestResponse.class));
                break;
            case INVOICE_TRAN_RESPONSE:
                loadDetails(new Gson().fromJson(json, InvoiceTranHistoryResponsePojo.class));
                break;
        }
        ypcm_close = (ImageView) findViewById(R.id.payment_success_img_id);
        ypcm_close.setOnClickListener(v -> finish());
//        Print
        txn_history_print_btn.setOnClickListener(v -> {
            if (((CoreApplication) getApplication()).isPOS()) {
                int[] status = new int[1];
                int ret = -1;
                try {
                    ret = mPrinter.getPrinterStatus(status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ret == 0) {
                    startPrint();
                } else {
                    Toast.makeText(getApplicationContext(), "Printer not available on device..!" + ret, Toast.LENGTH_SHORT).show();
                }
            } else {
                startSDKPrint();
            }
        });
    }

    public void showMessage(String str) {
        Toast.makeText(TransactionHistoryDisplayActivity.this, str + "", Toast.LENGTH_SHORT).show();
    }

    private void startPrint() {
        bloop = false;
        if (!bthreadrunning)
            new PrintThread().start();
    }

    private void startSDKPrint() {
        new Thread() {
            public void run() {
                try {
                    int ret;
                    if (typeOfResponse.equalsIgnoreCase("PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE")) {
                        ret = printSDKText();
                    } else {
                        ret = printSDKInvoiceText();
                    }
                    message = new Message();
                    message.what = MESSAGE_PRINT;
                    message.obj = ret + "";
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());
    }

    @Override
    public void onProgressUpdate(int progress) {
    }

    @Override
    public void onProgressComplete() {
    }

    private void loadDetails(TransTypeInterface transTypeInterface) {
        //TODO: Initialize Global Views If Any
        TransType type = TransType.valueOf(transTypeInterface.getG_response_trans_type());
        String send_version_response = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.SEND_VERSION_RESPONSE);
        try {
            JSONObject send_version_response_jo = new JSONObject(send_version_response);
            time_zone_str = send_version_response_jo.getString("g_servertime");
//            Toast.makeText(getApplicationContext(),"TimeZone : "+time_zone_str,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(),"TimeZone Ex: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        invoice_close_btn.setVisibility(View.VISIBLE);
        invoice_close_btn.setOnClickListener(v -> finish());
        switch (type) {
            case PAY_TO_MERCHANT_RESPONSE:
                PayToMerchantRequestResponse payToMerchantRequestResponse = (PayToMerchantRequestResponse) transTypeInterface;
                if (payToMerchantRequestResponse.getG_status_description().contains("Cancelled by merchant")) {
                    payment_confirm_status.setText("" + getString(R.string.cancelled_by_merchant));
                } else {
                    payment_confirm_status.setText(payToMerchantRequestResponse.getG_status_description());
                }
                //String branch = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.LOCATION);
                //String merchantId = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
                if (payToMerchantRequestResponse.getReceiverWalletId() != null && payToMerchantRequestResponse.getReceiverWalletId() != "") {
                    payment_merchantID_text.setText(payToMerchantRequestResponse.getReceiverWalletId());
                    payment_merchantID.setVisibility(View.VISIBLE);
                }
                if (payToMerchantRequestResponse.getBranch() != null && payToMerchantRequestResponse.getBranch() != "") {
                    payment_branch_text.setText(payToMerchantRequestResponse.getBranch());
                    payment_branch.setVisibility(View.VISIBLE);
                }
                payment_confirm_txn_id.setText("" + payToMerchantRequestResponse.getTransactionId());
                payment_confirm_date_id.setText("" + TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(payToMerchantRequestResponse.getServerTime())));
                payment_confirm_wallet_number_id.setText(payToMerchantRequestResponse.getCustomerMobileNumber());
                payment_confirm_total_payment_id.setText(getString(R.string.total_paymnet) + "    KWD " + PriceFormatter.format(payToMerchantRequestResponse.getTxnAmount(), 3, 3));
                payment_confirm_merchant_balance.setText(PriceFormatter.format(payToMerchantRequestResponse.getMer_balance(), 3, 3));
                switch (payToMerchantRequestResponse.getG_status()) {
                    case 1:
                        payment_success_img_id.setBackgroundResource(R.drawable.success);
                        status = 0;
                        break;
                    case -1:
                        payment_success_img_id.setBackgroundResource(R.drawable.red_cross);
                        status = 3;
                        break;
                }
                break;
            case PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE:
                if (((CoreApplication) getApplication()).isPOS() || ((CoreApplication) getApplication()).isNewPOS()) {
                    txn_history_print_btn.setVisibility(View.VISIBLE);
                }
                payToMerchantCommitRequestResponse = (PayToMerchantCommitRequestResponse) transTypeInterface;
                //June 23 2020 START
                Log.e("Transaction:", payToMerchantCommitRequestResponse.toString());
                if (payToMerchantCommitRequestResponse.getReceiverWalletId() != null && payToMerchantCommitRequestResponse.getReceiverWalletId() != "") {
                    payment_merchantID_text.setText(payToMerchantCommitRequestResponse.getReceiverWalletId());
                    payment_merchantID.setVisibility(View.VISIBLE);
                }
                if (payToMerchantCommitRequestResponse.getBranch() != null && payToMerchantCommitRequestResponse.getBranch() != "") {
                    payment_branch_text.setText(payToMerchantCommitRequestResponse.getBranch());
                    payment_branch.setVisibility(View.VISIBLE);
                }
                payment_confirm_due_amount_to_merchant_value.setText("" + payToMerchantCommitRequestResponse.getTotalAmountCreditedToMerchant() + " KWD");
                if (payToMerchantCommitRequestResponse.getPaymentType() != null) {
                    payment_confirm_payment_method_value.setText(payToMerchantCommitRequestResponse.getPaymentType());
                } else {
                    payment_confirm_payment_method_row.setVisibility(View.GONE);
                }
                if (payToMerchantCommitRequestResponse.getMdrCommission() != null) {
                    payment_confirm_fee_value.setText(payToMerchantCommitRequestResponse.getMdrCommission() + " KWD");
                } else {
                    payment_confirm_fee_row.setVisibility(View.GONE);
                }
                //June 23 2020 END
                if (payToMerchantCommitRequestResponse.getG_status_description().contains("Cancelled by merchant")) {
                    payment_confirm_status.setText("" + getString(R.string.cancelled_by_merchant));
                } else if (payToMerchantCommitRequestResponse.getG_status_description().contains("SUCCESS")) {
                    payment_confirm_status.setText(payToMerchantCommitRequestResponse.getG_status_description());
                } else {
                    payment_confirm_status.setText(payToMerchantCommitRequestResponse.getG_status_description());
//
                }
//                payment_confirm_status.setText(payToMerchantCommitRequestResponse.getG_status_description());
                payment_confirm_txn_id.setText("" + payToMerchantCommitRequestResponse.getTransactionId());
                payment_confirm_date_id.setText("" + TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(payToMerchantCommitRequestResponse.getServerTime())));
                payment_confirm_wallet_number_id.setText(payToMerchantCommitRequestResponse.getCustomerMobileNumber());
                payment_confirm_total_payment_id.setText(getString(R.string.total_paymnet) + "  KWD " + PriceFormatter.format(payToMerchantCommitRequestResponse.getTxnAmount(), 3, 3));
                payment_confirm_credited_amount.setText("KWD " + PriceFormatter.format(payToMerchantCommitRequestResponse.getTotalAmountCreditedToMerchant(), 3, 3));
                payment_confirm_merchant_balance.setText("KWD " + PriceFormatter.format(payToMerchantCommitRequestResponse.getMerchant_balance(), 3, 3));
                if (payToMerchantCommitRequestResponse.getTotalPrice() != 0) {
                    payment_confirm_total_amt_row.setVisibility(View.VISIBLE);
                    payment_confirm_total_amt_value.setText("" + PriceFormatter.format(payToMerchantCommitRequestResponse.getTotalPrice(), 3, 3) + " KWD");
                }
                if (payToMerchantCommitRequestResponse.getDiscountPrice() != 0) {
                    payment_confirm_discount_amt_row.setVisibility(View.VISIBLE);
                    payment_confirm_discount_amt_value.setText("" + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD");
                }
                switch (payToMerchantCommitRequestResponse.getG_status()) {
                    case 1:
                        payment_success_img_id.setBackgroundResource(R.drawable.success);
                        status = 0;
                        break;
                    case -1:
                        payment_success_img_id.setBackgroundResource(R.drawable.red_cross);
                        status = 3;
                        break;
                }
                break;
            case INVOICE_TRAN_RESPONSE:
                if (((CoreApplication) getApplication()).isPOS() || ((CoreApplication) getApplication()).isNewPOS()) {
                    txn_history_print_btn.setVisibility(View.VISIBLE);
                }
                tran_invoice = (InvoiceTranHistoryResponsePojo) transTypeInterface;
                Log.e("tran_invoice:", tran_invoice.toString());
                if (tran_invoice.getReceiverWalletId() != null && tran_invoice.getReceiverWalletId() != "") {
                    payment_merchantID_text.setText(tran_invoice.getReceiverWalletId());
                    payment_merchantID.setVisibility(View.VISIBLE);
                }
                if (tran_invoice.getBranch() != null && tran_invoice.getBranch() != "") {
                    payment_branch_text.setText(tran_invoice.getBranch());
                    payment_branch.setVisibility(View.VISIBLE);
                }
                //Toast.makeText(TransactionHistoryDisplayActivity.this,""+tran_invoice.getG_servertime(),Toast.LENGTH_LONG).show();
                payment_confirm_status.setText("" + tran_invoice.getG_status_description());
                payment_confirm_txn_id.setText("" + tran_invoice.getTransactionId());
                if (tran_invoice.getServerTime() != 0) {
                    date_tr.setVisibility(View.VISIBLE);
                    payment_confirm_date_id.setText("" + TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(tran_invoice.getServerTime())));
                } else {
                    date_tr.setVisibility(View.GONE);
                }
                if (tran_invoice.getCustomerName() != null && !tran_invoice.getCustomerName().equals("")) {
                    payment_confirm_fullname_row.setVisibility(View.VISIBLE);
                    payment_confirm_fullname_id.setText(tran_invoice.getCustomerName());
                } else {
                    payment_confirm_fullname_row.setVisibility(View.GONE);
                }
                if (tran_invoice.getArabicCustomerName() != null) {
                    payment_confirm_fullname_row.setVisibility(View.VISIBLE);
                    try {
                        Charset charset = Charset.forName("ISO-8859-6");
                        CharsetDecoder decoder = charset.newDecoder();
                        ByteBuffer buf = ByteBuffer.wrap(tran_invoice.getArabicCustomerName());
                        CharBuffer cbuf = decoder.decode(buf);
                        CharSequence customer_name = java.nio.CharBuffer.wrap(cbuf);
                        payment_confirm_fullname_id.setText("" + customer_name + "");
                        payment_confirm_fullname_id.setEnabled(false);
                    } catch (Exception e) {
                        Log.e("Invoice CustName Ex:", "" + e.getMessage());
                    }
                }
                if (tran_invoice.getEmailId() != null && tran_invoice.getEmailId() != "") {
                    payment_confirm_emailid_row.setVisibility(View.VISIBLE);
                    payment_confirm_emaild_id.setText(tran_invoice.getEmailId().trim());
                } else {
                    payment_confirm_emailid_row.setVisibility(View.GONE);
                }
                //reject
                if (tran_invoice.getDescription() != null && tran_invoice.getDescription() != "" && tran_invoice.getDescription() != " ") {
                    payment_confirm_desc_row.setVisibility(View.VISIBLE);
                    payment_confirm_desc_id.setText(tran_invoice.getDescription().trim());
                    payment_confirm_desc_id.setEnabled(false);
                } else {
                    payment_confirm_desc_row.setVisibility(View.GONE);
                }
                if (tran_invoice.getArabicDescription() != null) {
                    payment_confirm_desc_row.setVisibility(View.VISIBLE);
                    try {
                        Charset charset = Charset.forName("ISO-8859-6");
                        CharsetDecoder decoder = charset.newDecoder();
                        ByteBuffer buf = ByteBuffer.wrap(tran_invoice.getArabicDescription());
                        CharBuffer cbuf = decoder.decode(buf);
                        CharSequence description = java.nio.CharBuffer.wrap(cbuf);
                        payment_confirm_desc_id.setText(description + "");
                        payment_confirm_desc_id.setEnabled(false);
                    } catch (Exception e) {
                        Log.e("Invoice Descr Ex:", "" + e.getMessage());
                    }
                }
                if (tran_invoice.getBillType() != "" && tran_invoice.getBillType() != null) {
                    payment_confirm_reason_row.setVisibility(View.VISIBLE);
                    payment_confirm_reason_id.setText(tran_invoice.getBillType());
                } else {
                    payment_confirm_reason_row.setVisibility(View.GONE);
                }
                //merchant hospital newly added
                if (tran_invoice.getMedFileNo() != "" && tran_invoice.getMedFileNo() != null) {
                    payment_confirm_med_no_tr.setVisibility(View.VISIBLE);
                    payment_confirm_med_no_text.setText(tran_invoice.getMedFileNo());
                } else {
                    payment_confirm_med_no_tr.setVisibility(View.GONE);
                }
                if (tran_invoice.getAccountNo() != "" && tran_invoice.getAccountNo() != null) {
                    payment_confirm_accNo_tr.setVisibility(View.VISIBLE);
                    payment_confirm_accNo_text.setText(tran_invoice.getAccountNo());
                } else {
                    payment_confirm_accNo_tr.setVisibility(View.GONE);
                }
                if (tran_invoice.getCivilId() != "" && tran_invoice.getCivilId() != null) {
                    payment_confirm_civilID_tr.setVisibility(View.VISIBLE);
                    payment_confirm_civilID_text.setText(tran_invoice.getCivilId());
                } else {
                    payment_confirm_civilID_tr.setVisibility(View.GONE);
                }
                if (tran_invoice.getNurseId() != "" && tran_invoice.getNurseId() != null) {
                    payment_confirm_nurseID_tr.setVisibility(View.VISIBLE);
                    payment_confirm_nurseID_text.setText(tran_invoice.getNurseId());
                } else {
                    payment_confirm_nurseID_tr.setVisibility(View.GONE);
                }
                //offres module
                if (tran_invoice.getTotalPrice() != 0) {
                    payment_confirm_total_amt_row.setVisibility(View.VISIBLE);
                    payment_confirm_total_amt_value.setText("" + PriceFormatter.format(tran_invoice.getTotalPrice(), 3, 3) + " KWD");
                }
                if (tran_invoice.getDiscountPrice() != 0) {
                    payment_confirm_discount_amt_row.setVisibility(View.VISIBLE);
                    payment_confirm_discount_amt_value.setText("" + PriceFormatter.format(tran_invoice.getDiscountPrice(), 3, 3) + " KWD");
                }
                if (tran_invoice.getOfferDescription() != null && !tran_invoice.getOfferDescription().equals("")) {
                    payment_confirm_offerID_row.setVisibility(View.VISIBLE);
                    payment_confirm_offerID_value.setText("" + tran_invoice.getOfferDescription());
                } else {
                    payment_confirm_offerID_row.setVisibility(View.GONE);
                }
                //invoice link
               /* if (tran_invoice.getInvoiceLink() != null && !tran_invoice.getInvoiceLink().isEmpty()) {
                    payment_confirm_invoice_tr.setVisibility(View.VISIBLE);
                    payment_confirm_invoice_link_text.setText("" + tran_invoice.getInvoiceLink());
                } else {
                    payment_confirm_invoice_tr.setVisibility(View.GONE);
                }*/
                payment_confirm_wallet_number_id.setText(tran_invoice.getRecipientMobileNumber());
                payment_confirm_total_payment_id.setText(getString(R.string.total_paymnet) + " KWD " + PriceFormatter.format(Double.parseDouble(tran_invoice.getRechargeAmt()), 3, 3));
                // payment_confirm_credited_amount.setText("KWD "+PriceFormatter.format(tran_invoice.getTotalAmountCreditedToMerchant(), 3, 3));
                payment_after_bal_tr.setVisibility(View.GONE);
                // payment_confirm_merchant_balance.setText("KWD " + PriceFormatter.format(tran_invoice.getSenderbalanceAfeter(), 3, 3));
                if (tran_invoice.getAttacjmentLink() != null && !tran_invoice.getAttacjmentLink().isEmpty() && tran_invoice.getAttacjmentLink() != "") {
                    payment_attachment_img_div.setVisibility(View.VISIBLE);
                } else {
                    payment_attachment_img_div.setVisibility(View.GONE);
                }
                payment_attachment_img_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog promptsView = new Dialog(TransactionHistoryDisplayActivity.this);
                        promptsView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        promptsView.setContentView(R.layout.imager_alert);
                        final ImageView img = promptsView.findViewById(R.id.dialog_imageview);
                        final Button close = promptsView.findViewById(R.id.attached_img_close_btn);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                promptsView.dismiss();
                            }
                        });
                        new DownloadImageTask(img).execute(tran_invoice.getAttacjmentLink());
                        promptsView.show();
                    }
                });
                switch (tran_invoice.getPaymentStatus()) {
                    case 0:
                        status = 1;
                        payment_success_img_id.setBackgroundResource(R.drawable.txn_hold);
//                        payment_confirm_status.setText(getString(R.string.pending));
                        payment_confirm_status.setText("PENDING");
                        invoice_reminder_btn.setVisibility(View.VISIBLE);
                        invoice_share_btn.setVisibility(View.VISIBLE);
                        invoice_expire_btn.setVisibility(View.VISIBLE);
                        //for remind the invoice
                        invoice_reminder_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reminderRequst(tran_invoice.getTransactionId(), tran_invoice.getPaymentRefId());
                            }
                        });
                        invoice_close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        invoice_expire_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                invoiceExpiryAlertDialog(tran_invoice.getSerialNo());
                            }
                        });
                        //for share the invoice
                        invoice_share_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String json = new Gson().toJson(tran_invoice);
//                                Intent intent = new Intent(getBaseContext(), InvoiceActivity.class);
//                                intent.putExtra("INVOICE_EDIT_DETAILS", json);
//                                intent.putExtra("TXN_PENDING_EDIT", true);
//                                startActivity(intent);
                                String merchant_name = CustomSharedPreferences.getStringData((CoreApplication) getApplication(), CustomSharedPreferences.SP_KEY.NAME);
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Invoice");
                                sendIntent.putExtra(Intent.EXTRA_TEXT,
                                        "Dear customer,\nYou have an invoice from " + merchant_name + "." + " \nClick on the below link to view and pay\n\nStatus: " + "Pending" + "\nDate: " + TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(tran_invoice.getServerTime())) + "\nTransaction ID: " + tran_invoice.getTransactionId() + "\nCust Mobile No: " + tran_invoice.getRecipientMobileNumber() + "\nTotal Amount: " + PriceFormatter.format(tran_invoice.getTotalPrice(), 3, 3) + " KWD" + "\nInvoice URL: " + tran_invoice.getInvoiceLink()
                                );
                                sendIntent.setType("text/plain");
//                                startActivity(sendIntent);
                                startActivity(Intent.createChooser(sendIntent, "Choose any!"));
//                                startActivityForResult(sendIntent,1);
//                                Intent receiver = new Intent((CoreApplication)getApplication(), MyIntentChooserReceiver.class);
//                                PendingIntent pendingIntent = PendingIntent.getBroadcast((CoreApplication)getApplication(), 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
//                                startActivity(Intent.createChooser(sendIntent
//                                        , "Choose any!"
//                                        , pendingIntent.getIntentSender()));
                            }
                        });
                        break;
                    case 1:
                        status = 3;
                        payment_success_img_id.setBackgroundResource(R.drawable.mer_fail);
//                        payment_confirm_status.setText(R.string.hold);
                        payment_confirm_status.setText("HOLD");
                        break;
                    case 2:
                        status = 0;
                        payment_success_img_id.setBackgroundResource(R.drawable.success);
//                        payment_confirm_status.setText(getString(R.string.success));
                        payment_confirm_status.setText("SUCCESS");
                        //June 23 2020 START
                        if (tran_invoice.getTotalAmountCreditedToMerchant() != null) {
                            payment_confirm_due_amount_to_merchant_row.setVisibility(View.VISIBLE);
                            payment_confirm_due_amount_to_merchant_value.setText("" + tran_invoice.getTotalAmountCreditedToMerchant() + " KWD");
                        } else {
                            payment_confirm_due_amount_to_merchant_row.setVisibility(View.GONE);
                        }
                        if (tran_invoice.getPaymentType() != null) {
                            payment_confirm_payment_method_row.setVisibility(View.VISIBLE);
                            payment_confirm_payment_method_value.setText(tran_invoice.getPaymentType());
                        } else {
                            payment_confirm_payment_method_row.setVisibility(View.GONE);
                        }
                        if (tran_invoice.getMdrCommission() != null) {
                            payment_confirm_fee_row.setVisibility(View.VISIBLE);
                            payment_confirm_fee_value.setText(tran_invoice.getMdrCommission() + " KWD");
                        } else {
                            payment_confirm_fee_row.setVisibility(View.GONE);
                        }
                        //June 23 2020 END
                        break;
                    case 3:
                        status = 3;
                        payment_success_img_id.setBackgroundResource(R.drawable.red_cross);
//                        payment_confirm_status.setText(getString(R.string.rejected));
                        payment_confirm_status.setText("REJECTED");
//                        payment_confirm_status.setText("FAILED");
                        /*invoice_reminder_btn.setVisibility(View.VISIBLE);
                        invoice_reminder_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String json = new Gson().toJson(tran_invoice);
                                Intent intent = new Intent(getBaseContext(), InvoiceActivity.class);
                                intent.putExtra("INVOICE_EDIT_DETAILS", json);
                                intent.putExtra("TXN_PENDING_EDIT", true);
                                startActivity(intent);
                            }
                        });*/
                        break;
                    case 4:
                        status = 3;
                        payment_success_img_id.setBackgroundResource(R.drawable.red_cross);
//                        payment_confirm_status.setText(getString(R.string.failed));
                        payment_confirm_status.setText("FAILED");
                        break;
                    case 5:
                        status = 2;
                        payment_success_img_id.setBackgroundResource(R.drawable.expired);
//                        payment_confirm_status.setText(getString(R.string.failed));
                        payment_confirm_status.setText("EXPIRED");
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
    }

    private void invoiceExpiryAlertDialog(final String tran_invoice) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custom_alert_image, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TransactionHistoryDisplayActivity.this);
        alertDialog.setView(promptsView);
        alertDialog.setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                InvoiceExpiry ie = new InvoiceExpiry();
                ie.setInvoiceNo(tran_invoice);
                //Code to start server thread and display the progress fragment dialogue (retained)
                CoreApplication application = (CoreApplication) getApplication();
                String uiProcessorReference = application.addUserInterfaceProcessor(new InvoiceExpiryProcessing(ie, application, true));
                ProgressDialogFrag progress = new ProgressDialogFrag();
                Bundle bundle = new Bundle();
                bundle.putString("uuid", uiProcessorReference);
                progress.setCancelable(false);
                progress.setArguments(bundle);
                progress.show(getFragmentManager(), "progress_dialog");
            }
        });
        alertDialog.setNegativeButton(R.string.alert_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.create().dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(TransactionHistoryDisplayActivity.this.getApplicationContext(), "onActivityResult..:" + data.toString(), Toast.LENGTH_SHORT).show();
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Toast.makeText(TransactionHistoryDisplayActivity.this.getApplicationContext(), "Got Callback yeppeee...:", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void reminderRequst(String invoiceNo, String paymentRefId) {
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setInvoiceNo(paymentRefId);
        //invoiceRequest.setMerchantRefNumber(paymentRefId);
        /*invoiceRequest.setMobileNo(mobno_str);
        invoiceRequest.setDescription(remarks_str);
        invoiceRequest.setCustName(fullName);
        invoiceRequest.setCustEmailId(emailID);*/

       /* invoiceRequest.setMerchantRequest(true);
        String merchant_ref_id = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
        invoiceRequest.setMerchantRefNumber(merchant_ref_id);*/
        //Code to start server thread and display the progress fragment dialogue (retained)
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new ReminderProcessing(invoiceRequest, application, true));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(false);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }

    private void merchantLogo(String logo) {
        try {
            InputStream inputStream = null;
            Bitmap bitmap = null;
            //inputStream = getAssets().open("icon2.png");
            //bitmap = BitmapFactory.decodeStream(inputStream);
            String image = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.MERCHANT_LOGO);
            if (image.length() != 0) {
                bitmap = stringToBitmap(image);
                mPrinter.printImageBase(bitmap, 100, 100, Printer.Align.CENTER, 0);
            }
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void statusIcon() {
        try {
            InputStream inputStream = null;
            Bitmap icon;
            if (status == 0)
                icon = BitmapFactory.decodeResource(getResources(),R.drawable.success);
            else if (status == 1)
                icon = BitmapFactory.decodeResource(getResources(),R.drawable.txn_hold);
            else if (status == 2)
                icon = BitmapFactory.decodeResource(getResources(),R.drawable.expired);
            else
                icon = BitmapFactory.decodeResource(getResources(),R.drawable.red_cross);
            mPrinter.printImageBase(icon, 50, 50, Printer.Align.CENTER, 0);
            icon.recycle();
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void printHeaderDetails(int result) {
        String name = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.NAME);
        String branch = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.LOCATION);
        String merchantId = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
        try {
            //default content print
            //result = mPrinter.printString("Bookeey Wallet", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString(name, 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString(branch, 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString(merchantId, 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void p2mTransactionDetails(int result) {

        try {
            result = mPrinter.printString("Status : " + payToMerchantCommitRequestResponse.getG_status_description(), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Date : " + TimeUtils.getDisplayableDateWithSeconds(payToMerchantCommitRequestResponse.getG_servertime(), new Date(payToMerchantCommitRequestResponse.getServerTime())), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Transaction ID : " + payToMerchantCommitRequestResponse.getTransactionId(), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Cust Mobile No : " + payToMerchantCommitRequestResponse.getCustomerMobileNumber(), 25, Printer.Align.LEFT, false, false);
            if (payToMerchantCommitRequestResponse.getTotalPrice() != 0) {
                result = mPrinter.printString("Txn Amount : " + PriceFormatter.format(payToMerchantCommitRequestResponse.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }
            /*if (payToMerchantCommitRequestResponse.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt : " + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }*/
            result = mPrinter.printString("Total Payment : " + PriceFormatter.format(payToMerchantCommitRequestResponse.getTxnAmount(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Thank you for using", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("Bookeey Wallet", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("www.bookeey.com", 25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Smart Way2Pay", 25, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void p2mTransactionDetailsArabic(int result) {
        try {
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            if (payToMerchantCommitRequestResponse.getG_status_description().contains("Cancelled by merchant")) {
                result = mPrinter.printString(getString(R.string.status) + " : " + getString(R.string.cancelled_by_merchant), 25, Printer.Align.RIGHT, false, false);
            } else {
                result = mPrinter.printString(getString(R.string.status) + " : " + payToMerchantCommitRequestResponse.getG_status_description(), 25, Printer.Align.RIGHT, false, false);
            }
            result = mPrinter.printString(getString(R.string.date) + " : " + TimeUtils.getDisplayableDateWithSeconds(payToMerchantCommitRequestResponse.getG_servertime(), new Date(payToMerchantCommitRequestResponse.getServerTime())), 25, Printer.Align.RIGHT, false, false);
            result = mPrinter.printString(getString(R.string.transaction_id) + " : " + payToMerchantCommitRequestResponse.getTransactionId(), 25, Printer.Align.RIGHT, false, false);
            result = mPrinter.printString(getString(R.string.cust_mobile_no) + " : " + payToMerchantCommitRequestResponse.getCustomerMobileNumber(), 25, Printer.Align.RIGHT, false, false);
            if (payToMerchantCommitRequestResponse.getTotalPrice() != 0) {
                result = mPrinter.printString(getString(R.string.transaction_amount) + " : " + PriceFormatter.format(payToMerchantCommitRequestResponse.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.RIGHT, false, false);
            }
            /*if (payToMerchantCommitRequestResponse.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt : " + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }*/
            result = mPrinter.printString(getString(R.string.total_paymnet) + " : " + PriceFormatter.format(payToMerchantCommitRequestResponse.getTxnAmount(), 3, 3) + " KWD", 25, Printer.Align.RIGHT, false, false);
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
            result = mPrinter.printString(getString(R.string.thank_you_for_using_bookeey_wallet), 25, Printer.Align.CENTER, true, false);
//            result = mPrinter.printString("Bookeey Wallet", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("www.bookeey.com", 25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Smart Way2Pay", 25, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void invoiceTransactionDetailsArabic(int result) {
        try {
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            switch (tran_invoice.getPaymentStatus()) {
                case 0:
                    result = mPrinter.printString(getString(R.string.status) + " : " + "PENDING", 25, Printer.Align.RIGHT, false, false);
                    break;
                case 1:
                    result = mPrinter.printString(getString(R.string.status) + " : " + "HOLD", 25, Printer.Align.RIGHT, false, false);
                    break;
                case 2:
                    result = mPrinter.printString(getString(R.string.status) + " : " + "SUCCESS", 25, Printer.Align.RIGHT, false, false);
                    break;
                case 3:
                    result = mPrinter.printString(getString(R.string.status) + " : " + "REJECTED", 25, Printer.Align.RIGHT, false, false);
                    break;
                case 4:
                    result = mPrinter.printString(getString(R.string.status) + " : " + "FAILED", 25, Printer.Align.RIGHT, false, false);
                    break;
                case 5:
                    result = mPrinter.printString(getString(R.string.status) + " : " + "EXPIRED", 25, Printer.Align.RIGHT, false, false);
                    break;
                default:
                    break;
            }
            if (tran_invoice.getServerTime() != 0) {
                result = mPrinter.printString(getString(R.string.date) + " : " + TimeUtils.getDisplayableDateWithSeconds(tran_invoice.getG_servertime(), new Date(tran_invoice.getServerTime())), 25, Printer.Align.RIGHT, false, false);
            }
            result = mPrinter.printString(getString(R.string.transaction_id) + " : " + tran_invoice.getTransactionId(), 25, Printer.Align.RIGHT, false, false);
            if (tran_invoice.getCustomerName() != "" && tran_invoice.getCustomerName() != null) {
                result = mPrinter.printString(getString(R.string.customer_name) + " : " + tran_invoice.getCustomerName(), 25, Printer.Align.RIGHT, false, false);
            }
            if (tran_invoice.getEmailId() != "" && tran_invoice.getEmailId() != null) {
                result = mPrinter.printString(getString(R.string.email_id) + " : " + tran_invoice.getEmailId(), 25, Printer.Align.RIGHT, false, false);
            }
            if (tran_invoice.getDescription() != "" && tran_invoice.getDescription() != null) {
                result = mPrinter.printString(getString(R.string.description) + " : " + tran_invoice.getDescription(), 25, Printer.Align.RIGHT, false, false);
            }
            if (tran_invoice.getBillType() != "" && tran_invoice.getBillType() != null) {
                result = mPrinter.printString(getString(R.string.reason) + " : " + tran_invoice.getBillType(), 25, Printer.Align.RIGHT, false, false);
            }
            result = mPrinter.printString(getString(R.string.cust_mobile_no) + " : " + tran_invoice.getRecipientMobileNumber(), 25, Printer.Align.RIGHT, false, false);
            /*if (invoiceTranHistoryResponse.getOfferId() != 0) {
                result = mPrinter.printString("Offer ID : " + invoiceTranHistoryResponse.getOfferId(), 25, Printer.Align.LEFT, false, false);
            }*/
            if (tran_invoice.getTotalPrice() != 0) {
                result = mPrinter.printString(getString(R.string.total_amount) + " : " + PriceFormatter.format(tran_invoice.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.RIGHT, false, false);
            }
            /*if (invoiceTranHistoryResponse.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt : " + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }*/
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
            result = mPrinter.printString(getString(R.string.thank_you_for_using_bookeey_wallet), 25, Printer.Align.CENTER, true, false);
//            result = mPrinter.printString("Bookeey Wallet", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("www.bookeey.com", 25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Smart Way2Pay", 25, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            Log.e("Invoice Ara Print: ", "" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void invoiceTransactionDetails(int result) {
        try {
            switch (tran_invoice.getPaymentStatus()) {
                case 0:
                    result = mPrinter.printString("Status : " + "PENDING", 25, Printer.Align.LEFT, false, false);
                    break;
                case 1:
                    result = mPrinter.printString("Status : " + "HOLD", 25, Printer.Align.LEFT, false, false);
                    break;
                case 2:
                    result = mPrinter.printString("Status : " + "SUCCESS", 25, Printer.Align.LEFT, false, false);
                    break;
                case 3:
                    result = mPrinter.printString("Status : " + "REJECTED", 25, Printer.Align.LEFT, false, false);
                    break;
                case 4:
                    result = mPrinter.printString("Status : " + "FAILED", 25, Printer.Align.LEFT, false, false);
                    break;
                case 5:
                    result = mPrinter.printString("Status : " + "EXPIRED", 25, Printer.Align.LEFT, false, false);
                    break;
                default:
                    break;
            }
            if (tran_invoice.getServerTime() != 0) {
                result = mPrinter.printString("Date : " + TimeUtils.getDisplayableDateWithSeconds(tran_invoice.getG_servertime(), new Date(tran_invoice.getServerTime())), 25, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printString("Transaction ID : " + tran_invoice.getTransactionId(), 25, Printer.Align.LEFT, false, false);
            if (tran_invoice.getCustomerName() != "" && tran_invoice.getCustomerName() != null) {
                result = mPrinter.printString("Cust Name : " + tran_invoice.getCustomerName(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getEmailId() != "" && tran_invoice.getEmailId() != null) {
                result = mPrinter.printString("Email ID : " + tran_invoice.getEmailId(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getDescription() != "" && tran_invoice.getDescription() != null) {
                result = mPrinter.printString("Description : " + tran_invoice.getDescription(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getBillType() != "" && tran_invoice.getBillType() != null) {
                result = mPrinter.printString("Reason : " + tran_invoice.getBillType(), 25, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printString("Cust Mobile No : " + tran_invoice.getRecipientMobileNumber(), 25, Printer.Align.LEFT, false, false);
            /*if (invoiceTranHistoryResponse.getOfferId() != 0) {
                result = mPrinter.printString("Offer ID : " + invoiceTranHistoryResponse.getOfferId(), 25, Printer.Align.LEFT, false, false);
            }*/
            if (tran_invoice.getTotalPrice() != 0) {
                result = mPrinter.printString("Total Amount : " + PriceFormatter.format(tran_invoice.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }
            /*if (invoiceTranHistoryResponse.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt : " + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }*/
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Thank you for using", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("Bookeey Wallet", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("www.bookeey.com", 25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Smart Way2Pay", 25, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            Log.e("Invoice Eng Print: ", "" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void bookeeyLogo(String logo) {
        try {
            InputStream inputStream = null;
            Bitmap bitmap = null;
            inputStream = getAssets().open("icon2.png");
            bitmap = BitmapFactory.decodeStream(inputStream);
            mPrinter.printImageBase(bitmap, 100, 100, Printer.Align.CENTER, 0);
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private int printSDKText() {
        String name = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.NAME);
        String branch = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.LOCATION);
        String merchantId = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
        PrintManagerSDK printManager_new = new PrintManagerSDK();
        printManager_new.initPrint(getApplicationContext());
        printManager_new.feedLine(3);
        Bitmap bitmap1 = null;
        String mImage = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.MERCHANT_LOGO);
        if (mImage.length() != 0) {
            bitmap1 = stringToBitmap(mImage);
            bitmap1 = Bitmap.createScaledBitmap(bitmap1, 100, 100, true);
            byte[] imageData = getBitmapBytes(bitmap1);
            printManager_new.addImage(imageData, 0, 100, 100);
            printManager_new.feedLine(3);
        }
        printManager_new.addText(name, 2, 1);
        printManager_new.feedLine(1);
        printManager_new.addText(branch, 2, 1);
        printManager_new.feedLine(1);
        printManager_new.addText(merchantId, 2, 1);
        printManager_new.feedLine(1);
        printManager_new.addText("__________________________________________", 1, 1);
        BitmapDrawable draw;
        if (payToMerchantCommitRequestResponse.getG_status_description().equals("SUCCESS"))
            draw = (BitmapDrawable) getApplicationContext().getResources().getDrawable(R.drawable.success);
        else
            draw = (BitmapDrawable) getApplicationContext().getResources().getDrawable(R.drawable.red_cross);
        byte[] imageData = getBitmapBytes(draw.getBitmap());
        printManager_new.addImage(imageData, 0, 100, 100);
        printManager_new.feedLine(2);
        printManager_new.addTextGoodsList(getString(R.string.status) + " : ", "", payToMerchantCommitRequestResponse.getG_status_description(), 1);
        printManager_new.addTextGoodsList(getString(R.string.date) + " : ", "", TimeUtils.getDisplayableDateWithSeconds(payToMerchantCommitRequestResponse.getG_servertime(), new Date(payToMerchantCommitRequestResponse.getServerTime())), 1);
        printManager_new.addTextGoodsList(getString(R.string.transaction_id) + " : ", "", payToMerchantCommitRequestResponse.getTransactionId(), 1);
        printManager_new.addTextGoodsList(getString(R.string.cust_mobile_no) + " : ", "", payToMerchantCommitRequestResponse.getCustomerMobileNumber(), 1);
        if (payToMerchantCommitRequestResponse.getTotalPrice() != 0) {
            printManager_new.addTextGoodsList(getString(R.string.total_amount) + " : ", "", PriceFormatter.format(payToMerchantCommitRequestResponse.getTotalPrice(), 3, 3) + " KWD", 1);
        }
        printManager_new.addTextGoodsList(getString(R.string.total_paymnet) + " : ", "", PriceFormatter.format(payToMerchantCommitRequestResponse.getTxnAmount(), 3, 3) + " KWD", 1);
        printManager_new.addText("__________________________________________", 1, 1);
        printManager_new.feedLine(4);
        printManager_new.addText("Thank you for using", 2, 1);
        printManager_new.addText("Bookeey Wallet", 2, 1);
        printManager_new.feedLine(2);
        printManager_new.addText("www.bookeey.com", 1, 1);
        printManager_new.addText("Smart Way2Pay", 1, 1);
        printManager_new.feedLine(5);
        Bitmap bitmap = getLogoBitmap(getApplicationContext());
        byte[] imageData2 = getBitmapBytes(bitmap);
        printManager_new.addImage(imageData2, 0, 100, 100);
        printManager_new.feedLine(5);
        int ret = printManager_new.startPrint();
        printManager_new.close();
        return ret;
    }

    public byte[] getBitmapBytes(Bitmap bitmap) {
        byte[] imageData = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
        return imageData;
    }

    private Bitmap getLogoBitmap(Context context) {
        BitmapDrawable draw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.icon2);
        return draw.getBitmap();
    }

    private int printSDKInvoiceText() {
        String name = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.NAME);
        String branch = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.LOCATION);
        String merchantId = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
        PrintManagerSDK printManager_new = new PrintManagerSDK();
        printManager_new.initPrint(getApplicationContext());
        printManager_new.feedLine(3);
        Bitmap bitmap1 = null;
        String mImage = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.MERCHANT_LOGO);
        if (mImage.length() != 0) {
            bitmap1 = stringToBitmap(mImage);
            bitmap1 = Bitmap.createScaledBitmap(bitmap1, 100, 100, true);
            byte[] imageData = getBitmapBytes(bitmap1);
            printManager_new.addImage(imageData, 0, 100, 100);
            printManager_new.feedLine(3);
        }
        printManager_new.addText(name, 2, 1);
        printManager_new.feedLine(1);
        printManager_new.addText(branch, 2, 1);
        printManager_new.feedLine(1);
        printManager_new.addText(merchantId, 2, 1);
        printManager_new.feedLine(1);
        printManager_new.addText("__________________________________________", 1, 1);
        BitmapDrawable draw;
        if (tran_invoice.getPaymentStatus() == 2)
            draw = (BitmapDrawable) getApplicationContext().getResources().getDrawable(R.drawable.success);
        else
            draw = (BitmapDrawable) getApplicationContext().getResources().getDrawable(R.drawable.red_cross);
        byte[] imageData = getBitmapBytes(draw.getBitmap());
        printManager_new.addImage(imageData, 0, 100, 100);
        printManager_new.feedLine(2);
        switch (tran_invoice.getPaymentStatus()) {
            case 0:
                printManager_new.addTextGoodsList(getString(R.string.status) + " : ", "", "PENDING", 1);
                break;
            case 1:
                printManager_new.addTextGoodsList(getString(R.string.status) + " : ", "", "HOLD", 1);
                break;
            case 2:
                printManager_new.addTextGoodsList(getString(R.string.status) + " : ", "", "SUCCESS", 1);
                break;
            case 3:
                printManager_new.addTextGoodsList(getString(R.string.status) + " : ", "", "REJECTED", 1);
                break;
            case 4:
                printManager_new.addTextGoodsList(getString(R.string.status) + " : ", "", "FAILED", 1);
                break;
            default:
                break;
        }
        if (tran_invoice.getServerTime() != 0) {
            printManager_new.addTextGoodsList(getString(R.string.date) + " : ", "", TimeUtils.getDisplayableDateWithSeconds(tran_invoice.getG_servertime(), new Date(tran_invoice.getServerTime())), 1);
        }
        printManager_new.addTextGoodsList(getString(R.string.transaction_id) + " : ", "", tran_invoice.getTransactionId(), 1);
        if (tran_invoice.getCustomerName() != "" && tran_invoice.getCustomerName() != null) {
            printManager_new.addTextGoodsList(getString(R.string.customer_name) + " : ", "", tran_invoice.getCustomerName(), 1);
        }
        if (tran_invoice.getEmailId() != "" && tran_invoice.getEmailId() != null) {
            printManager_new.addTextGoodsList(getString(R.string.email_id) + " : ", "", tran_invoice.getEmailId(), 1);
        }
        if (tran_invoice.getDescription() != "" && tran_invoice.getDescription() != null) {
            printManager_new.addTextGoodsList(getString(R.string.description) + " : ", "", tran_invoice.getDescription(), 1);
        }
        if (tran_invoice.getBillType() != "" && tran_invoice.getBillType() != null) {
            printManager_new.addTextGoodsList(getString(R.string.reason) + " : ", "", tran_invoice.getBillType(), 1);
        }
        printManager_new.addTextGoodsList(getString(R.string.cust_mobile_no) + " : ", "", tran_invoice.getRecipientMobileNumber(), 1);
        if (tran_invoice.getTotalPrice() != 0) {
            printManager_new.addTextGoodsList(getString(R.string.total_amount) + " : ", "", PriceFormatter.format(tran_invoice.getTotalPrice(), 3, 3) + " KWD", 1);
        }
        printManager_new.addText("__________________________________________", 1, 1);
        printManager_new.feedLine(4);
        printManager_new.addText("Thank you for using", 2, 1);
        printManager_new.addText("Bookeey Wallet", 2, 1);
        printManager_new.feedLine(2);
        printManager_new.addText("www.bookeey.com", 1, 1);
        printManager_new.addText("Smart Way2Pay", 1, 1);
        printManager_new.feedLine(5);
        Bitmap bitmap = getLogoBitmap(getApplicationContext());
        byte[] imageData2 = getBitmapBytes(bitmap);
        printManager_new.addImage(imageData2, 0, 100, 100);
        printManager_new.feedLine(5);
        int ret = printManager_new.startPrint();
        printManager_new.close();
        return ret;
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            pd.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                // Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                bmImage.setImageBitmap(result);
            }
        }
    }

    private class PrintThread extends Thread {
        @Override
        public void run() {
            bthreadrunning = true;
            int result = 0;
            do {
                try {
                    result = mPrinter.printInit();
                    //clear print cache
                    mPrinter.clearPrintDataCache();
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                try {
                    merchantLogo("logo");
                    printHeaderDetails(result);
                    statusIcon();
                    if (typeOfResponse.equalsIgnoreCase("PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE")) {
                        if (selectedLanguage.equalsIgnoreCase("ar")) {
                            p2mTransactionDetailsArabic(result);
                        } else {
                            p2mTransactionDetails(result);
                        }
                    } else {
                        if (selectedLanguage.equalsIgnoreCase("ar")) {
                            invoiceTransactionDetailsArabic(result);
                        } else {
                            invoiceTransactionDetails(result);
                        }
                    }
                    //To print the logo
                    bookeeyLogo("logo");
                    //print end reserve height
                    result = mPrinter.printPaper(100);
                    //Detecting the in-place status of the card during printing
                    //mPrinter.printPaper_trade(5,100);
                    result = mPrinter.printFinish();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } while (bloop);
            bthreadrunning = false;
        }
    }
}
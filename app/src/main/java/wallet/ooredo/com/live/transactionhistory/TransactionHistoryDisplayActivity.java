package wallet.ooredo.com.live.transactionhistory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

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
import wallet.ooredo.com.live.invoice.InvoiceActivity;
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
            payment_confirm_due_amount_to_merchant_row;
    String typeOfResponse;
    String time_zone_str = "";
    private ImageView ypcm_close, payment_success_img_id;
    private TextView payment_confirm_status, payment_confirm_txn_id, payment_confirm_date_id, payment_confirm_total_payment_id, payment_confirm_wallet_number_id,
            payment_confirm_merchant_balance, payment_confirm_credited_amount,
            payment_confirm_emaild_id, payment_confirm_reason_id,
            payment_confirm_total_amt_value,
            payment_confirm_discount_amt_value, payment_confirm_offerID_value,
            payment_confirm_invoice_link_text,
            payment_confirm_med_no_text,
            payment_confirm_civilID_text,
            payment_confirm_nurseID_text,
            payment_confirm_accNo_text,
            payment_confirm_fee_value,
            payment_confirm_payment_method_value,
            payment_confirm_due_amount_to_merchant_value;
    private Button invoice_edit_btn, invoice_pending_edit_btn, invoice_share_btn, txn_history_print_btn, invoice_expire_btn;
    private Printer mPrinter;
    private boolean bloop = false;
    private boolean bthreadrunning = false;
    private PayToMerchantCommitRequestResponse payToMerchantCommitRequestResponse = null;
    private InvoiceTranHistoryResponsePojo tran_invoice = null;
    private EditText payment_confirm_fullname_id, payment_confirm_desc_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        payment_confirm_credited_amount = (TextView) findViewById(R.id.payment_confirm_credited_amount);
        payment_after_bal_tr = (TableRow) findViewById(R.id.payment_after_bal_tr);
        invoice_edit_btn = (Button) findViewById(R.id.invoice_edit_btn);
        invoice_share_btn = (Button) findViewById(R.id.invoice_share_btn);
        invoice_expire_btn = (Button) findViewById(R.id.invoice_expire_btn);
        invoice_pending_edit_btn = (Button) findViewById(R.id.invoice_pending_edit_btn);
        payment_confirm_fullname_row = (TableRow) findViewById(R.id.payment_confirm_fullname_row);
        payment_confirm_emailid_row = (TableRow) findViewById(R.id.payment_confirm_emailid_row);
        payment_confirm_desc_row = (TableRow) findViewById(R.id.payment_confirm_desc_row);
        payment_confirm_reason_row = (TableRow) findViewById(R.id.payment_confirm_reason_row);
        payment_confirm_fullname_id = (EditText) findViewById(R.id.payment_confirm_fullname_id);
        payment_confirm_emaild_id = (TextView) findViewById(R.id.payment_confirm_emaild_id);
        payment_confirm_desc_id = (EditText) findViewById(R.id.payment_confirm_desc_id);
        payment_confirm_reason_id = (TextView) findViewById(R.id.payment_confirm_reason_id);
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
                    Toast.makeText(TransactionHistoryDisplayActivity.this, "Link Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
                //For Newer Versions
                else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Message", payment_confirm_invoice_link_text.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(TransactionHistoryDisplayActivity.this, "Link Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        if (null != type) {
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
        }
        ypcm_close = (ImageView) findViewById(R.id.payment_success_img_id);
        ypcm_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        Print
        txn_history_print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] status = new int[1];
                int ret = -1;
                try {
                    ret = mPrinter.getPrinterStatus(status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //GETPrinterstatus(int[] status) 状态值	N	HEX1
                // 00 打印机正常
                // 0x01：参数错误
                // 0x06：不可执行
                // 0x8A：缺纸,
                // 0x8B：过热
                if (ret == 0) {
                    //tvshow.setText("Printer status: " + status[0]);
                    startPrint();
                } else {
                    //tvshow.setText("Fail");
                    Toast.makeText(getApplicationContext(), "Printer not available on device..!" + ret, Toast.LENGTH_SHORT).show();
//                    startPrint();
                    // Get a PrintManager instance
//                    PrintManager printManager = (PrintManager) TransactionHistoryDisplayActivity.this
//                            .getSystemService(Context.PRINT_SERVICE);
//
//                    // Set job name, which will be displayed in the print queue
//                    String jobName = TransactionHistoryDisplayActivity.this.getString(R.string.app_name) + " Document";
//
//                    // Start a print job, passing in a PrintDocumentAdapter implementation
//                    // to handle the generation of a print document
//                    printManager.print(jobName, new MyPrintDocumentAdapter(getActivity()),
//                            null);
//                    startPrintPax();
                }
            }
        });
    }

    private void startPrint() {
        bloop = false;
        if (bthreadrunning == false)
            new PrintThread().start();
    }

    private void startPrintPax() {
        new Thread(new Runnable() {
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bookeey_medium_with_space, options);
                Log.i("Test", "width:" + bitmap.getWidth() + "height:" + bitmap.getHeight());


                /*
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().fontSet((EFontTypeAscii) asciiSpinner.getSelectedItem(),
                        (EFontTypeExtCode) extCodeSpinner.getSelectedItem());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bookeey_medium_with_space, options);
//                        Log.i("Test", "width:"+bitmap.getWidth()+"height:"+bitmap.getHeight());
                if (containBitmap) {
                    PrinterTester.getInstance().printBitmap(bitmap);
                }
                PrinterTester.getInstance().spaceSet(Byte.parseByte(wordSpaceEt.getText().toString()),
                        Byte.parseByte(lineSpaceEt.getText().toString()));
                PrinterTester.getInstance().leftIndents(Short.parseShort(leftIndentEt.getText().toString()));
                PrinterTester.getInstance().setGray(Integer.parseInt(setGrayEt.getText().toString()));
                if (doubleWidth) {
                    PrinterTester.getInstance().setDoubleWidth(doubleWidth, doubleWidth);
                }
                if (doubleHeight) {
                    PrinterTester.getInstance().setDoubleHeight(doubleHeight, doubleHeight);
                }
                PrinterTester.getInstance().setInvert(invert);
                String str = printStrEt.getText().toString();
                if (str != null && str.length() > 0)
                    PrinterTester.getInstance().printStr(str, null);
                PrinterTester.getInstance().step(Integer.parseInt(stepEt.getText().toString().trim()));

                getDotLineTv.post(new Runnable() {
                    public void run() {
                        getDotLineTv.setText(PrinterTester.getInstance().getDotLine() + "");
                    }
                });


                final String status = PrinterTester.getInstance().start();

//                statusTv.post(new Runnable() {
//                    public void run() {
//                        statusTv.setText(status);
//                    }
//                });

                 */
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());
//        MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
//        new DownloadImageTask(back_logo).execute(merchantLoginRequestResponse.getMerchantLogo());
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
        switch (type) {
            case PAY_TO_MERCHANT_RESPONSE:
                PayToMerchantRequestResponse payToMerchantRequestResponse = (PayToMerchantRequestResponse) transTypeInterface;
                if (payToMerchantRequestResponse.getG_status_description().contains("Cancelled by merchant")) {
                    payment_confirm_status.setText("" + getString(R.string.cancelled_by_merchant));
                } else {
                    payment_confirm_status.setText(payToMerchantRequestResponse.getG_status_description());
                }
                payment_confirm_txn_id.setText("" + payToMerchantRequestResponse.getTransactionId());
                payment_confirm_date_id.setText("" + TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(payToMerchantRequestResponse.getServerTime())));
                payment_confirm_wallet_number_id.setText(payToMerchantRequestResponse.getCustomerMobileNumber());
                payment_confirm_total_payment_id.setText(getString(R.string.total_paymnet) + "    KWD " + PriceFormatter.format(payToMerchantRequestResponse.getTxnAmount(), 3, 3));
                payment_confirm_merchant_balance.setText(PriceFormatter.format(payToMerchantRequestResponse.getMer_balance(), 3, 3));
                switch (payToMerchantRequestResponse.getG_status()) {
                    case 1:
                        payment_success_img_id.setBackgroundResource(R.drawable.success);
                        break;
                    case -1:
                        payment_success_img_id.setBackgroundResource(R.drawable.red_cross);
                        break;
                }
                break;
            case PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE:
                if (((CoreApplication) getApplication()).isPOS()) {
                    txn_history_print_btn.setVisibility(View.VISIBLE);
                }
                payToMerchantCommitRequestResponse = (PayToMerchantCommitRequestResponse) transTypeInterface;
                //June 23 2020 START
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
//                    switch (tran_invoice.getPaymentStatus()) {
//                        case 0:
//                            payment_success_img_id.setBackgroundResource(R.drawable.txn_hold);
//
//                            break;
//                        case 1:
//
//
//                            payment_confirm_status.setText("HOLD");
//                            break;
//                        case 2:
//
//                            payment_confirm_status.setText("SUCCESS");
//
//
//                            break;
//
//                        case 3:
//
//
//                            payment_confirm_status.setText("REJECTED");
//
//                            break;
//                        case 4:
//
//                            payment_confirm_status.setText("FAILED");
//
//                            break;
//                        default:
//                            break;
//                    }
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
                        break;
                    case -1:
                        payment_success_img_id.setBackgroundResource(R.drawable.red_cross);
                        break;
                }
                break;
            case INVOICE_TRAN_RESPONSE:
                if (((CoreApplication) getApplication()).isPOS()) {
                    txn_history_print_btn.setVisibility(View.VISIBLE);
                }
                tran_invoice = (InvoiceTranHistoryResponsePojo) transTypeInterface;
//                Toast.makeText(TransactionHistoryDisplayActivity.this,""+tran_invoice.getG_servertime(),Toast.LENGTH_LONG).show();
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
                if (tran_invoice.getEmailId() != "" && tran_invoice.getEmailId() != null) {
                    payment_confirm_emailid_row.setVisibility(View.VISIBLE);
                    payment_confirm_emaild_id.setText(tran_invoice.getEmailId().trim());
                } else {
                    payment_confirm_emailid_row.setVisibility(View.GONE);
                }
                //reject
                if (tran_invoice.getDescription() != "" && tran_invoice.getDescription() != null) {
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
                if (tran_invoice.getOfferDescription() != null && tran_invoice.getOfferDescription() != "") {
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
                switch (tran_invoice.getPaymentStatus()) {
                    case 0:
                        payment_success_img_id.setBackgroundResource(R.drawable.txn_hold);
//                        payment_confirm_status.setText(getString(R.string.pending));
                        payment_confirm_status.setText("PENDING");
                        invoice_edit_btn.setVisibility(View.VISIBLE);
                        invoice_pending_edit_btn.setVisibility(View.VISIBLE);
                        invoice_share_btn.setVisibility(View.VISIBLE);
                        invoice_expire_btn.setVisibility(View.VISIBLE);
                        //for remind the invoice
                        invoice_edit_btn.setText(getString(R.string.reminder));
                        invoice_edit_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reminderRequst(tran_invoice.getTransactionId(), tran_invoice.getPaymentRefId());
                            }
                        });
                        //for edit the invoice
                        invoice_pending_edit_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String json = new Gson().toJson(tran_invoice);
                                Intent intent = new Intent(getBaseContext(), InvoiceActivity.class);
                                intent.putExtra("INVOICE_EDIT_DETAILS", json);
                                intent.putExtra("TXN_PENDING_EDIT", true);
                                startActivity(intent);
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
                        payment_success_img_id.setBackgroundResource(R.drawable.mer_fail);
//                        payment_confirm_status.setText(R.string.hold);
                        payment_confirm_status.setText("HOLD");
                        break;
                    case 2:
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
                        payment_success_img_id.setBackgroundResource(R.drawable.red_cross);
//                        payment_confirm_status.setText(getString(R.string.rejected));
                        payment_confirm_status.setText("REJECTED");
//                        payment_confirm_status.setText("FAILED");
                        invoice_edit_btn.setVisibility(View.VISIBLE);
                        invoice_edit_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String json = new Gson().toJson(tran_invoice);
                                Intent intent = new Intent(getBaseContext(), InvoiceActivity.class);
                                intent.putExtra("INVOICE_EDIT_DETAILS", json);
                                intent.putExtra("TXN_PENDING_EDIT", true);
                                startActivity(intent);
                            }
                        });
                        break;
                    case 4:
                        payment_success_img_id.setBackgroundResource(R.drawable.red_cross);
//                        payment_confirm_status.setText(getString(R.string.failed));
                        payment_confirm_status.setText("FAILED");
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
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//
//        Intent intent = new Intent(getBaseContext(), TransactionHistoryActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }

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

    private void p2mTransactionDetails(int result) {
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
            result = mPrinter.printString("Status:" + payToMerchantCommitRequestResponse.getG_status_description(), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Date:" + TimeUtils.getDisplayableDateWithSeconds(payToMerchantCommitRequestResponse.getG_servertime(), new Date(payToMerchantCommitRequestResponse.getServerTime())), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Transaction ID:" + payToMerchantCommitRequestResponse.getTransactionId(), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Cust Mobile No:" + payToMerchantCommitRequestResponse.getCustomerMobileNumber(), 25, Printer.Align.LEFT, false, false);
            if (payToMerchantCommitRequestResponse.getTotalPrice() != 0) {
                result = mPrinter.printString("Txn Amount:" + PriceFormatter.format(payToMerchantCommitRequestResponse.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }
            /*if (payToMerchantCommitRequestResponse.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt:" + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }*/
            result = mPrinter.printString("Total Payment:" + PriceFormatter.format(payToMerchantCommitRequestResponse.getTxnAmount(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
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
            if (payToMerchantCommitRequestResponse.getG_status_description().contains("Cancelled by merchant")) {
                result = mPrinter.printString(getString(R.string.status) + ": " + getString(R.string.cancelled_by_merchant), 25, Printer.Align.LEFT, false, false);
            } else {
                result = mPrinter.printString(getString(R.string.status) + ": " + payToMerchantCommitRequestResponse.getG_status_description(), 25, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printString(getString(R.string.date) + ": " + TimeUtils.getDisplayableDateWithSeconds(payToMerchantCommitRequestResponse.getG_servertime(), new Date(payToMerchantCommitRequestResponse.getServerTime())), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString(getString(R.string.transaction_id) + ": " + payToMerchantCommitRequestResponse.getTransactionId(), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString(getString(R.string.cust_mobile_no) + ": " + payToMerchantCommitRequestResponse.getCustomerMobileNumber(), 25, Printer.Align.LEFT, false, false);
            if (payToMerchantCommitRequestResponse.getTotalPrice() != 0) {
                result = mPrinter.printString(getString(R.string.transaction_amount) + ": " + PriceFormatter.format(payToMerchantCommitRequestResponse.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }
            /*if (payToMerchantCommitRequestResponse.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt:" + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }*/
            result = mPrinter.printString(getString(R.string.total_paymnet) + ":" + PriceFormatter.format(payToMerchantCommitRequestResponse.getTxnAmount(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
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
            switch (tran_invoice.getPaymentStatus()) {
                case 0:
                    result = mPrinter.printString(getString(R.string.status) + ": " + "PENDING", 25, Printer.Align.LEFT, false, false);
                    break;
                case 1:
                    result = mPrinter.printString(getString(R.string.status) + ": " + "HOLD", 25, Printer.Align.LEFT, false, false);
                    break;
                case 2:
                    result = mPrinter.printString(getString(R.string.status) + ": " + "SUCCESS", 25, Printer.Align.LEFT, false, false);
                    break;
                case 3:
                    result = mPrinter.printString(getString(R.string.status) + ": " + "REJECTED", 25, Printer.Align.LEFT, false, false);
                    break;
                case 4:
                    result = mPrinter.printString(getString(R.string.status) + ": " + "FAILED", 25, Printer.Align.LEFT, false, false);
                    break;
                default:
                    break;
            }
            if (tran_invoice.getServerTime() != 0) {
                result = mPrinter.printString(getString(R.string.date) + ": " + TimeUtils.getDisplayableDateWithSeconds(tran_invoice.getG_servertime(), new Date(tran_invoice.getServerTime())), 25, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printString(getString(R.string.transaction_id) + ": " + tran_invoice.getTransactionId(), 25, Printer.Align.LEFT, false, false);
            if (tran_invoice.getCustomerName() != "" && tran_invoice.getCustomerName() != null) {
                result = mPrinter.printString(getString(R.string.customer_name) + ": " + tran_invoice.getCustomerName(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getEmailId() != "" && tran_invoice.getEmailId() != null) {
                result = mPrinter.printString(getString(R.string.email_id) + ": " + tran_invoice.getEmailId(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getDescription() != "" && tran_invoice.getDescription() != null) {
                result = mPrinter.printString(getString(R.string.description) + ": " + tran_invoice.getDescription(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getBillType() != "" && tran_invoice.getBillType() != null) {
                result = mPrinter.printString(getString(R.string.reason) + ": " + tran_invoice.getBillType(), 25, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printString(getString(R.string.cust_mobile_no) + ": " + tran_invoice.getRecipientMobileNumber(), 25, Printer.Align.LEFT, false, false);
            /*if (invoiceTranHistoryResponse.getOfferId() != 0) {
                result = mPrinter.printString("Offer ID:" + invoiceTranHistoryResponse.getOfferId(), 25, Printer.Align.LEFT, false, false);
            }*/
            if (tran_invoice.getTotalPrice() != 0) {
                result = mPrinter.printString(getString(R.string.total_amount) + ": " + PriceFormatter.format(tran_invoice.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }
            /*if (invoiceTranHistoryResponse.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt:" + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
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
            switch (tran_invoice.getPaymentStatus()) {
                case 0:
                    result = mPrinter.printString("Status:" + "PENDING", 25, Printer.Align.LEFT, false, false);
                    break;
                case 1:
                    result = mPrinter.printString("Status:" + "HOLD", 25, Printer.Align.LEFT, false, false);
                    break;
                case 2:
                    result = mPrinter.printString("Status:" + "SUCCESS", 25, Printer.Align.LEFT, false, false);
                    break;
                case 3:
                    result = mPrinter.printString("Status:" + "REJECTED", 25, Printer.Align.LEFT, false, false);
                    break;
                case 4:
                    result = mPrinter.printString("Status:" + "FAILED", 25, Printer.Align.LEFT, false, false);
                    break;
                default:
                    break;
            }
            if (tran_invoice.getServerTime() != 0) {
                result = mPrinter.printString("Date:" + TimeUtils.getDisplayableDateWithSeconds(tran_invoice.getG_servertime(), new Date(tran_invoice.getServerTime())), 25, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printString("Transaction ID:" + tran_invoice.getTransactionId(), 25, Printer.Align.LEFT, false, false);
            if (tran_invoice.getCustomerName() != "" && tran_invoice.getCustomerName() != null) {
                result = mPrinter.printString("Cust Name:" + tran_invoice.getCustomerName(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getEmailId() != "" && tran_invoice.getEmailId() != null) {
                result = mPrinter.printString("Email ID:" + tran_invoice.getEmailId(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getDescription() != "" && tran_invoice.getDescription() != null) {
                result = mPrinter.printString("Description:" + tran_invoice.getDescription(), 25, Printer.Align.LEFT, false, false);
            }
            if (tran_invoice.getBillType() != "" && tran_invoice.getBillType() != null) {
                result = mPrinter.printString("Reason:" + tran_invoice.getBillType(), 25, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printString("Cust Mobile No:" + tran_invoice.getRecipientMobileNumber(), 25, Printer.Align.LEFT, false, false);
            /*if (invoiceTranHistoryResponse.getOfferId() != 0) {
                result = mPrinter.printString("Offer ID:" + invoiceTranHistoryResponse.getOfferId(), 25, Printer.Align.LEFT, false, false);
            }*/
            if (tran_invoice.getTotalPrice() != 0) {
                result = mPrinter.printString("Total Amount:" + PriceFormatter.format(tran_invoice.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }
            /*if (invoiceTranHistoryResponse.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt:" + PriceFormatter.format(payToMerchantCommitRequestResponse.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
//            pd.dismiss();
            bmImage.setImageBitmap(result);
        }
    }

    private class PrintThread extends Thread {
        @Override
        public void run() {
            bthreadrunning = true;
            int datalen = 0;
            int result = 0;
            byte[] senddata = null;
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
                    //To print the logo
                    merchantLogo("logo");
                    //To print thr text
                    String selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                    if (typeOfResponse.equalsIgnoreCase("PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE")) {
                        if (selectedLanguage.equalsIgnoreCase("en")) {
                            p2mTransactionDetails(result);
                        } else if (selectedLanguage.equalsIgnoreCase("ar")) {
                            p2mTransactionDetailsArabic(result);
                        } else {
                            p2mTransactionDetails(result);
                        }
                    } else {
                        if (selectedLanguage.equalsIgnoreCase("en")) {
                            invoiceTransactionDetails(result);
                        } else if (selectedLanguage.equalsIgnoreCase("ar")) {
                            invoiceTransactionDetailsArabic(result);
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







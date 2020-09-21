package wallet.ooredo.com.live.invoice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;

import java.util.Date;

import coreframework.database.CustomSharedPreferences;
import coreframework.taskframework.GenericActivity;
import coreframework.utils.PriceFormatter;
import coreframework.utils.TimeUtils;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import wallet.ooredo.com.live.transactionhistory.TransactionHistoryDisplayActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceRequest;

/**
 * Created by 10037 on 21-Nov-17.
 */

public class InvoiceSuccess extends GenericActivity implements View.OnClickListener {

    private String response = null;
    private InvoiceRequest response_obj = null;
    TextView invoice_success_status_text, invoice_success_invoice_no_text,
            invoice_success_mobile_text, invoice_success_amount_text,
            invoice_success_invoice_link_text,
            payment_confirm_med_no_text,
            payment_confirm_civilID_text,
            payment_confirm_nurseID_text,
            payment_confirm_accNo_text,
            invoice_success_customer_name_text;
    Button ypc_close;
    ImageView invoice_success_img_id;
    TableRow invoice_success_link_tr,
            payment_confirm_med_no_tr,
            payment_confirm_civilID_tr,
            payment_confirm_nurseID_tr,
            payment_confirm_accNo_tr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_success);
        ypc_close = (Button) findViewById(R.id.ypc_close);
        ypc_close.setOnClickListener(this);
        invoice_success_status_text = (TextView) findViewById(R.id.invoice_success_status_text);
        invoice_success_invoice_no_text = (TextView) findViewById(R.id.invoice_success_invoice_no_text);
        invoice_success_mobile_text = (TextView) findViewById(R.id.invoice_success_mobile_text);
        invoice_success_amount_text = (TextView) findViewById(R.id.invoice_success_amount_text);
        invoice_success_img_id = (ImageView) findViewById(R.id.invoice_success_img_id);
        //This is the place where the response from leg1 is received as intent data
        //start

        //newly added for invoice link
        invoice_success_link_tr = (TableRow) findViewById(R.id.invoice_success_link_tr);
        invoice_success_invoice_link_text = (TextView) findViewById(R.id.invoice_success_invoice_link_text);
        //invoice_success_invoice_link_text.setTextIsSelectable(true);


        //for hospital merchant newly added
        payment_confirm_med_no_tr = (TableRow) findViewById(R.id.payment_confirm_med_no_tr);
        payment_confirm_civilID_tr = (TableRow) findViewById(R.id.payment_confirm_civilID_tr);
        payment_confirm_nurseID_tr = (TableRow) findViewById(R.id.payment_confirm_nurseID_tr);
        payment_confirm_accNo_tr = (TableRow) findViewById(R.id.payment_confirm_accNo_tr);

        payment_confirm_med_no_text = (TextView) findViewById(R.id.payment_confirm_med_no_text);
        payment_confirm_civilID_text = (TextView) findViewById(R.id.payment_confirm_civilID_text);
        payment_confirm_nurseID_text = (TextView) findViewById(R.id.payment_confirm_nurseID_text);
        payment_confirm_accNo_text = (TextView) findViewById(R.id.payment_confirm_accNo_text);

        invoice_success_customer_name_text = (TextView) findViewById(R.id.invoice_success_customer_name_text);

        final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.payment_confirm_body_id);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_LEFT);
            }
        });
        invoice_success_invoice_link_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the Operation System SDK version as an int
                int sdkVer = android.os.Build.VERSION.SDK_INT;
                //For Older Android SDK versions
                if (sdkVer < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    @SuppressWarnings("deprecation")
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(invoice_success_invoice_link_text.getText().toString());


//                    Toast.makeText(InvoiceSuccess.this, "Link Copied to Clipboard", Toast.LENGTH_SHORT).show();


                    Toast.makeText(InvoiceSuccess.this, getString(R.string.link_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                }
                //For Newer Versions
                else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Message", invoice_success_invoice_link_text.getText().toString());
                    clipboard.setPrimaryClip(clip);


//                    Toast.makeText(InvoiceSuccess.this, "Link Copied to Clipboard", Toast.LENGTH_SHORT).show();

                    Toast.makeText(InvoiceSuccess.this, getString(R.string.link_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                }
            }
        });

        response = getIntent().getExtras().getString("request_data");
        try {
            if (!response.isEmpty()) {
                response_obj = new Gson().fromJson(response, InvoiceRequest.class);
                invoice_success_status_text.setText("--PENDING--");
                invoice_success_invoice_no_text.setText(response_obj.getInvoiceNo());
                invoice_success_mobile_text.setText(response_obj.getMobileNo());
                invoice_success_customer_name_text.setText(response_obj.getCustName());

                invoice_success_amount_text.setText(String.valueOf(response_obj.getAmount()));
                /*if (response_obj.getInvoiceLink() != null && !response_obj.getInvoiceLink().isEmpty()) {
                    invoice_success_link_tr.setVisibility(View.VISIBLE);
                    invoice_success_invoice_link_text.setText(response_obj.getInvoiceLink());
                }*/

                //merchant hospital newly added
                if (response_obj.getMedFileNo() != "" && response_obj.getMedFileNo() != null) {
                    payment_confirm_med_no_tr.setVisibility(View.VISIBLE);
                    payment_confirm_med_no_text.setText(response_obj.getMedFileNo());
                } else {
                    payment_confirm_med_no_tr.setVisibility(View.GONE);
                }

                if (response_obj.getAccountNo() != "" && response_obj.getAccountNo() != null) {
                    payment_confirm_accNo_tr.setVisibility(View.VISIBLE);
                    payment_confirm_accNo_text.setText(response_obj.getAccountNo());
                } else {
                    payment_confirm_accNo_tr.setVisibility(View.GONE);
                }
                if (response_obj.getCivilId() != "" && response_obj.getCivilId() != null) {
                    payment_confirm_civilID_tr.setVisibility(View.VISIBLE);
                    payment_confirm_civilID_text.setText(response_obj.getCivilId());
                } else {
                    payment_confirm_civilID_tr.setVisibility(View.GONE);
                }
                if (response_obj.getNurseId() != "" && response_obj.getNurseId() != null) {
                    payment_confirm_nurseID_tr.setVisibility(View.VISIBLE);
                    payment_confirm_nurseID_text.setText(response_obj.getNurseId());
                } else {
                    payment_confirm_nurseID_tr.setVisibility(View.GONE);
                }

                String status = "--PENDING--";
                switch (status) {
                    case "--SUCCESS--":
//                        invoice_success_img_id.setImageResource(R.drawable.success);
                        invoice_success_img_id.setImageResource(R.drawable.invoice_success);
                        break;
                    case "--PENDING--":
//                        invoice_success_img_id.setImageResource(R.drawable.success);
                        invoice_success_img_id.setImageResource(R.drawable.invoice_success);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Button ypc_share = (Button)findViewById(R.id.ypc_share);

        ypc_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String merchant_name = CustomSharedPreferences.getStringData((CoreApplication) getApplication(), CustomSharedPreferences.SP_KEY.NAME);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Invoice");

                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Dear customer,\nYou have an invoice from " + merchant_name + "." + " \nClick on the below link to view and pay\n\nStatus: " + "Pending" +"\n\nAmount: " + String.valueOf(response_obj.getAmount())+"\n\nInvoice URL: " + response_obj.getInvoiceLink()
                );
                sendIntent.setType("text/plain");

                startActivity(Intent.createChooser(sendIntent, "Choose any!"));


            }
        });



        //end
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Facebook
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("MX_Invoice_Final");

       String selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(InvoiceSuccess.this, selectedLanguage);
        }


        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(InvoiceSuccess.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

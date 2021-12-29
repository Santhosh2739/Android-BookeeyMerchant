package wallet.ooredo.com.live.invoice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;

import coreframework.database.CustomSharedPreferences;
import coreframework.taskframework.GenericActivity;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceRequest;
/**
 * Created by 10037 on 21-Nov-17.
 */
public class InvoiceSuccess extends GenericActivity implements View.OnClickListener {
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
    String whatsAppNo = null;
    boolean isSuccess = false;
    private String response, sendLink = null;
    private InvoiceRequest response_obj = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if(selectedLanguage.equals("ar"))
            setContentView(R.layout.invoice_success_right);
        else
            setContentView(R.layout.invoice_success);
        ypc_close = findViewById(R.id.ypc_close);
        ypc_close.setOnClickListener(this);
        invoice_success_status_text = findViewById(R.id.invoice_success_status_text);
        invoice_success_invoice_no_text = findViewById(R.id.invoice_success_invoice_no_text);
        invoice_success_mobile_text = findViewById(R.id.invoice_success_mobile_text);
        invoice_success_amount_text = findViewById(R.id.invoice_success_amount_text);
        invoice_success_img_id = findViewById(R.id.invoice_success_img_id);
        //This is the place where the response from leg1 is received as intent data
        //start
        //newly added for invoice link
        invoice_success_link_tr = findViewById(R.id.invoice_success_link_tr);
        invoice_success_invoice_link_text = findViewById(R.id.invoice_success_invoice_link_text);
        //invoice_success_invoice_link_text.setTextIsSelectable(true);
        //for hospital merchant newly added
        payment_confirm_med_no_tr = findViewById(R.id.payment_confirm_med_no_tr);
        payment_confirm_civilID_tr = findViewById(R.id.payment_confirm_civilID_tr);
        payment_confirm_nurseID_tr = findViewById(R.id.payment_confirm_nurseID_tr);
        payment_confirm_accNo_tr = findViewById(R.id.payment_confirm_accNo_tr);
        payment_confirm_med_no_text = findViewById(R.id.payment_confirm_med_no_text);
        payment_confirm_civilID_text = findViewById(R.id.payment_confirm_civilID_text);
        payment_confirm_nurseID_text = findViewById(R.id.payment_confirm_nurseID_text);
        payment_confirm_accNo_text = findViewById(R.id.payment_confirm_accNo_text);
        invoice_success_customer_name_text = findViewById(R.id.invoice_success_customer_name_text);
        final HorizontalScrollView scrollView = findViewById(R.id.payment_confirm_body_id);
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
        whatsAppNo = getIntent().getExtras().getString("whatsAppNo");
        try {
            if (!response.isEmpty()) {
                response_obj = new Gson().fromJson(response, InvoiceRequest.class);
                Log.e("response_obj", response_obj.toString());
                isSuccess = true;
                String cName = response_obj.getCustName() != null && !response_obj.getCustName().equals("") ? response_obj.getCustName() : "Customer";
                String lang = response_obj.getLanguage() != null ? response_obj.getLanguage() : "English";
                String merchant_name = CustomSharedPreferences.getStringData(getApplication(), CustomSharedPreferences.SP_KEY.NAME);
                boolean isWhatsapp = response_obj.isSMSSent();
                if (lang.equalsIgnoreCase("English")) {
                    sendLink = "Dear " + cName + ",\nYou have an invoice from " + merchant_name + "." + " \nClick on the below link to view and pay\n\nStatus: " + "Pending" + "\n\nAmount: " + response_obj.getAmount() + "\n\nInvoice URL: " + response_obj.getInvoiceLink();
                } else {
                    cName = cName.equals("Customer") ? "عميلنا" : cName;
                    sendLink = cName + " العزيز،\n" +
                            "لديك فاتورة من " + merchant_name + ".\n" +
                            "إضغط على الرابط أدناه للاطلاع على الفاتورة والدفع\n" +
                            "\n" +
                            "الحالة: قيد الانتظار\n" +
                            "\n" +
                            "قيمة الفاتورة: " + response_obj.getAmount() + "\n" +
                            "\n" +
                            "رابط الدفع: " + response_obj.getInvoiceLink();
                }
                Button ypc_share = findViewById(R.id.ypc_share);
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
                    case "--PENDING--":
//                        invoice_success_img_id.setImageResource(R.drawable.success);
                        invoice_success_img_id.setImageResource(R.drawable.invoice_success);
                        break;
                }
                if (!isWhatsapp && !whatsAppNo.equals("")) {
                    openWhatsApp();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button ypc_share = findViewById(R.id.ypc_share);
        ypc_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Invoice");
                sendIntent.putExtra(Intent.EXTRA_TEXT, sendLink
                );
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Choose any!"));
            }
        });
        //end
    }

    private void alertDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custom_alert_whatsapp, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InvoiceSuccess.this);
        alertDialog.setView(promptsView);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", (dialog, which) -> openWhatsApp());
        alertDialog.show();
    }

    public void openWhatsApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + whatsAppNo + "&text=" + sendLink));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("MX_Invoice_Final");
        String selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(InvoiceSuccess.this, selectedLanguage);
        }
        final ImageView back_logo = findViewById(R.id.back_logo);
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

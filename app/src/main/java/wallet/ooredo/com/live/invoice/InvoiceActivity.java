package wallet.ooredo.com.live.invoice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import coreframework.database.CustomSharedPreferences;
import coreframework.processing.InvoiceProcessing;
import coreframework.processing.PendingInvoicEditeProcessing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import coreframework.utils.PriceFormatter;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.utils.LocaleHelper;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceRequest;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceTranHistoryResponsePojo;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
/**
 * Created by 10037 on 20-Nov-17.
 */
public class InvoiceActivity extends GenericActivity implements YPCHeadlessCallback {
    //To send Image while generating Invoice
    private static final int CAMERA_REQUEST = 123;
    private static final int PICK_FROM_GALLERY = 2;
    Button submit_btn;
    EditText invoice_amount_edit, invoice_remarks_edit, merchant_edit_description_edit, merchant_inv_no, invoice_fullname_edit, invoice_emailid_edit,
            invoice_md_no_edit,
            invoice_civil_id_edit,
            invoice_nurse_id_edit,
            invoice_account_no_edit;
    AutoCompleteTextView invoice_mobileno_edit;
    String mobno_str, amount_str, invoiceNo, remarks_str, fullName, emailID, whatsAppNo = "";
    TextView description_text, merchant_edit_description_text;
    int RQS_PICK_CONTACT = 1;
    LinearLayout  merchant_edit_description_linear, invoice_choose_language_layout, invoice_hospital_layout, email_layout,
             invoice_emailid_edit_layout;
    Spinner invoice_choose_language_spinner, invoice_choose_send_link;
    String[] languages = {"English", " عربي"};

    String selectedLanguage = null;
    boolean internationalMobile = false;
    private String reject_edit_response = null;
    private boolean isPendingEdit = false;
    private String selected_language, selected_send_link;
    private String image_data = "";
    private TextView tv_attached_status = null;
    private ImageView img_attached_status = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting language
        String[] sendLinkType = {getString(R.string.SMS), getString(R.string.WhatsApp)};
        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(InvoiceActivity.this, selectedLanguage);
//            Toast toast = Toast.makeText(InvoiceActivity.this, "Language setted: "+selectedLanguage, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
//            toast.show();
        }
        //@end
        setContentView(R.layout.invoice_activity);
        //getActionBar().hide();
        enableUndoBar();
        submit_btn = findViewById(R.id.submit_btn);
        invoice_mobileno_edit = findViewById(R.id.invoice_mobileno_edit);
        invoice_amount_edit = findViewById(R.id.invoice_amount_edit);
        merchant_inv_no = findViewById(R.id.merchant_inv_no);
        invoice_remarks_edit = findViewById(R.id.invoice_remarks_edit);
        invoice_fullname_edit = findViewById(R.id.invoice_fullname_edit);
        invoice_emailid_edit = findViewById(R.id.invoice_emailId_edit);
        invoice_emailid_edit_layout = findViewById(R.id.invoice_emailId_edit_layout);
        invoice_mobileno_edit.setOnTouchListener((view, motionEvent) -> {
            final int DRAWABLE_RIGHT = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getRawX() >= (invoice_mobileno_edit.getRight() - invoice_mobileno_edit.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    invoice_mobileno_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                    internationalMobile = false;
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    startActivityForResult(intent, 1);
                }
            }
            return false;
        });


        //TEST
//        invoice_fullname_edit.setText("كيف حالكم");
//        invoice_fullname_edit.setText("Basha");
        tv_attached_status = findViewById(R.id.tv_attached_status);
        img_attached_status = findViewById(R.id.img_attached_status);
        //reasons_text_linear = (LinearLayout) findViewById(R.id.reasons_text_linear);
        description_text = findViewById(R.id.description_text);
        merchant_edit_description_text = findViewById(R.id.merchant_edit_description_text);
        merchant_edit_description_linear = findViewById(R.id.merchant_edit_description_linear);
        merchant_edit_description_edit = findViewById(R.id.merchant_edit_description_edit);
        email_layout = findViewById(R.id.email_label);
        merchant_inv_no.setEnabled(false);
        //newly added for hospital merchant
        invoice_hospital_layout = findViewById(R.id.invoice_hospital_layout);
        invoice_md_no_edit = findViewById(R.id.invoice_md_no_edit);
        invoice_civil_id_edit = findViewById(R.id.invoice_civil_id_edit);
        invoice_nurse_id_edit = findViewById(R.id.invoice_nurse_id_edit);
        invoice_account_no_edit = findViewById(R.id.invoice_account_no_edit);
        boolean extra_Filedds = CustomSharedPreferences.getbooleanData(InvoiceActivity.this, CustomSharedPreferences.SP_KEY.EXTRA_FIELDS);
        if (extra_Filedds)
            invoice_hospital_layout.setVisibility(View.VISIBLE);
        else
            invoice_hospital_layout.setVisibility(View.GONE);
        //choose language
        invoice_choose_language_layout = findViewById(R.id.invoice_choose_language_layout);
        invoice_choose_language_spinner = findViewById(R.id.invoice_choose_language_spinner);
        invoice_choose_send_link = findViewById(R.id.invoice_choose_send_link);
        invoice_choose_language_layout.setVisibility(View.VISIBLE);
        final LanguageType language = new LanguageType(getApplicationContext(), Arrays.asList(languages));
        //Setting the ArrayAdapter data on the Spinner
        invoice_choose_language_spinner.setAdapter(language);
        invoice_choose_language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_language = languages[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final SendLinkType sendLink = new SendLinkType(getApplicationContext(), Arrays.asList(sendLinkType));
        //Setting the ArrayAdapter data on the Spinner
        invoice_choose_send_link.setAdapter(sendLink);
        invoice_choose_send_link.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_send_link = sendLinkType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //float max_amount = Float.valueOf(invoice_amount_edit.getText().toString());
        //float max_amount =100;

        /*invoice_amount_edit.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        invoice_amount_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                amount_str = invoice_amount_edit.getText().toString().trim();
                int indexofDesc = amount_str.indexOf(".");
                if (indexofDesc > 4) {
                    invoice_amount_edit.setInputType(InputType.TYPE_NULL);
                }
                try {
                    amount_str = amount_str.toString();

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            reject_edit_response = bundle.getString("INVOICE_EDIT_DETAILS");
            isPendingEdit = bundle.getBoolean("TXN_PENDING_EDIT");
        }
        if (reject_edit_response != null) {
            InvoiceTranHistoryResponsePojo response = new Gson().fromJson(reject_edit_response, InvoiceTranHistoryResponsePojo.class);
            merchant_inv_no.setText(response.getPaymentRefId());
            invoice_remarks_edit.setVisibility(View.VISIBLE);
            invoice_remarks_edit.setHint("Reason for reject the invoice... ");
            description_text.setVisibility(View.VISIBLE);
            description_text.setText("Reason");
            //reasons_text_linear.setVisibility(View.VISIBLE);
            invoice_remarks_edit.setText(response.getBillType());
            invoice_mobileno_edit.setText(response.getRecipientMobileNumber());
            if (response.getCustomerName() != null && !response.getCustomerName().isEmpty()) {
                invoice_fullname_edit.setText(response.getCustomerName());
            }
            if (response.getArabicCustomerName() != null) {
                try {
                    Charset charset = Charset.forName("ISO-8859-6");
                    CharsetDecoder decoder = charset.newDecoder();
                    ByteBuffer buf = ByteBuffer.wrap(response.getArabicCustomerName());
                    CharBuffer cbuf = decoder.decode(buf);
                    CharSequence customer_name = java.nio.CharBuffer.wrap(cbuf);
                    invoice_fullname_edit.setText(" " + customer_name + "");
                } catch (Exception e) {
                    Log.e("Invoice CustName Ex:", "" + e.getMessage());
                }
            }
            invoice_emailid_edit.setText(response.getEmailId());
            invoice_remarks_edit.setEnabled(false);
            merchant_edit_description_text.setVisibility(View.VISIBLE);
            merchant_edit_description_linear.setVisibility(View.VISIBLE);
            merchant_edit_description_edit.setVisibility(View.VISIBLE);
            boolean isHospital_fields = CustomSharedPreferences.getbooleanData(InvoiceActivity.this, CustomSharedPreferences.SP_KEY.EXTRA_FIELDS);
            if (isHospital_fields) {
                invoice_hospital_layout.setVisibility(View.VISIBLE);
                if (!response.getMedFileNo().equals("") && response.getMedFileNo() != null) {
                    invoice_md_no_edit.setText(response.getMedFileNo());
                }
                if (!response.getCivilId().equals("") && response.getCivilId() != null) {
                    invoice_civil_id_edit.setText(response.getCivilId());
                }
                if (!response.getNurseId().equals("") && response.getNurseId() != null) {
                    invoice_nurse_id_edit.setText(response.getNurseId());
                }
                if (!response.getAccountNo().equals("") && response.getAccountNo() != null) {
                    invoice_account_no_edit.setText(response.getAccountNo());
                }
            } else {
                invoice_hospital_layout.setVisibility(View.GONE);
            }
            //language
            invoice_choose_language_layout.setVisibility(View.GONE);
            if (response.getDescription() != null && !response.getDescription().isEmpty()) {
                merchant_edit_description_edit.setText(response.getDescription());
            }
            if (response.getArabicDescription() != null) {
                try {
                    Charset charset = Charset.forName("ISO-8859-6");
                    CharsetDecoder decoder = charset.newDecoder();
                    ByteBuffer buf = ByteBuffer.wrap(response.getArabicDescription());
                    CharBuffer cbuf = decoder.decode(buf);
                    CharSequence description = java.nio.CharBuffer.wrap(cbuf);
                    merchant_edit_description_edit.setText(" " + description + "");
                } catch (Exception e) {
                    Log.e("Invoice Description Ex:", "" + e.getMessage());
                }
            }
            invoice_amount_edit.setText(PriceFormatter.format(response.getTotalPrice(), 3, 3));
        } else {
            //To get the current year
            int y = Calendar.getInstance().get(Calendar.YEAR);
            String s = String.valueOf(y);
            String year = s.substring(2);
            //String random_str = randomString(5);
            invoiceNo = String.valueOf(System.currentTimeMillis());
            merchant_inv_no.setText(invoiceNo);
            invoice_remarks_edit.setEnabled(true);
            merchant_edit_description_text.setVisibility(View.GONE);
            merchant_edit_description_linear.setVisibility(View.GONE);
            merchant_edit_description_edit.setVisibility(View.GONE);
        }
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobno_str = invoice_mobileno_edit.getText().toString().trim();
                if(selected_send_link.equals(getString(R.string.WhatsApp)))
                    whatsAppNo = "965" + mobno_str;
                amount_str = invoice_amount_edit.getText().toString().trim();
                invoiceNo = merchant_inv_no.getText().toString().trim();
                fullName = invoice_fullname_edit.getText().toString().trim();
                emailID = invoice_emailid_edit.getText().toString().trim();
                if (merchant_edit_description_edit.getVisibility() == View.VISIBLE) {
                    String merchant_edit_des = merchant_edit_description_edit.getText().toString();
                    remarks_str = merchant_edit_des.replace('\n', ' ');//replaces all occurrences of '\n' to 'free space'
                } else {
                    String merchant_edit_des = invoice_remarks_edit.getText().toString();
                    remarks_str = merchant_edit_des.replace('\n', ' ');//replaces all occurrences of '\n' to 'free space'
                }
                if (invoice_mobileno_edit.getText().toString().length() == 0) {
//                    showNeutralDialogue("Alert!", "Please enter mobile number");
                    showNeutralDialogue("Alert!", getString(R.string.please_enter_mobile_number));
                } else if (!internationalMobile && invoice_mobileno_edit.getText().toString().length() != 8) {
//                    showNeutralDialogue("Alert!", "Please enter valid mobile number...");
                    showNeutralDialogue("Alert!", getString(R.string.please_enter_valid_mobile_number));
                } else if (invoice_amount_edit.getText().toString().length() == 0) {
//                    showNeutralDialogue("Alert!", "Please enter amount");
                    showNeutralDialogue("Alert!", getString(R.string.please_enter_amount));
                } else if (invoice_hospital_layout.getVisibility() == View.VISIBLE) {
                    if (isPendingEdit) {
                        pendingEditRequest(mobno_str, amount_str, invoiceNo, remarks_str, fullName, emailID);
                    } else {
                        checkTheInvoiceNoAndAmountToProceed(invoiceNo, amount_str, mobno_str);
                        submitRequest(mobno_str, amount_str, invoiceNo, remarks_str, fullName, emailID, selected_language);
                    }
                } else {
                    if (isPendingEdit) {
                        pendingEditRequest(mobno_str, amount_str, invoiceNo, remarks_str, fullName, emailID);
                    } else {
                        checkTheInvoiceNoAndAmountToProceed(invoiceNo, amount_str, mobno_str);
                        submitRequest(mobno_str, amount_str, invoiceNo, remarks_str, fullName, emailID, selected_language);
                    }
                }
            }
        });
        //Arabic language logic
        TextView tv_amount_header = findViewById(R.id.tv_amount_header);
        TextView tv_mobile_no_header = findViewById(R.id.tv_mobile_no_header);
        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
//        if (selectedLanguage.equalsIgnoreCase("en")) {
//
//        } else {
//            tv_amount_header.setTextColor(Color.RED);
//            tv_mobile_no_header.setTextColor(Color.RED);
//        }
        tv_amount_header.setTextColor(Color.RED);
        tv_mobile_no_header.setTextColor(Color.RED);
        TextView attach_img_btn = findViewById(R.id.attach_tv_btn);
        attach_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog();
            }
        });
        ImageView img_attached_symbol = findViewById(R.id.img_attached_symbol);
        img_attached_symbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog();
            }
        });
//        Toast.makeText(InvoiceActivity.this, "OnCreate() called! ", Toast.LENGTH_SHORT).show();
    }

    public void checkTheInvoiceNoAndAmountToProceed(String invoiceNo, String amount, String mobileNo) {
        String recent_invoiceNo = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.RECENT_INVOICE_NO);
        String recent_invoice_amount = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.RECENT_INVOICE_AMOUNT);
        String recent_invoice_mobile_no = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.RECENT_INVOICE_MOBILE_NO);
        if (recent_invoiceNo.length() > 0 && recent_invoice_amount.length() > 0 && recent_invoice_mobile_no.length() > 0) {
            if (recent_invoiceNo.equalsIgnoreCase(invoiceNo) && recent_invoice_amount.equalsIgnoreCase(amount) && recent_invoice_mobile_no.equalsIgnoreCase(mobileNo)) {
                Toast.makeText(InvoiceActivity.this, "Please check the details once again", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void enableButton() {
        new Handler().postDelayed(new Runnable() {
                                      public void run() {
                                          submit_btn.setEnabled(true);
                                      }
                                  }, 5000    //Specific time in milliseconds
        );
    }
    //Send Image while generating Invoice

    private void alertDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custom_alert_image, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InvoiceActivity.this);
        alertDialog.setView(promptsView);
        alertDialog.setPositiveButton(getString(R.string.camera), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!checkPermissionForCamera()) {
                    Log.i("IF", "if");
                    requestPermissionForCamera(CAMERA_REQUEST);
                } else {
                    Log.i("ELSE", "else");
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    } else {
                        Toast.makeText(getBaseContext(), "Your camera can't able to take the data of picture ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        alertDialog.setNegativeButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);
                }
            }
        });
        alertDialog.show();
    }

    public boolean checkPermissionForCamera() {
        Log.i("checkPermission", "checkPermissionForCamera");
        int result = ContextCompat.checkSelfPermission(InvoiceActivity.this, android.Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionForCamera(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(InvoiceActivity.this, android.Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(InvoiceActivity.this, new String[]{android.Manifest.permission.CAMERA}, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Log.i("CAMERA_REQUEST", "camera request");
                //create instance of File with same name we created before to get image from storage
                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                Uri frontimageUri= data.getData();
                Bitmap mPhoto = Bitmap.createScaledBitmap(photo, 600, 600, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mPhoto.compress(Bitmap.CompressFormat.JPEG, 75, baos); //bm is the bitmap object
                byte[] imageData = baos.toByteArray();
                image_data = Base64.encodeToString(imageData, Base64.NO_WRAP);
                tv_attached_status.setText(getString(R.string.attached_successfully));
                img_attached_status.setImageResource(R.drawable.success);
                submit_btn.requestFocus();
                Toast toast = Toast.makeText(InvoiceActivity.this, getString(R.string.attached_successfully), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
                toast.show();
            }
            if (requestCode == RQS_PICK_CONTACT) {
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    cursor.moveToFirst();
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String str = number.replaceAll(" ", "").trim();
                    if (str.length() == 8) {
                        invoice_mobileno_edit.setText(str);
                    } else if (str.startsWith("+965")) {
                        str = str.replaceAll("\\+965", "");
                        invoice_mobileno_edit.setText(str);
                    } else {
                        str = str.replaceAll("\\D", "");
                        invoice_mobileno_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(str.length())});
                        invoice_mobileno_edit.setText(str);
                        internationalMobile = true;
                    }
                }
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(InvoiceActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
            toast.show();
        }
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
//                try {
//                    cropCapturedImage(imageUri);
//                } catch (ActivityNotFoundException aNFE) {
//                    String errorMessage = "Sorry - your device doesn't support the crop action!";
//                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//                    toast.show();
//                }
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap mPhoto = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mPhoto.compress(Bitmap.CompressFormat.JPEG, 75, baos); //bm is the bitmap object
                byte[] imageData = baos.toByteArray();
                image_data = Base64.encodeToString(imageData, Base64.NO_WRAP);
                tv_attached_status.setText(getString(R.string.attached_successfully));
                img_attached_status.setImageResource(R.drawable.success);
                submit_btn.requestFocus();
                Toast toast = Toast.makeText(InvoiceActivity.this, getString(R.string.attached_successfully), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
                toast.show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(InvoiceActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
                toast.show();
            }
        }
        //Setting language
        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(InvoiceActivity.this, selectedLanguage);
//            Toast toast = Toast.makeText(InvoiceActivity.this, "Language setted: "+selectedLanguage, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
//            toast.show();
        }
        //@end
    }

    private void cropCapturedImage(Uri uri) {
        //call the standard crop action intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(uri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, 0);
    }

    private void submitRequest(String mobno_str, String amount_str, String invoiceNo, String remarks_str, String fullName, String emailID, String selected_language) {
        CustomSharedPreferences.saveStringData(getApplicationContext(), invoiceNo, CustomSharedPreferences.SP_KEY.RECENT_INVOICE_NO);
        CustomSharedPreferences.saveStringData(getApplicationContext(), amount_str, CustomSharedPreferences.SP_KEY.RECENT_INVOICE_AMOUNT);
        CustomSharedPreferences.saveStringData(getApplicationContext(), mobno_str, CustomSharedPreferences.SP_KEY.RECENT_INVOICE_MOBILE_NO);
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setInvoiceNo(invoiceNo);
        invoiceRequest.setAmount(Double.parseDouble(amount_str));
        invoiceRequest.setMobileNo(mobno_str);
        invoiceRequest.setDescription(remarks_str);
        invoiceRequest.setCustName(fullName);
        invoiceRequest.setCustEmailId(emailID);
        invoiceRequest.setLanguage(selected_language);
        invoiceRequest.setMerchantRequest(true);
        invoiceRequest.setClientImage(image_data);
        invoiceRequest.setSMSSent(selected_send_link.equals(getString(R.string.SMS)));
        try {
            Charset charset = Charset.forName("ISO-8859-6");
            CharsetEncoder encoder = charset.newEncoder();
            if (fullName.length() > 0) {
                ByteBuffer fullName_byte_buffer = encoder.encode(CharBuffer.wrap(fullName));
                byte[] fullName_byte_array = new byte[fullName_byte_buffer.remaining()];
                fullName_byte_buffer.get(fullName_byte_array);
                invoiceRequest.setArabicCustomerName(fullName_byte_array);
            }
            if (remarks_str.length() > 0) {
                ByteBuffer description_byte_buffer = encoder.encode(CharBuffer.wrap(remarks_str));
                byte[] description_byte_array = new byte[description_byte_buffer.remaining()];
                description_byte_buffer.get(description_byte_array);
                invoiceRequest.setArabicDescription(description_byte_array);
            }
        } catch (Exception e) {
            Log.e("Invoice Error: ", "Arabic Error: " + e.getMessage());
        }
        //hospital
        if (invoice_hospital_layout.getVisibility() == View.VISIBLE) {
            invoiceRequest.setMedFileNo(invoice_md_no_edit.getText().toString());
            invoiceRequest.setAccountNo(invoice_account_no_edit.getText().toString());
            invoiceRequest.setNurseId(invoice_nurse_id_edit.getText().toString());
            invoiceRequest.setCivilId(invoice_civil_id_edit.getText().toString());
        }
        String merchant_ref_id = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
        invoiceRequest.setMerchantRefNumber(merchant_ref_id);
        //Code to start server thread and display the progress fragment dialogue (retained)
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new InvoiceProcessing(invoiceRequest, whatsAppNo, application, true));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(false);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }

    private void pendingEditRequest(String mobno_str, String amount_str, String invoiceNo, String remarks_str, String fullName, String emailID) {
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setInvoiceNo(invoiceNo);
        invoiceRequest.setAmount(Double.parseDouble(amount_str));
        invoiceRequest.setMobileNo(mobno_str);
        invoiceRequest.setDescription(remarks_str);
        invoiceRequest.setCustName(fullName);
        invoiceRequest.setCustEmailId(emailID);
        invoiceRequest.setClientImage(image_data);
        try {
            Charset charset = Charset.forName("ISO-8859-6");
            CharsetEncoder encoder = charset.newEncoder();
            if (fullName.length() > 0) {
                ByteBuffer fullName_byte_buffer = encoder.encode(CharBuffer.wrap(fullName));
                byte[] fullName_byte_array = new byte[fullName_byte_buffer.remaining()];
                fullName_byte_buffer.get(fullName_byte_array);
                invoiceRequest.setArabicCustomerName(fullName_byte_array);
            }
            if (remarks_str.length() > 0) {
                ByteBuffer description_byte_buffer = encoder.encode(CharBuffer.wrap(remarks_str));
                byte[] description_byte_array = new byte[description_byte_buffer.remaining()];
                description_byte_buffer.get(description_byte_array);
                invoiceRequest.setArabicDescription(description_byte_array);
            }
        } catch (Exception e) {
            Log.e("Invoice Error: ", "Arabic Error: " + e.getMessage());
        }
        invoiceRequest.setMerchantRequest(true);
        String merchant_ref_id = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
        invoiceRequest.setMerchantRefNumber(merchant_ref_id);
        //hospital
        if (invoice_hospital_layout.getVisibility() == View.VISIBLE) {
            invoiceRequest.setMedFileNo(invoice_md_no_edit.getText().toString());
            invoiceRequest.setAccountNo(invoice_account_no_edit.getText().toString());
            invoiceRequest.setNurseId(invoice_nurse_id_edit.getText().toString());
            invoiceRequest.setCivilId(invoice_civil_id_edit.getText().toString());
        }
        //Code to start server thread and display the progress fragment dialogue (retained)
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new PendingInvoicEditeProcessing(invoiceRequest, application, true));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(false);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }

    private String randomString(int len) {
        String AB = "0123456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString().toUpperCase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set language
        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(InvoiceActivity.this, selectedLanguage);
        }
//        Toast.makeText(InvoiceActivity.this, "Merchant logo: "+((CoreApplication) getApplication()).getMerchnat_logo(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(InvoiceActivity.this, "Merchant logo: "+((CoreApplication) getApplication()), Toast.LENGTH_SHORT).show();
        Log.e("Merchant logo url: ", "" + ((CoreApplication) getApplication()).getMerchantLoginRequestResponse().getMerchantLogo());
        final ImageView back_logo = findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());
        String mx_logo_url;
        if (((CoreApplication) getApplication()).getMerchnat_logo() == null) {
            MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
            Log.e("Merchant logo url: ", "" + merchantLoginRequestResponse.getMerchantLogo());
            mx_logo_url = merchantLoginRequestResponse.getMerchantLogo();
            String login_response = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
            Log.e("Merchant login resp: ", "" + login_response);
            if (mx_logo_url == null) {
                login_response = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
                Log.e("MX login resp in if: ", "" + login_response);
                try {
                    JSONObject login_resp_jo = new JSONObject(login_response);
                    mx_logo_url = login_resp_jo.getString("merchantLogo");
                } catch (Exception e) {
                    Log.e("MX log Ex: ", "" + e.getMessage());
                }
            }
//            String url =  "https://www.bookeey.com/mno/merlogo/mer160008.jpg";
//            new DownloadImageTask(back_logo).execute(url);
            new DownloadImageTask(back_logo).execute(mx_logo_url);
//            Toast.makeText(MainActivity.this,"Getting merchant logo",Toast.LENGTH_LONG).show();
        }
    }

    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String image_string = Base64.encodeToString(b, Base64.DEFAULT);
        CustomSharedPreferences.saveStringData(getBaseContext(), image_string, CustomSharedPreferences.SP_KEY.MERCHANT_LOGO);
        return image_string;
    }

    private void refresh(InvoiceActivity loginActivity, String selectedLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = loginActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, InvoiceActivity.class);
            startActivity(refresh);
            finish();
        } else {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = loginActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, InvoiceActivity.class);
            startActivity(refresh);
            finish();
        }
    }

    @Override
    public void onProgressUpdate(int progress) {
    }

    @Override
    public void onProgressComplete() {
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
                ((CoreApplication) getApplication()).setMerchnat_logo(result);
                bmImage.setImageBitmap(result);
                bitmapToString(result);
            }
        }
    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(getBaseContext(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
}


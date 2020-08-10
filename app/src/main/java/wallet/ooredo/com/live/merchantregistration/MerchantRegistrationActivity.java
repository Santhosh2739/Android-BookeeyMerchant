package wallet.ooredo.com.live.merchantregistration;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import coreframework.database.CustomSharedPreferences;
import coreframework.processing.MerchantLoginProcessing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.invoice.InvoiceActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;

public class MerchantRegistrationActivity extends GenericActivity implements YPCHeadlessCallback {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static String TAG = "DEVICE_ID";
    private static String SENDER_ID = "861276600463"; //google api project number
    private GoogleCloudMessaging gcm = null;
    static String region_value, vehicle;
    static long date;
    ArrayAdapter<String> region_adapter;
    int position1;
    String regid_reg_id;
    String imeinumber = null;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 89;

    String selectedLanguage = null;

    ImageView coutry_flag_img = null;
    TextView language_text = null;
    public static final int REQUEST_READ_PHONE_STATE = 10011;

    ArrayList<String> business_type_items=new ArrayList<String>();
    Spinner business_type;


    //To send Image while generating Invoice
    private static final int CAMERA_REQUEST = 123;
    private static final int PICK_FROM_GALLERY = 2;
    private String image_data = "";

    private TextView tv_attached_status =null;
    private ImageView img_attached_status  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_registration);

        enableUndoBar();

        business_type_items.add("Automotive");
        business_type_items.add("Fitness");
        business_type_items.add("Services");
        business_type_items.add("Games & Toys");
        business_type_items.add("Electronics & Computers");
        business_type_items.add("Groceries & Hypermarkets");
        business_type_items.add("Transportation");
        business_type_items.add("Hotels & Accomadation");
        business_type_items.add("Health & Beauty");
        business_type_items.add("Medical Care");
        business_type_items.add("Retail");
        business_type_items.add("Fashion");
        business_type_items.add("Food & Beverages");
        business_type_items.add("All");

        business_type =(Spinner)findViewById(R.id.business_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, business_type_items);
        business_type.setAdapter(adapter);


        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                ((CoreApplication) getApplication()).setMerchnat_logo(null);


                String userId = ((EditText) findViewById(R.id.login_user_id)).getText().toString();

                String password = ((EditText) findViewById(R.id.login_password)).getText().toString();


                merchantLogin(imeinumber, userId, password);


            }
        });


        //Language refresh
        final LinearLayout language_layout = (LinearLayout) findViewById(R.id.language_layout);


        coutry_flag_img = (ImageView) findViewById(R.id.coutry_flag_img);
        language_text = (TextView) findViewById(R.id.language_text);

        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MerchantRegistrationActivity.this, selectedLanguage);
//            refresh(MerchantLoginActivity.this, selectedLanguage);
        }

        //language selection KrypC logic
//        if (language_text.getText().toString().equals("English")) {
//            language_text.setText(getResources().getString(R.string.login_arabic));
//            coutry_flag_img.setImageResource(R.drawable.kuwait);
//        } else {
//            language_text.setText(getResources().getString(R.string.login_english));
//            coutry_flag_img.setImageResource(R.drawable.usa);
//        }

        //Rahman logic

        if (selectedLanguage.equalsIgnoreCase("en")) {
            language_text.setText(getResources().getString(R.string.login_arabic));
            coutry_flag_img.setImageResource(R.drawable.kuwait);
        } else if(selectedLanguage.equalsIgnoreCase("ar")){
            language_text.setText(getResources().getString(R.string.login_english));
            coutry_flag_img.setImageResource(R.drawable.usa);
        }else{

            language_text.setText(getResources().getString(R.string.login_arabic));
            coutry_flag_img.setImageResource(R.drawable.kuwait);

        }


        language_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (language_text.getText().toString().equals("English")) {
                    language_text.setText(getResources().getString(R.string.login_arabic));
                    selectedLanguage = "en";
                    coutry_flag_img.setImageResource(R.drawable.kuwait);
                    CustomSharedPreferences.saveStringData(getApplicationContext(), "en", CustomSharedPreferences.SP_KEY.LANGUAGE);
                    LocaleHelper.setLocale(MerchantRegistrationActivity.this, "en");
                    refresh(MerchantRegistrationActivity.this, "en");

                } else {
                    selectedLanguage = "ar";
                    language_text.setText(getResources().getString(R.string.login_english));
                    coutry_flag_img.setImageResource(R.drawable.usa);
                    CustomSharedPreferences.saveStringData(getApplicationContext(), "ar", CustomSharedPreferences.SP_KEY.LANGUAGE);
                    LocaleHelper.setLocale(MerchantRegistrationActivity.this, "ar");
                    refresh(MerchantRegistrationActivity.this, "ar");
                }
            }
        });




        TextView attach_img_btn  = (TextView)findViewById(R.id.attach_tv_btn);
        attach_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog();
            }
        });


        ImageView img_attached_symbol  = (ImageView)findViewById(R.id.img_attached_symbol);
        img_attached_symbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog();
            }
        });

        tv_attached_status= (TextView)findViewById(R.id.tv_attached_status);
        img_attached_status = (ImageView)findViewById(R.id.img_attached_status) ;

    }


    private void refresh(MerchantRegistrationActivity mainActivity, String selectedLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = mainActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, MerchantRegistrationActivity.class);
            startActivity(refresh);
            finish();
        } else {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = mainActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, MerchantRegistrationActivity.class);
            startActivity(refresh);
            finish();
        }
    }

    private void merchantLogin(String imeinumber, String userId, String password) {
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new MerchantLoginProcessing(imeinumber, userId, password, true, application));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(true);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }


    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onProgressComplete() {

    }




    //Send Image while generating Invoice

    private void alertDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custom_alert_image, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MerchantRegistrationActivity.this);
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
                        Toast.makeText(getBaseContext(), "Your camera can't able to take the data of picture ", Toast.LENGTH_SHORT);
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
        int result = ContextCompat.checkSelfPermission(MerchantRegistrationActivity.this, android.Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    public void requestPermissionForCamera(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MerchantRegistrationActivity.this, android.Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MerchantRegistrationActivity.this, new String[]{android.Manifest.permission.CAMERA}, requestCode);

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

//                submit_btn.requestFocus();

                Toast toast = Toast.makeText(MerchantRegistrationActivity.this, getString(R.string.attached_successfully), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
                toast.show();


            }

        } catch (Exception e) {
            Toast toast = Toast.makeText(MerchantRegistrationActivity.this, "Something went wrong\n\n"+e.getMessage(), Toast.LENGTH_SHORT);
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

//                submit_btn.requestFocus();



                Toast toast = Toast.makeText(MerchantRegistrationActivity.this, getString(R.string.attached_successfully), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
                toast.show();



            } catch (Exception e) {

                e.printStackTrace();

                Toast toast = Toast.makeText(MerchantRegistrationActivity.this, "Something went wrong \n\n"+e.getMessage(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
                toast.show();
            }

        }



        //Setting language
        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {

            LocaleHelper.setLocale(MerchantRegistrationActivity.this, selectedLanguage);



        }
        //@end


    }


}


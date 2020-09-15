package wallet.ooredo.com.live.merchantlogin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Locale;

import coreframework.database.CustomSharedPreferences;
import coreframework.processing.MerchantLoginProcessing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;

/**
 * Created by munireddy on 15-06-2015.
 */
public class MerchantLoginActivity extends GenericActivity implements YPCHeadlessCallback {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_login);
        enableUndoBar();
        String deviceID = ((CoreApplication) getApplication()).getThisDeviceUniqueAndroidId();
        TextView device_id = (TextView) findViewById(R.id.device_id);
        device_id.setText("Your Device Id: " + deviceID);
        //DEMO TEST
//        ((EditText) findViewById(R.id.login_user_id)).setText("mref16000162");
//        ((EditText) findViewById(R.id.login_password)).setText("9002");

//        PRD
//        ((EditText) findViewById(R.id.login_user_id)).setText("mref1600057");
//        ((EditText) findViewById(R.id.login_password)).setText("1234");

        //DEMO TEST
//        ((EditText) findViewById(R.id.login_user_id)).setText("mref18000289");
//        ((EditText) findViewById(R.id.login_password)).setText("9817");




        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                ((CoreApplication) getApplication()).setMerchnat_logo(null);


                String userId = ((EditText) findViewById(R.id.login_user_id)).getText().toString();

                String password = ((EditText) findViewById(R.id.login_password)).getText().toString();
                /*if(checkPermission()) {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    imeinumber = telephonyManager.getDeviceId();
                }*/
//                imeinumber = getUniqueIMEIId();
                if (userId.length() == 0) {
                    showNeutralDialogue("Info", getString(R.string.please_enter_merchant_id));
                    return;
                }
                if (password.length() == 0) {
                    showNeutralDialogue("Info", getString(R.string.please_enter_password));
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean isEnabled = checkLocationPermission();
                    if (isEnabled)
                        merchantLogin(imeinumber, userId, password);
                }else{
                    merchantLogin(imeinumber, userId, password);
                }

            }
        });


        //Language refresh
        final LinearLayout language_layout = (LinearLayout) findViewById(R.id.language_layout);


        coutry_flag_img = (ImageView) findViewById(R.id.coutry_flag_img);
         language_text = (TextView) findViewById(R.id.language_text);

        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MerchantLoginActivity.this, selectedLanguage);
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
                    LocaleHelper.setLocale(MerchantLoginActivity.this, "en");
                    refresh(MerchantLoginActivity.this, "en");

                } else {
                    selectedLanguage = "ar";
                    language_text.setText(getResources().getString(R.string.login_english));
                    coutry_flag_img.setImageResource(R.drawable.usa);
                    CustomSharedPreferences.saveStringData(getApplicationContext(), "ar", CustomSharedPreferences.SP_KEY.LANGUAGE);
                    LocaleHelper.setLocale(MerchantLoginActivity.this, "ar");
                    refresh(MerchantLoginActivity.this, "ar");
                }
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    imeinumber = getUniqueIMEIId();

//                    Toast.makeText(MerchantLoginActivity.this, "1. "+imeinumber,Toast.LENGTH_LONG).show();


                }
                break;

            default:
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            imeinumber = getUniqueIMEIId();

//            Toast.makeText(MerchantLoginActivity.this, "2. "+imeinumber,Toast.LENGTH_LONG).show();
        }

       //Facebook
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("MX_LoginInvoked");

        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);

//        Toast.makeText(MerchantLoginActivity.this," 0. "+selectedLanguage,Toast.LENGTH_LONG).show();

        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MerchantLoginActivity.this, selectedLanguage);
//            refresh(MerchantLoginActivity.this, selectedLanguage);
        }else{
            LocaleHelper.setLocale(MerchantLoginActivity.this, "en");
            language_text.setText(getResources().getString(R.string.login_arabic));
            coutry_flag_img.setImageResource(R.drawable.kuwait);

//            Toast.makeText(MerchantLoginActivity.this," 1. "+selectedLanguage,Toast.LENGTH_LONG).show();


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

//            Toast.makeText(MerchantLoginActivity.this,"2. "+selectedLanguage,Toast.LENGTH_LONG).show();


        }
    }

    private void refresh(MerchantLoginActivity mainActivity, String selectedLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = mainActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, MerchantLoginActivity.class);
            startActivity(refresh);
            finish();
        } else {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = mainActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, MerchantLoginActivity.class);
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

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MerchantLoginActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private String getUniqueIMEIId() {
        try {
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(MerchantLoginActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!checkPermissionForPhoneState()) {
//                        Log.i("IF", "if");
//                        requestPermissionForIMEI(1002);
//                        imeinumber = telephonyManager.getDeviceId();
//                    }
//                }
//                return imeinumber;
//            }
            imeinumber = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.e("imei", "=" + imeinumber);
            if (imeinumber != null && !imeinumber.isEmpty()) {
                return imeinumber;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeinumber;
    }

    private void requestPermissionForIMEI(int i) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MerchantLoginActivity.this, android.Manifest.permission.READ_PHONE_STATE)) {

//            Toast.makeText(MerchantLoginActivity.this, "Phone read permission needed. Please allow in App Settings for additional functionality", Toast.LENGTH_LONG).show();


            Toast.makeText(MerchantLoginActivity.this, getString(R.string.phone_read_permission_needed), Toast.LENGTH_LONG).show();

        }else {
            ActivityCompat.requestPermissions(MerchantLoginActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, i);
        }
    }

    private boolean checkPermissionForPhoneState() {
        Log.i("checkPermission", "checkPermissionForPhoneState");
        int result = ContextCompat.checkSelfPermission(MerchantLoginActivity.this, android.Manifest.permission.READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //    private String getRegistrationId(Context context) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
//        if (registrationId.isEmpty()) {
//            Log.i(this.toString(), "Registration not found.");
//            return "";
//        }
//        // Check if app was updated; if so, it must clear the registration ID
//        // since the existing regID is not guaranteed to work with the new app version.
//        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(context);
//        if (registeredVersion != currentVersion) {
//            Log.i(this.toString(), "App version changed.");
//            return "";
//        }
//        return registrationId;
//    }
//
//    private void storeRegistrationId(Context context, String regId) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        int appVersion = getAppVersion(context);
//        Log.i(this.toString(), "Saving regId on app version " + appVersion);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(PROPERTY_REG_ID, regId);
//        editor.putInt(PROPERTY_APP_VERSION, appVersion);
//        editor.commit();
//    }
//
//    private static int getAppVersion(Context context) {
//        try {
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            throw new RuntimeException("Could not get package name: " + e);
//        }
//    }
//
//    private SharedPreferences getGCMPreferences(Context context) {
//        // This sample app persists the registration ID in shared preferences, but
//        // how you store the regID in your app is up to you.
//        return getSharedPreferences(MerchantLoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
//    }
//
//    private void registerInBackground() {
//        new AsyncTask<String, Void, String>() {
//            private String regId = "";
//
//            @Override
//            protected String doInBackground(String... params) {
//                String msg = "";
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(MerchantLoginActivity.this);
//                    }
//                    regId = gcm.register(SENDER_ID);
//                    Log.d(TAG, "########################################");
//                    Log.d(TAG, "Current Device's Registration ID is: " + msg);
//                    Log.i(this.toString(), "regId = " + regId);
//                    // You should send the registration ID to your server over HTTP,
//                    // so it can use GCM/HTTP or CCS to send messages to your app.
//                    // The request to your server should be authenticated if your app
//                    // is using accounts.
//                    // sendRegistrationIdToBackend();
//                    // For this demo: we don't need to send it because the device
//                    // will send upstream messages to a server that echo back the
//                    // message using the 'from' address in the message.
//                    // Persist the regID - no need to register again.
//                    storeRegistrationId(MerchantLoginActivity.this, regId);
//                    msg = regId;
//                } catch (IOException ex) {
//                    msg = "Error :" + ex.getMessage();
//                    // If there is an error, don't just keep trying to register.
//                    // Require the user to click a button again, or perform
//                    // exponential back-off.
//                }
//                return msg;
//            }
//
//            @Override
//            protected void onPostExecute(String msg) {
//                // setting registration id in edit text.
//                CustomSharedPreferences.saveGCMRegId(MerchantLoginActivity.this, msg, CustomSharedPreferences.SP_KEY.GCM_REG_ID);
//            }
//
//        }.execute(null, null, null);
//    }
//
//
//
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.i(this.toString(), "This device is not supported.");
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }
    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onProgressComplete() {

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MerchantLoginActivity.this, android.Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }
}

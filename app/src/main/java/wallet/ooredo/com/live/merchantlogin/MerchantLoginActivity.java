package wallet.ooredo.com.live.merchantlogin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import coreframework.database.CustomSharedPreferences;
import coreframework.processing.MerchantLoginProcessing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.utils.LocaleHelper;

public class MerchantLoginActivity extends GenericActivity implements YPCHeadlessCallback {
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

//DEMO TEST
        // ((EditText) findViewById(R.id.login_user_id)).setText("mref18000301");
        //   ((EditText) findViewById(R.id.login_password)).setText("4140");

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((CoreApplication) getApplication()).setMerchnat_logo(null);
                String userId = ((EditText) findViewById(R.id.login_user_id)).getText().toString();
                String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

                if (userId.length() == 0) {
                    showNeutralDialogue("Info", getString(R.string.please_enter_merchant_id));
                    return;
                }
                if (password.length() == 0) {
                    showNeutralDialogue("Info", getString(R.string.please_enter_password));
                    return;
                }
                merchantLogin(imeinumber, userId, password);
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

        if (selectedLanguage.equalsIgnoreCase("en")) {
            language_text.setText(getResources().getString(R.string.login_arabic));
            coutry_flag_img.setImageResource(R.drawable.kuwait);
        } else if (selectedLanguage.equalsIgnoreCase("ar")) {
            language_text.setText(getResources().getString(R.string.login_english));
            coutry_flag_img.setImageResource(R.drawable.usa);
        } else {

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
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MerchantLoginActivity.this, selectedLanguage);
//            refresh(MerchantLoginActivity.this, selectedLanguage);
        } else {
            LocaleHelper.setLocale(MerchantLoginActivity.this, "en");
            language_text.setText(getResources().getString(R.string.login_arabic));
            coutry_flag_img.setImageResource(R.drawable.kuwait);
        }

        if (selectedLanguage.equalsIgnoreCase("en")) {
            language_text.setText(getResources().getString(R.string.login_arabic));
            coutry_flag_img.setImageResource(R.drawable.kuwait);
        } else if (selectedLanguage.equalsIgnoreCase("ar")) {
            language_text.setText(getResources().getString(R.string.login_english));
            coutry_flag_img.setImageResource(R.drawable.usa);
        } else {
            language_text.setText(getResources().getString(R.string.login_arabic));
            coutry_flag_img.setImageResource(R.drawable.kuwait);
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

    @Override
    public void onProgressUpdate(int progress) {
    }

    @Override
    public void onProgressComplete() {
    }
}

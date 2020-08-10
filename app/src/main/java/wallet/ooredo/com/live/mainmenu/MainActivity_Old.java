package wallet.ooredo.com.live.mainmenu;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.google.zxing.client.android.CaptureActivity;
import coreframework.database.CustomSharedPreferences;
import coreframework.processing.DecodeIncomingQrPaymentCode;
import coreframework.processing.OffersProcessing;
import coreframework.processing.QRCodeOfferRedeemValidateProcessing;
import coreframework.taskframework.MainGenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.barcodepaymentcollection.DisplayAmountToMerchantActivityDummy;
import wallet.ooredo.com.live.consolidatedreports.Consolidated_Reports_Selection;
import wallet.ooredo.com.live.invoice.InvoiceActivity;
import wallet.ooredo.com.live.service.Util;
import wallet.ooredo.com.live.transactionhistory.TransactionHistoryActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;
import ycash.wallet.json.pojo.decodedbarcode.DecodedQrPojo;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.offerredeem.OfferPreviewRequest;
import ycash.wallet.json.pojo.pushnotification.SendVersionNumber;
import ycash.wallet.json.pojo.transactionhistory.TransactionHistoryResponse;


/**
 * Created by munireddy on 15-06-2015.
 */
public class MainActivity_Old extends MainGenericActivity implements YPCHeadlessCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    static final String tag = MainActivity_Old.class.getCanonicalName();
//    private GridView mGridMain;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    List<MainMenuGridInfo> listAppInfo = new ArrayList<MainMenuGridInfo>();
//    MainMenuGridViewAdapter processAdapter;
    MainMenuListViewAdapter processAdapter;

    private static final int collect_payment_mtag = 1006;
    private static final int collect_payment_history_mtag = 1007;
    private static final int online_payments_mtag = 1008;
    private static final int consolidated_reports = 1010;
    private static final int redeem_offer = 1011;

    private static final int invoice = 1012;
    private static final int offers = 1013;

    MainMenuGridInfo messageGrid;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static String TAG = "DEVICE_ID";
    private static String SENDER_ID = "861276600463"; //google api project number
    //    private static String SENDER_ID = "603716903348"; //google api project number
    private GoogleCloudMessaging gcm = null;
    TextView main_menu_branch_name__bottom_tiltle, main_menu_merchant_refid__bottom_tiltle;
    private String batteryStatus = null;
    String imeinumber = null;

    //for location

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    int count = 0;

    String selectedLanguage = null;

    public static final String KEY_CAMERA_FACING = "KEY_CAMERA_FACING";
    public static final String KEY_COMING_BY_CAPTURE_BACK = "KEY_COMING_BY_CAPTURE_BACK";

    public static final int REQUEST_READ_PHONE_STATE = 10011;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* requestWindowFeature(Window.FEATURE_NO_TITLE);// hide statusbar of Android
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/
        setContentView(R.layout.merchant_mainmenu);





        ActionBar.LayoutParams params = new
                ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setTitle("MainMenu");
        View cView = getLayoutInflater().inflate(R.layout.actionbar, null);
        TextView title_text = (TextView) cView.findViewById(R.id.title_text);
        title_text.setText("MainMenu");
        getActionBar().setCustomView(cView, params);


       /* TextView merchant_pos_id = (TextView) findViewById(R.id.merchant_pos_id);
        merchant_pos_id.setText("MainMenu");*/
        String name = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.NAME);
        String location = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.LOCATION);
        String merchantId = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);

        String authtoken = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.AUTH_TOKEN);

        main_menu_branch_name__bottom_tiltle = (TextView) findViewById(R.id.main_menu_branch_name__bottom_tiltle);
        main_menu_merchant_refid__bottom_tiltle = (TextView) findViewById(R.id.main_menu_merchant_refid__bottom_tiltle);

        main_menu_merchant_refid__bottom_tiltle.setText(merchantId);
        MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
        if (merchantLoginRequestResponse.getMerchantName() != null) {
            ((TextView) findViewById(R.id.main_menu_merchant_name__bottom_tiltle)).setText(merchantLoginRequestResponse.getMerchantName());
        } else {
            ((TextView) findViewById(R.id.main_menu_merchant_name__bottom_tiltle)).setText(name);
        }
        /*if (merchantLoginRequestResponse.getMerchantAddress() != null && merchantLoginRequestResponse.getMerchantAddress().length() > 0) {
            ((TextView) findViewById(R.id.merchant_address_tv)).setText("( " + merchantLoginRequestResponse.getMerchantAddress() + " )");
            ((TextView) findViewById(R.id.merchant_address_tv)).setSelected(true);
        }*/
        if (merchantLoginRequestResponse.getMerchantAddress() != null) {
            main_menu_branch_name__bottom_tiltle.setText("(" + merchantLoginRequestResponse.getMerchantAddress() + ")");
        } else {
            main_menu_branch_name__bottom_tiltle.setText("(" + location + ")");
        }
        if (merchantLoginRequestResponse.getOauth_2_0_client_token() != null) {
            authtoken = merchantLoginRequestResponse.getOauth_2_0_client_token();
            CustomSharedPreferences.saveStringData(MainActivity_Old.this, authtoken, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        } else {
            CustomSharedPreferences.saveStringData(MainActivity_Old.this, authtoken, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        }
//        mGridMain = (GridView) findViewById(R.id.gvMain);

        ListView listMain  = (ListView)findViewById(R.id.listMain);

        listAppInfo.add(new MainMenuGridInfo(R.drawable.main_collectpayment_selector, R.string.collectpayment, collect_payment_mtag));
        listAppInfo.add(new MainMenuGridInfo(R.drawable.invoice_creation3, R.string.invoice, invoice));
        listAppInfo.add(new MainMenuGridInfo(R.drawable.main_collectpay_history_selector, R.string.collectpayment_history, collect_payment_history_mtag));
        listAppInfo.add(new MainMenuGridInfo(R.drawable.consolidated, R.string.consolidated_reports, consolidated_reports));
        listAppInfo.add(new MainMenuGridInfo(R.drawable.offer, R.string.offers, offers));
        listAppInfo.add(new MainMenuGridInfo(R.drawable.redemtionlogo_g, R.string.redeem_offer, redeem_offer));
//        listAppInfo.add(new MainMenuGridInfo(R.drawable.redemtionlogo_g, R.string.redeem_offer, redeem_offer));



        //Adding items at indexes
//        listAppInfo.set(0,new MainMenuGridInfo(R.drawable.main_collectpayment_selector, R.string.collectpayment, collect_payment_mtag));
//        listAppInfo.set(1,new MainMenuGridInfo(R.drawable.invoice_creation3, R.string.invoice, invoice));
//        listAppInfo.set(2,new MainMenuGridInfo(R.drawable.main_collectpay_history_selector, R.string.collectpayment_history, collect_payment_history_mtag));
//        listAppInfo.set(3,new MainMenuGridInfo(R.drawable.consolidated, R.string.consolidated_reports, consolidated_reports));
//        listAppInfo.set(4,new MainMenuGridInfo(R.drawable.offer, R.string.offers, offers));
//        listAppInfo.set(5,new MainMenuGridInfo(R.drawable.redemtionlogo_g, R.string.redeem_offer, redeem_offer));







//        processAdapter = new MainMenuGridViewAdapter(this, listAppInfo);

        processAdapter = new MainMenuListViewAdapter(this, listAppInfo);
        listMain.setAdapter(processAdapter);
        listMain.setOnItemClickListener(mItemClickListener);
        float batteryLevel = getBatteryLevel(); //Your function
        batteryStatus = String.valueOf(batteryLevel);

        sendVersionNumber(batteryStatus);

        //check gps
        checkGPS(MainActivity_Old.this);


        //location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds



//        Toast.makeText(MainActivity.this,"onCreate",Toast.LENGTH_LONG).show();



        //Language refresh
        final LinearLayout language_layout = (LinearLayout) findViewById(R.id.language_layout);
        final ImageView coutry_flag_img = (ImageView) findViewById(R.id.coutry_flag_img);
        final TextView language_text = (TextView) findViewById(R.id.language_text);

        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MainActivity_Old.this, selectedLanguage);
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
                    LocaleHelper.setLocale(MainActivity_Old.this, "en");
                    refresh(MainActivity_Old.this, "en");

                } else {
                    selectedLanguage = "ar";
                    language_text.setText(getResources().getString(R.string.login_english));
                    coutry_flag_img.setImageResource(R.drawable.usa);
                    CustomSharedPreferences.saveStringData(getApplicationContext(), "ar", CustomSharedPreferences.SP_KEY.LANGUAGE);
                    LocaleHelper.setLocale(MainActivity_Old.this, "ar");
                    refresh(MainActivity_Old.this, "ar");
                }
            }
        });



        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));



    }



    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Intent intentCapture = new Intent(getBaseContext(), CaptureActivity.class);
            intentCapture.setAction("br.com.google.zxing.client.android.SCAN");
            intentCapture.putExtra("SCAN_MODE", "QR_CODE_MODE");
            intentCapture.putExtra("CHARACTER_SET", "ISO-8859-1");
            int camerFace =  CustomSharedPreferences.getIntData(getApplicationContext(),CustomSharedPreferences.SP_KEY.KEY_CAMERA_FACING);
            intentCapture.putExtra(KEY_CAMERA_FACING, camerFace);
            startActivityForResult(intentCapture, 0);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        Toast.makeText(this, "You pressed button! "+keyCode, Toast.LENGTH_LONG).show();


        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
//            Toast.makeText(this, "You pressed the home button!", Toast.LENGTH_LONG).show();
            return true;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Toast.makeText(this, "You pressed the BACK button!", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





    private void refresh(MainActivity_Old mainActivity, String selectedLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = mainActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, MainActivity_Old.class);
//            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(refresh);
            finish();
        } else {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = mainActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, MainActivity_Old.class);
//            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(refresh);
            finish();
        }
    }

    private void checkGPS(final MainActivity_Old context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppCompatAlertDialogStyle));
            // Setting Dialog Title
            // dialog.setTitle("GPS settings");
            dialog.setTitle(Html.fromHtml("<font color='#000000'>Location</font>"));

            // Setting Dialog Message
            dialog.setMessage("Allow BookeeyMerchant to access this device's location. Do you want to go to settings menu and enable?");
            dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    sendVersionNumber(batteryStatus);
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private boolean checkLocationPermissionLatest() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity_Old.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }
        return ((float) level / (float) scale) * 100.0f;
    }

    @Override
    public void onProgressUpdate(int progress) {

    }
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity_Old.this,
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

    @Override
    protected void onResume() {
        super.onResume();

        getDeviceIMEI();

        callDeviceStatusUpdateTimerService();


        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        try {
            //Now lets connect to the API
            mGoogleApiClient.connect();
        }catch(Exception e){
        Log.e("Google Client:onResume",""+e.getMessage());
    }





    //To support older version crashing 1.9
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isEnabled = checkLocationPermission();
        }
        ///END


        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MainActivity_Old.this, selectedLanguage);
        }




        Log.e("Merchant logo url: ",""+((CoreApplication) getApplication()).getMerchantLoginRequestResponse().getMerchantLogo());

        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());

        String mx_logo_url =  null;

        if(((CoreApplication) getApplication()).getMerchnat_logo()==null) {



            MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
            Log.e("Merchant logo url: ",""+merchantLoginRequestResponse.getMerchantLogo());

            mx_logo_url  = merchantLoginRequestResponse.getMerchantLogo();

            String login_response = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);

            Log.e("Merchant login resp: ",""+login_response);


            if(mx_logo_url==null) {

                login_response = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);

                Log.e("MX login resp in if: ",""+login_response);

                try {
                    JSONObject login_resp_jo = new JSONObject(login_response);

                    mx_logo_url = login_resp_jo.getString("merchantLogo");

                }catch(Exception e){

                    Log.e("MX log Ex: ",""+e.getMessage());

                }


            }



//            String url =  "https://www.bookeey.com/mno/merlogo/mer160008.jpg";
//            new DownloadImageTask(back_logo).execute(url);

            new DownloadImageTask(back_logo).execute(mx_logo_url);


//            Toast.makeText(MainActivity.this,"Getting merchant logo",Toast.LENGTH_LONG).show();

        }




    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the contacts-related task you need to do.

                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    } else {
                        //If everything went fine lets get latitude and longitude
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
//                         Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
                        convertLatLngToLocation(currentLatitude, currentLongitude);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


//    @Override
//    protected void onPause() {
//        super.onPause();


//        Log.v(this.getClass().getSimpleName(), "onPause()");
//        //Disconnect from API onPause()
//
//
//
//        try {
//            if (mGoogleApiClient.isConnected()) {
//                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//                mGoogleApiClient.disconnect();
//            }
//        }catch(Exception e){
//          Log.e("Google Client:onPause",""+e.getMessage());
//        }


//    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissionForWrite()) {
                Log.i("IF", "if");
                requestPermissionForWrite(123);
            } else {
                Log.i("ELSE", "else");


                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    } else {
                        //If everything went fine lets get latitude and longitude
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
//                        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
                        convertLatLngToLocation(currentLatitude, currentLongitude);
                    }

            }

            //KrypC
//            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if (location == null) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            } else {
//                //If everything went fine lets get latitude and longitude
//                currentLatitude = location.getLatitude();
//                currentLongitude = location.getLongitude();
//                // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
//                convertLatLngToLocation(currentLatitude, currentLongitude);
//            }


        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
//                 Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
                convertLatLngToLocation(currentLatitude, currentLongitude);
            }
        }
    }

    private void requestPermissionForWrite(int i) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity_Old.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {


//            Toast.makeText(MainActivity.this, "Location permission needed. Please allow in App Settings for additional functionality", Toast.LENGTH_LONG).show();


            Toast.makeText(MainActivity_Old.this, getString(R.string.location_permission_needed), Toast.LENGTH_LONG).show();


        } else {
            ActivityCompat.requestPermissions(MainActivity_Old.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, i);
        }
    }

    private boolean checkPermissionForWrite() {
        Log.i("checkPermission", "checkPermissionForLocation");
        int result = ContextCompat.checkSelfPermission(MainActivity_Old.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            // Toast.makeText(getApplicationContext(), "allow gps", Toast.LENGTH_LONG).show();
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        //  Toast.makeText(this, currentLatitude + "  changed WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        if (count == 0) {
            convertLatLngToLocation(currentLatitude, currentLongitude);
            count++;
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
                InputStream in = new URL(urldisplay).openStream();
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


    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String image_string = Base64.encodeToString(b, Base64.DEFAULT);
        CustomSharedPreferences.saveStringData(getBaseContext(), image_string, CustomSharedPreferences.SP_KEY.MERCHANT_LOGO);
        return image_string;
    }



    String regId = "";

    public void sendVersionNumber(String batteryStatus) {

        regId = getRegistrationId(MainActivity_Old.this);
        Log.i(this.toString(), "registration id from shared pref : " + regId);
        String merchant_ref_id = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);



        if (regId.isEmpty()) // if blank, then app is not yet registered
        {
            registerInBackground();
        } else {
            CustomSharedPreferences.saveGCMRegId(MainActivity_Old.this, "" + regId, CustomSharedPreferences.SP_KEY.GCM_REG_ID);

            imeinumber = getUniqueIMEIId();

            CustomSharedPreferences.saveStringData(getBaseContext(), imeinumber, CustomSharedPreferences.SP_KEY.IMEI_NO);

            String currentLocation = CustomSharedPreferences.getStringData(MainActivity_Old.this, CustomSharedPreferences.SP_KEY.CURRENT_LOCATION);

            /*if (checkPermission()) {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                imeinumber = telephonyManager.getDeviceId();
            }*/






            //Send Version Numer COMMENTED TO AVOID KEEP LOADING PROBLEM

//            CoreApplication application = (CoreApplication) getApplication();
//            String uiProcessorReference = application.addUserInterfaceProcessor(new SendingVersionNumberProcessing(imeinumber, regid, merchant_ref_id, batteryStatus, currentLocation, true, application));
//            ProgressDialogFrag progress = new ProgressDialogFrag();
//            Bundle bundle = new Bundle();
//            bundle.putString("uuid", uiProcessorReference);
//            progress.setCancelable(false);
//            progress.setArguments(bundle);
//            progress.show(getFragmentManager(), "progress_dialog");


            initSendVersionNumberUsingAsyncTask(imeinumber, regId, merchant_ref_id, batteryStatus, currentLocation);


        }



    }


    private void initSendVersionNumberUsingAsyncTask(String imeinumber, String gcm_reg_id, String merchant_ref_id, String batteryStatus, String location){


        //SET MORE PARAMS HERE STARTS
        //GET APP VERSION
        PackageInfo p_info;
        double appVersionDouble = 0d;
        try {
            p_info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            appVersionDouble = Double.parseDouble(p_info.versionName);
        } catch (PackageManager.NameNotFoundException e) {

        }

        SendVersionNumber sendVersionNumerber = new SendVersionNumber();
        sendVersionNumerber.setImeiNumber(imeinumber);
        sendVersionNumerber.setGCMRegistrationId(gcm_reg_id);
        sendVersionNumerber.setBatteryStatus(batteryStatus);
        sendVersionNumerber.setCurrentLocation(location);
        sendVersionNumerber.setMerchantRefNumber(merchant_ref_id);
        sendVersionNumerber.setG_transType(TransType.MERCHANTAPPVERSION.name());
        sendVersionNumerber.setCurrentVersionNumber(appVersionDouble);
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.MERCHANTAPPVERSION.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(sendVersionNumerber)));
        String sendVersionFinalUrl = buffer.toString();

        new SendVersionNumberAsyncTask().execute(sendVersionFinalUrl);

    }



    class SendVersionNumberAsyncTask extends AsyncTask<String, Void, String> {

        JSONObject postData;


        public SendVersionNumberAsyncTask( ) {

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        protected String doInBackground(String... urls) {

            String response =  null;

            try {
                // This is getting the url from the string we passed in
                URL url = new URL(urls[0]);

                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestMethod("POST");


                // OPTIONAL - Sets an authorization header
//                urlConnection.setRequestProperty("Authorization", "someAuthString");

                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                     response = convertInputStreamToString(inputStream);

                    // From here you can convert the string to JSON with whatever JSON parser you like to use
                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method

                } else {
                    // Status code is not 200
                    // Do something to handle the error

                    response=   null;
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());


                response = "Exception: "+e.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {

                CustomSharedPreferences.saveStringData(getApplicationContext(), result, CustomSharedPreferences.SP_KEY.SEND_VERSION_RESPONSE);



//              Toast.makeText(MainActivity.this,"SendVersion Resonse: "+result,Toast.LENGTH_LONG).show();
              Log.e("Send version->","Response: "+result);
            }else{

                Toast.makeText(MainActivity_Old.this,"System Error!",Toast.LENGTH_LONG).show();

            }
        }
    }
    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void convertLatLngToLocation(double currentLatitude, double currentLongitude) {

//        Locale locale = new Locale("en", "US");
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

        try {

            String  currentLocation = "";
            List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
                currentLocation = obj.getAddressLine(0);
                Log.v("IGA", "Address" + currentLocation);
                // to get individual objects
                /*add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();*/
//                Log.v("IGA", "Address" + currentLocation);
                //sendVersionNumber(batteryStatus, currentLocation);

                CustomSharedPreferences.saveStringData(MainActivity_Old.this, currentLocation, CustomSharedPreferences.SP_KEY.CURRENT_LOCATION);

            } else {
                //sendVersionNumber(batteryStatus, currentLocation);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    public void callDeviceStatusUpdateTimerService(){

//        startService(new Intent(this, DeviceStatusUpdateService.class));
//        Calendar cal = Calendar.getInstance();
//        Intent intent = new Intent(this, DeviceStatusUpdateService.class);
//        PendingIntent pintent = PendingIntent
//                .getService(this, 0, intent, 0);
//
//        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        // Start service every hour
////        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
////                3600*1000, pintent);
//
//        // Start service every hour
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
//                10*1000, pintent);



//        Intent myIntent = new Intent(getApplicationContext(), DeviceStatusUpdateService.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),  0, myIntent, 0);
//
//        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND, 5); // first time
//        long frequency= 5 * 1000; // in ms
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);

        Util.scheduleJob(getApplicationContext());


    }

    private String getUniqueIMEIId() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(MainActivity_Old.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkPermissionForPhoneState()) {
                        Log.i("IF", "if");
                        requestPermissionForPhone(1002);
                        imeinumber = telephonyManager.getDeviceId();
                    }
                }
                return imeinumber;
            }
            imeinumber = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imeinumber);
            if (imeinumber != null && !imeinumber.isEmpty()) {
                return imeinumber;
            } else {
                return Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeinumber;
    }

    private void requestPermissionForPhone(int i) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity_Old.this, Manifest.permission.READ_PHONE_STATE)) {
//            final Toast toast = Toast.makeText(MainActivity.this, "Phone read permission needed. Please allow in App Settings for additional functionality", Toast.LENGTH_LONG);

            final Toast toast = Toast.makeText(MainActivity_Old.this, getString(R.string.phone_read_permission_needed), Toast.LENGTH_LONG);


            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 1000);
        } else {
            ActivityCompat.requestPermissions(MainActivity_Old.this, new String[]{Manifest.permission.READ_PHONE_STATE}, i);
        }
    }

    private boolean checkPermissionForPhoneState() {
        Log.i("checkPermission", "checkPermissionForPhoneState");
        int result = ContextCompat.checkSelfPermission(MainActivity_Old.this, Manifest.permission.READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos,
                                long id) {

            MainMenuListViewAdapter.ViewHolder holder = (MainMenuListViewAdapter.ViewHolder) view.getTag();
            switch (holder.tag) {
                case collect_payment_mtag:


                    CustomSharedPreferences.saveIntData(getApplicationContext(), 0, CustomSharedPreferences.SP_KEY.KEY_CAMERA_FACING);


//                    Original code
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        boolean isEnabled = checkLocationPermissionLatest();
                        if (isEnabled) {
                            intent = new Intent(getBaseContext(), CaptureActivity.class);
                            intent.setAction("br.com.google.zxing.client.android.SCAN");
                            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                            intent.putExtra("CHARACTER_SET", "ISO-8859-1");
                            startActivityForResult(intent, 0);
                        }
                    } else {
                        intent = new Intent(getBaseContext(), CaptureActivity.class);
                        intent.setAction("br.com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                        intent.putExtra("CHARACTER_SET", "ISO-8859-1");
                        startActivityForResult(intent, 0);
                    }




//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setMessage("Choose the scanner")
//                            .setCancelable(false)
//                            .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                    //Camera scan
//                                    Intent intent;
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                                        boolean isEnabled = checkLocationPermissionLatest();
//                                        if (isEnabled) {
//                                            intent = new Intent(getBaseContext(), CaptureActivity.class);
//                                            intent.setAction("br.com.google.zxing.client.android.SCAN");
//                                            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//                                            intent.putExtra("CHARACTER_SET", "ISO-8859-1");
//                                            startActivityForResult(intent, 0);
//                                        }
//                                    } else {
//                                        intent = new Intent(getBaseContext(), CaptureActivity.class);
//                                        intent.setAction("br.com.google.zxing.client.android.SCAN");
//                                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//                                        intent.putExtra("CHARACTER_SET", "ISO-8859-1");
//                                        startActivityForResult(intent, 0);
//                                    }
//
//
//                                }
//                            })
//                            .setNegativeButton("Scanner", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//
//                                    try {
//
//                                        // get prompts.xml view
//                                        LayoutInflater li = LayoutInflater.from(MainActivity.this);
//                                        View promptsView = li.inflate(R.layout.prompts, null);
//
//                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                                                MainActivity.this);
//
//                                        // set prompts.xml to alertdialog builder
//                                        alertDialogBuilder.setView(promptsView);
//
//
//                                        // create alert dialog
//                                        final AlertDialog alertDialog = alertDialogBuilder.create();
//
//                                        final EditText userInput = (EditText) promptsView
//                                                .findViewById(R.id.editTextDialogUserInput);
//
//                                        userInput.setOnEditorActionListener(
//                                                new EditText.OnEditorActionListener() {
//                                                    @Override
//                                                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                                                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                                                                actionId == EditorInfo.IME_ACTION_DONE ||
//                                                                event != null &&
//                                                                        event.getAction() == KeyEvent.ACTION_DOWN &&
//                                                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                                                            if (event == null || !event.isShiftPressed()) {
//                                                                // the user is done typing.
////                                                                Toast.makeText(MainActivity.this, "Typing Done " + v.getText().toString(), Toast.LENGTH_LONG).show();
//
//                                                                String url = v.getText().toString();
//                                                                CoreApplication application = (CoreApplication) getApplication();
//                                                                String uiProcessorReference = application.addUserInterfaceProcessor(new DecodeIncomingQrPaymentCode(url));
//                                                                ProgressDialogFrag progress = new ProgressDialogFrag();
//                                                                Bundle bundle = new Bundle();
//                                                                bundle.putString("uuid", uiProcessorReference);
//                                                                progress.setCancelable(true);
//                                                                progress.setArguments(bundle);
//                                                                progress.show(getFragmentManager(), "progress_dialog");
//
//
//                                                                alertDialog.dismiss();
//
//                                                                return true; // consume.
//                                                            }
//                                                        }
//                                                        return false; // pass on to other listeners.
//                                                    }
//                                                }
//                                        );
//
//
//
//                                        // show it
//                                        alertDialog.show();
//
//                                    }catch (Exception e){
//
//                                        Toast.makeText(MainActivity.this, "Exception " + e.getMessage(), Toast.LENGTH_LONG).show();
//
//                                    }
//
//
//
//                                }
//                            });
//                    AlertDialog alert = builder.create();
//                    alert.show();



                    break;
                case consolidated_reports:
                    intent = new Intent(getBaseContext(), Consolidated_Reports_Selection.class);
                    startActivity(intent);
                    break;
                case online_payments_mtag:
                    Toast.makeText(MainActivity_Old.this, "Online Payments", Toast.LENGTH_LONG).show();
                    break;
                case collect_payment_history_mtag:
                    ((CoreApplication) getApplication()).setTransactionHistoryResponse(new TransactionHistoryResponse());
                    intent = new Intent(getBaseContext(), TransactionHistoryActivity.class);
                    startActivity(intent);
                    break;

                case redeem_offer:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        boolean isEnabled = checkLocationPermissionLatest();
                        if (isEnabled) {
                            Log.i("IF", "if");
                            intent = new Intent(getBaseContext(), CaptureActivity.class);
                            intent.setAction("br.com.google.zxing.client.android.SCAN");
                            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                            intent.putExtra("CHARACTER_SET", "ISO-8859-1");
                            startActivityForResult(intent, 1);
                        }
                    } else {
                        intent = new Intent(getBaseContext(), CaptureActivity.class);
                        intent.setAction("br.com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                        intent.putExtra("CHARACTER_SET", "ISO-8859-1");
                        startActivityForResult(intent, 1);
                    }

                    break;
                case invoice:
                    intent = new Intent(getBaseContext(), InvoiceActivity.class);
                    startActivity(intent);
                    break;
                case offers:
                    getOffers();
                    /*intent = new Intent(getBaseContext(), InvoiceActivity.class);
                    startActivity(intent);*/
                    break;
                default:
                    break;
            }
        }
    };

    private void getOffers() {
        CoreApplication application = (CoreApplication) getApplication();
        String merchant_ref_id = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
        OfferPreviewRequest offerPreviewRequest = new OfferPreviewRequest();
        offerPreviewRequest.setMerchantId(merchant_ref_id);
        String uiProcessorReference = application.addUserInterfaceProcessor(new OffersProcessing(offerPreviewRequest, application, true));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(true);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }


    public void getDeviceIMEI(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            imeinumber = getUniqueIMEIId();

//            Toast.makeText(MainActivity.this, "Main 2. "+imeinumber,Toast.LENGTH_LONG).show();


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getBaseContext() != null) {
            switch (requestCode) {
                case 0: {
                    if (resultCode == RESULT_OK) {

                        //Setting language
                        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
                            LocaleHelper.setLocale(MainActivity_Old.this, selectedLanguage);
                        }
                        //@end

                        String url = data.getStringExtra("SCAN_RESULT");
                        CoreApplication application = (CoreApplication) getApplication();
                        String uiProcessorReference = application.addUserInterfaceProcessor(new DecodeIncomingQrPaymentCode(url));
                        ProgressDialogFrag progress = new ProgressDialogFrag();
                        Bundle bundle = new Bundle();
                        bundle.putString("uuid", uiProcessorReference);
                        progress.setCancelable(true);
                        progress.setArguments(bundle);
                        progress.show(getFragmentManager(), "progress_dialog");
                    }else {


                        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
                            LocaleHelper.setLocale(MainActivity_Old.this, selectedLanguage);
                        }

                    }

                }
                break;
                case 1: {
                    if (resultCode == RESULT_OK) {

                        //Setting language
                        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
                            LocaleHelper.setLocale(MainActivity_Old.this, selectedLanguage);
                        }
                        //@end

                        String qr_data = data.getStringExtra("SCAN_RESULT");
                        CoreApplication application = (CoreApplication) getApplication();
                        String uiProcessorReference = application.addUserInterfaceProcessor(new QRCodeOfferRedeemValidateProcessing(qr_data, application));
                        ProgressDialogFrag progress = new ProgressDialogFrag();
                        Bundle bundle = new Bundle();
                        bundle.putString("uuid", uiProcessorReference);
                        progress.setCancelable(true);
                        progress.setCancelable(true);
                        progress.setArguments(bundle);
                        progress.show(getFragmentManager(), "progress_dialog");
                    }else{

                        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
                            LocaleHelper.setLocale(MainActivity_Old.this, selectedLanguage);
                        }
                    }
                }
                break;
            }

        }
    }

    public void onBarcodeDecoded(DecodedQrPojo decodedQrPojo) {
        if (null != decodedQrPojo) {

            Intent proceedIntent = new Intent(this, DisplayAmountToMerchantActivityDummy.class);
            proceedIntent.putExtra("data", decodedQrPojo);
            startActivity(proceedIntent);


//            Toast.makeText(MainActivity.this," "+decodedQrPojo.getBcodeHeaderEncoder().amount,Toast.LENGTH_LONG).show();


            //Direct to L2 on Apr/17/2019
//            PayToMerchantRequest payToMerchantRequest = new PayToMerchantRequest();
//            payToMerchantRequest.setAmount(decodedQrPojo.getBcodeHeaderEncoder().amount);
//            payToMerchantRequest.setCustomerId(Hex.toHex(decodedQrPojo.getBcodeHeaderEncoder().staticId));
//            payToMerchantRequest.setBarcodeData(Hex.toHex(decodedQrPojo.getCompleteCode()));
//            String timezone= ((CoreApplication)getApplication()).getGenericResponse().getG_servertime();
//            Calendar cal= null;
//            if(timezone!=null) {
//                cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
//            }else{
//                cal= Calendar.getInstance(TimeZone.getDefault());
//            }
//            long currentLocalTime = cal.getTimeInMillis();
//            payToMerchantRequest.setClientDate(currentLocalTime);
//
//            //Code to start server thread and display the progress fragment dialogue (retained)
//            CoreApplication application = (CoreApplication) getApplication();
//            String uiProcessorReference = application.addUserInterfaceProcessor(new PayToMerchantPhase1Processing(payToMerchantRequest, application, true));
//            ProgressDialogFrag progress = new ProgressDialogFrag();
//            Bundle bundle = new Bundle();
//            bundle.putString("uuid", uiProcessorReference);
//            progress.setCancelable(true);
//            progress.setArguments(bundle);
//            progress.show(getFragmentManager(), "progress_dialog");

        }
    }


    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(this.toString(), "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(this.toString(), "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(this.toString(), "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity_Old.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask<String, Void, String>() {


            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity_Old.this);
                    }
                    try {
                        regId = gcm.register(SENDER_ID);


                        if(regId==null){
                            Log.d("No registration Id->", " "+regId);
                        }else{
                            Log.d("Got registration Id->", " "+regId);
                        }

                    } catch (Exception e) {

                        Log.d("No registration Id: ", " "+e.getMessage());


                        e.printStackTrace();
                    }
                    Log.d(TAG, "########################################");
                    Log.d(TAG, "Current Device's Registration ID is: " + msg);
                    Log.i(this.toString(), "regId = " + regId);
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    // sendRegistrationIdToBackend();
                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.
                    // Persist the regID - no need to register again.
                    storeRegistrationId(MainActivity_Old.this, regId);
                    msg = regId;
                } catch (Exception ex) {
//                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    msg="";
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {


                //Rahman
//                Toast.makeText(MainActivity.this,"GCM Id:"+msg,Toast.LENGTH_LONG).show();


                // setting registration id in edit text.
                CustomSharedPreferences.saveGCMRegId(MainActivity_Old.this, msg, CustomSharedPreferences.SP_KEY.GCM_REG_ID);
                String currentLocation = CustomSharedPreferences.getStringData(MainActivity_Old.this, CustomSharedPreferences.SP_KEY.CURRENT_LOCATION);


                // String regid_reg_id = getRegistrationId(MainActivity.this);

                String merchant_ref_id = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
                String imeinumber = null;
                if (checkPermission()) {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    imeinumber = telephonyManager.getDeviceId();
                }



                //Send Version Numer
//                CoreApplication application = (CoreApplication) getApplication();
//                String uiProcessorReference = application.addUserInterfaceProcessor(new SendingVersionNumberProcessing(imeinumber, msg, merchant_ref_id, batteryStatus, currentLocation, true, application));
//                ProgressDialogFrag progress = new ProgressDialogFrag();
//                Bundle bundle = new Bundle();
//                bundle.putString("uuid", uiProcessorReference);
//                progress.setCancelable(false);
//                progress.setArguments(bundle);
//                progress.show(getFragmentManager(), "progress_dialog");



                //To void blocking operation
                initSendVersionNumberUsingAsyncTask(imeinumber, regId, merchant_ref_id, batteryStatus, currentLocation);


            }

        }.execute(null, null, null);
    }

    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(MainActivity_Old.this, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(this.toString(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }





}
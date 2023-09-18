package wallet.ooredo.com.live.mainmenu;

import android.Manifest;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.consolidatedreports.Consolidated_Reports_Selection;
import wallet.ooredo.com.live.invoice.InvoiceActivity;
import wallet.ooredo.com.live.transactionhistory.TransactionHistoryActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.offerredeem.OfferPreviewRequest;
import ycash.wallet.json.pojo.transactionhistory.TransactionHistoryResponse;

public class MainActivity_Old extends MainGenericActivity implements YPCHeadlessCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    List<MainMenuGridInfo> listAppInfo = new ArrayList<MainMenuGridInfo>();
    MainMenuListViewAdapter processAdapter;
    private static final int collect_payment_mtag = 1006;
    private static final int collect_payment_history_mtag = 1007;
    private static final int online_payments_mtag = 1008;
    private static final int consolidated_reports = 1010;
    private static final int redeem_offer = 1011;
    private static final int invoice = 1012;
    private static final int offers = 1013;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    TextView main_menu_branch_name__bottom_tiltle, main_menu_merchant_refid__bottom_tiltle;
    String imeinumber = null;
    String selectedLanguage = null;
    public static final String KEY_CAMERA_FACING = "KEY_CAMERA_FACING";
    public static final int REQUEST_READ_PHONE_STATE = 10011;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ListView listMain = (ListView) findViewById(R.id.listMain);

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

        //Language refresh
        final LinearLayout language_layout = (LinearLayout) findViewById(R.id.language_layout);
        final ImageView coutry_flag_img = (ImageView) findViewById(R.id.coutry_flag_img);
        final TextView language_text = (TextView) findViewById(R.id.language_text);

        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MainActivity_Old.this, selectedLanguage);
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
            int camerFace = CustomSharedPreferences.getIntData(getApplicationContext(), CustomSharedPreferences.SP_KEY.KEY_CAMERA_FACING);
            intentCapture.putExtra(KEY_CAMERA_FACING, camerFace);
            startActivityForResult(intentCapture, 0);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
//           Toast.makeText(this, "You pressed the home button!", Toast.LENGTH_LONG).show();
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

        //To support older version crashing 1.9
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isEnabled = checkLocationPermission();
        }
        ///END

        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MainActivity_Old.this, selectedLanguage);
        }

        Log.e("Merchant logo url: ", "" + ((CoreApplication) getApplication()).getMerchantLoginRequestResponse().getMerchantLogo());

        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());

        String mx_logo_url = null;

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
            new DownloadImageTask(back_logo).execute(mx_logo_url);
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
//                                        LayoutInflater li = LayoutInflater.from(MainActivity.this);
//                                        View promptsView = li.inflate(R.layout.prompts, null);
//
//                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                                                MainActivity.this);
//
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


    public void getDeviceIMEI() {
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
                    } else {


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
                    } else {

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

    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(MainActivity_Old.this, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
package wallet.ooredo.com.live.mainmenu;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import wallet.ooredo.com.live.BuildConfig;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.barcodepaymentcollection.DisplayAmountToMerchantActivityDummy;
import wallet.ooredo.com.live.consolidatedreports.Consolidated_Reports_Selection;
import wallet.ooredo.com.live.invoice.InvoiceActivity;
import wallet.ooredo.com.live.transactionhistory.TransactionHistoryActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;
import wallet.ooredo.com.live.utils.NFCUtils;
import ycash.wallet.json.pojo.decodedbarcode.DecodedQrPojo;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.offerredeem.OfferPreviewRequest;
import ycash.wallet.json.pojo.transactionhistory.TransactionHistoryResponse;

public class MainActivity extends MainGenericActivity implements YPCHeadlessCallback {
    public static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final String KEY_CAMERA_FACING = "KEY_CAMERA_FACING";
    private static final int collect_payment_mtag = 1006;
    private static final int collect_payment_history_mtag = 1007;
    private static final int online_payments_mtag = 1008;
    private static final int consolidated_reports = 1010;
    private static final int redeem_offer = 1011;
    private static final int invoice = 1012;
    private static final int offers = 1013;
    String versionname;
    List<MainMenuGridInfo> listAppInfo = new ArrayList<>();
    MainMenuListViewAdapter processAdapter;
    TextView main_menu_branch_name__bottom_tiltle, main_menu_merchant_refid__bottom_tiltle;
    String selectedLanguage = null;

    private AppUpdateManager mAppUpdateManager;
    private final AdapterView.OnItemClickListener mItemClickListener = (parent, view, pos, id) -> {
        MainMenuListViewAdapter.ViewHolder holder = (MainMenuListViewAdapter.ViewHolder) view.getTag();
        Intent intent;
        switch (holder.tag) {
            case collect_payment_mtag:
                //intent = new Intent(MainActivity.this,ScannedBarcodeActivity.class);
               // startActivityForResult(intent, 0);
                //startActivityForResult(new Intent(MainActivity.this, ScannedBarcodeActivity.class), 0);
                CustomSharedPreferences.saveIntData(getApplicationContext(), 0, CustomSharedPreferences.SP_KEY.KEY_CAMERA_FACING);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean isEnabled = checkCameraPermissionLatest();
                    Log.i("isEnabled", "-" + isEnabled);
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
                break;
            case consolidated_reports:
                intent = new Intent(getBaseContext(), Consolidated_Reports_Selection.class);
                startActivity(intent);
                break;
            case online_payments_mtag:
                Toast.makeText(MainActivity.this, "Online Payments", Toast.LENGTH_LONG).show();
                break;
            case collect_payment_history_mtag:
                ((CoreApplication) getApplication()).setTransactionHistoryResponse(new TransactionHistoryResponse());
                intent = new Intent(getBaseContext(), TransactionHistoryActivity.class);
                startActivity(intent);
                break;
            case redeem_offer:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean isEnabled = checkCameraPermissionLatest();
                    Log.e("isEnabled", "-" + isEnabled);
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
                break;
            default:
                break;
        }
    };

    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.INSTALLED) {
                if (mAppUpdateManager != null) {
                    mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                }
            } else {
                Log.i("Check update", "InstallStateUpdatedListener: state: " + state.installStatus());
            }
        }
    };

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
        getActionBar().setTitle(R.string.main_menu);
        View cView = getLayoutInflater().inflate(R.layout.actionbar, null);
        TextView title_text = (TextView) cView.findViewById(R.id.title_text);
        title_text.setText(R.string.main_menu);
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
            CustomSharedPreferences.saveStringData(MainActivity.this, authtoken, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        } else {
            CustomSharedPreferences.saveStringData(MainActivity.this, authtoken, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        }
//        mGridMain = (GridView) findViewById(R.id.gvMain);
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
            LocaleHelper.setLocale(MainActivity.this, selectedLanguage);
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
        language_layout.setOnClickListener(v -> {
            if (language_text.getText().toString().equals("English")) {
                language_text.setText(getResources().getString(R.string.login_arabic));
                selectedLanguage = "en";
                coutry_flag_img.setImageResource(R.drawable.kuwait);
                CustomSharedPreferences.saveStringData(getApplicationContext(), "en", CustomSharedPreferences.SP_KEY.LANGUAGE);
                LocaleHelper.setLocale(MainActivity.this, "en");
                refresh(MainActivity.this, "en");
            } else {
                selectedLanguage = "ar";
                language_text.setText(getResources().getString(R.string.login_english));
                coutry_flag_img.setImageResource(R.drawable.usa);
                CustomSharedPreferences.saveStringData(getApplicationContext(), "ar", CustomSharedPreferences.SP_KEY.LANGUAGE);
                LocaleHelper.setLocale(MainActivity.this, "ar");
                refresh(MainActivity.this, "ar");
            }
        });
        versionname = BuildConfig.VERSION_NAME;
        if (!((CoreApplication) getApplication()).isPOS()) {
            VersionChecker versionChecker = new VersionChecker();
            versionChecker.execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsView = li.inflate(R.layout.custom_update_playstore_dialog, null);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setView(promptsView);
                alertDialog.setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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

    private void refresh(MainActivity mainActivity, String selectedLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);
            Resources resources = mainActivity.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            Intent refresh = new Intent(this, MainActivity.class);
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
            Intent refresh = new Intent(this, MainActivity.class);
//            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(refresh);
            finish();
        }
    }

    private boolean checkCameraPermissionLatest() {
        Log.e("permission.CAMERA", "-" + ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onProgressUpdate(int progress) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            LocaleHelper.setLocale(MainActivity.this, selectedLanguage);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", requestCode + "-" + resultCode + "-" + data);
        if (getBaseContext() != null) {
            switch (requestCode) {
                case 0: {
                    if (resultCode == RESULT_OK) {
                        //Setting language
                        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
                            LocaleHelper.setLocale(MainActivity.this, selectedLanguage);
                        }
                        //@end
                        Log.e("inside", "QR code");
                        if(data != null && data.getStringExtra("SCAN_RESULT") != null) {
                            String url = data.getStringExtra("SCAN_RESULT");
                            Log.e("inside", "" + url);
                            CoreApplication application = (CoreApplication) getApplication();
                            String uiProcessorReference = application.addUserInterfaceProcessor(new DecodeIncomingQrPaymentCode(url));
                            ProgressDialogFrag progress = new ProgressDialogFrag();
                            Bundle bundle = new Bundle();
                            bundle.putString("uuid", uiProcessorReference);
                            progress.setCancelable(true);
                            progress.setArguments(bundle);
                            progress.show(getFragmentManager(), "progress_dialog");
                        }
                    } else {
                        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
                            LocaleHelper.setLocale(MainActivity.this, selectedLanguage);
                        }
                    }
                }
                break;
                case 1: {
                    if (resultCode == RESULT_OK) {
                        //Setting language
                        selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
                            LocaleHelper.setLocale(MainActivity.this, selectedLanguage);
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
                            LocaleHelper.setLocale(MainActivity.this, selectedLanguage);
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("1.onNewIntent", "onNewIntent");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            List<String> msgs = NFCUtils.getStringsFromNfcIntent(intent);
//            Toast.makeText(this, "Message received : "+msgs.get(0), Toast.LENGTH_LONG).show();
            String url = msgs.get(0);
            CoreApplication application = (CoreApplication) getApplication();
            String uiProcessorReference = application.addUserInterfaceProcessor(new DecodeIncomingQrPaymentCode(url));
            ProgressDialogFrag progress = new ProgressDialogFrag();
            Bundle bundle = new Bundle();
            bundle.putString("uuid", uiProcessorReference);
            progress.setCancelable(true);
            progress.setArguments(bundle);
            progress.show(getFragmentManager(), "progress_dialog");
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

    public class VersionChecker extends AsyncTask<String, String, String> {
        private String newVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//        showIfNotVisible("");
        }

        @Override
        protected void onPostExecute(String latestVersion) {
            Log.e("Version", "" + latestVersion);
            if (latestVersion != null && !latestVersion.isEmpty()) {
                double live_version = Double.parseDouble(latestVersion);
                double local_version = Double.parseDouble(versionname);
                if (local_version < live_version) {
                    LayoutInflater li = LayoutInflater.from(MainActivity.this);
                    View promptsView = li.inflate(R.layout.custom_update_playstore_dialog, null);
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setView(promptsView);
                    alertDialog.setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }
    }
}
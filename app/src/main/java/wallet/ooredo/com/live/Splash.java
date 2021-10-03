package wallet.ooredo.com.live;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.FacebookSdk;
import com.google.gson.Gson;

import coreframework.database.CustomSharedPreferences;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.merchantlogin.MerchantLoginActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;
import wallet.ooredo.com.live.utils.PrintManagerSDK;
import wangpos.sdk4.libbasebinder.Printer;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
/**
 * Code Modified (Mohit) for login merchant in case of restart of device if login response is available in persistent space.
 */
public class Splash extends Activity {
    PackageInfo p_info;
    double d2_update = 0d;
    private Printer mPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    mPrinter = new Printer(getApplicationContext());
                } finally {
                    ((CoreApplication) getApplication()).setPOS(checkPrinterStatus());
                    // Log.e("POS Device", "" + ((CoreApplication) getApplication()).isPOS());
                }
            }
        };
        thread.start();
        if (Build.MODEL.contains("i9100") || Build.MODEL.contains("i9000s") || Build.MODEL.contains("i9100/W")) {
            ((CoreApplication) getApplication()).setNewPOS(true);
        }
        Log.e("POS Device", "" + ((CoreApplication) getApplication()).isNewPOS());
        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            p_info = ((CoreApplication) getApplication()).getPackageManager().getPackageInfo(((CoreApplication) getApplication()).getPackageName(), 0);
            d2_update = Double.parseDouble(p_info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Setting language
                String selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
                    LocaleHelper.setLocale(Splash.this, selectedLanguage);
                }
                boolean isLoggedIn = ((CoreApplication) getApplication()).isUserLoggedIn();
                Log.e("isLoggedIn", "" + isLoggedIn);
                Log.e("isLoggedIn", "" + isLoggedIn);
                if (isLoggedIn) {
                    Intent in = new Intent(Splash.this, wallet.ooredo.com.live.mainmenu.MainActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    //Check persistent login response:
                    //CustomSharedPreferences.saveStringData(getBaseContext(),"",CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
                    String login_response = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
                    if (login_response.equalsIgnoreCase(CustomSharedPreferences.SIMPLE_NULL)) {
                        Intent in = new Intent(Splash.this, MerchantLoginActivity.class);
                        startActivity(in);
                    } else {
                        //A Session Is Available In Persistent Storage,
                        //Check if it is still valid, if valid set the session to active object session and go to main menu
                        //else clean persistence and go to login screen
                        //BYPASS CORE LOGIC
                        //The Complex Accurate Logic Can Be Ignored as the once global wrapper shall logout user in case of server invalid token response:
                        //Code could be streamlined later based on analysis and test results:
                        MerchantLoginRequestResponse merchantLoginRequestResponse = new Gson().fromJson(login_response, MerchantLoginRequestResponse.class);
                        ((CoreApplication) getApplication()).setMerchantLoginRequestResponse(merchantLoginRequestResponse);
						//Merchant Logo data

						//new DownloadImageTask().execute(merchantLoginRequestResponse.getMerchantLogo());
                        ((CoreApplication) getApplication()).setIsUserLoggedIn(true);
                        Intent in = new Intent(Splash.this, wallet.ooredo.com.live.mainmenu.MainActivity.class);
                        startActivity(in);
                    }
                    finish();
                }
            }
        }, 3000);
    }

    public boolean checkPrinterStatus() {
        int[] status = new int[1];
        int ret = -1;
        try {
            ret = mPrinter.getPrinterStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret != -1;
    }
}
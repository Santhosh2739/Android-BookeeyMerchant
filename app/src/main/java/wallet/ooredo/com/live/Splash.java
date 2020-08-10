package wallet.ooredo.com.live;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.URL;

import coreframework.database.CustomSharedPreferences;
import coreframework.utils.CustomizedExceptionHandler;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import wallet.ooredo.com.live.merchantlogin.MerchantLoginActivity;
import wallet.ooredo.com.live.merchantregistration.MerchantRegistrationActivity;
import wallet.ooredo.com.live.utils.LocaleHelper;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;

/**
 * Code Modified (Mohit) for login merchant in case of restart of device if login response is available in persistent space.
 */
public class Splash extends Activity {
    PackageInfo p_info;
    double d1_old = 0d;
    double d2_update = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);


        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());



        CoreApplication application = (CoreApplication) getApplication();

        //To store logs in custom loation in your phone
        /*Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler(
                "/mnt/sdcard/"));*/
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

                Log.e("isLoggedIn",""+isLoggedIn);
                Log.e("isLoggedIn",""+isLoggedIn);



                if (isLoggedIn) {
                    Intent in = new Intent(Splash.this, wallet.ooredo.com.live.mainmenu.MainActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    //Check persistent login response:
                    //CustomSharedPreferences.saveStringData(getBaseContext(),"",CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
                    String login_response = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
                    if (login_response.equalsIgnoreCase(CustomSharedPreferences.SIMPLE_NULL)) {
                        //No Data Present




                        //Take the user to login activity
                        Intent in = new Intent(Splash.this, MerchantLoginActivity.class);
                        startActivity(in);
                        finish();



//                        May 17 2020
//                        Intent in = new Intent(Splash.this, MerchantRegistrationActivity.class);
//                        startActivity(in);
//                        finish();


                    } else {
                        //A Session Is Available In Persistent Storage,
                        //Check if it is still valid, if valid set the session to active object session and go to main menu
                        //else clean persistence and go to login screen

                        //BYPASS CORE LOGIC
                        //The Complex Accurate Logic Can Be Ignored as the once global wrapper shall logout user in case of server invalid token response:
                        //Code could be streamlined later based on analysis and test results:
                        MerchantLoginRequestResponse merchantLoginRequestResponse = new Gson().fromJson(login_response, MerchantLoginRequestResponse.class);
                        ((CoreApplication) getApplication()).setMerchantLoginRequestResponse(merchantLoginRequestResponse);


//						//Merchant Logo data
//
//						new DownloadImageTask().execute(merchantLoginRequestResponse.getMerchantLogo());

                        ((CoreApplication) getApplication()).setIsUserLoggedIn(true);
                        Intent in = new Intent(Splash.this, wallet.ooredo.com.live.mainmenu.MainActivity.class);
                        startActivity(in);
                        finish();
                    }
                }
            }
        }, 3000);
    }


//	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//
//		public DownloadImageTask() {
//
//
//		}
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
////            pd.show();
//		}
//
//		protected Bitmap doInBackground(String... urls) {
//			String urldisplay = urls[0];
//			Bitmap mIcon11 = null;
//			try {
//				InputStream in = new java.net.URL(urldisplay).openStream();
//				mIcon11 = BitmapFactory.decodeStream(in);
//			} catch (Exception e) {
//				Log.e("Error", e.getMessage());
//				e.printStackTrace();
//			}
//			return mIcon11;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap result) {
//			super.onPostExecute(result);
//			((CoreApplication) getApplication()).setMerchnat_logo(result);
//
//		}
//	}

}
package wallet.ooredo.com.live.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import coreframework.database.CustomSharedPreferences;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import ycash.wallet.json.pojo.pushnotification.SendVersionNumber;

public class ServiceToUpdateDeviceInfo extends Service {
//    public static final int notify = 5000;
    public static final int notify = 20*60*1000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
//        Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show();
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast
//                    Toast.makeText(ServiceToUpdateDeviceInfo.this, "Service is running", Toast.LENGTH_SHORT).show();
                    sendVersionNumber();
                }
            });
        }
    }
    private int getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return (int)Math.round(50.0f);
        }

        float battery_float = ((float) level / (float) scale) * 100.0f;
        int battery_float_int = (int)Math.round(battery_float);
        return battery_float_int;
    }

    public void sendVersionNumber() {

        String merchant_ref_id = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);

        String currentLocation = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.CURRENT_LOCATION);

        String batteryStatus =  Integer.toString(getBatteryLevel());

        String regId = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.GCM_REG_ID);

        String imeinumber = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.IMEI_NO);

        Log.e("LocationOct15,OldDevice",merchant_ref_id+" - "+currentLocation+" - "+batteryStatus+" - "+imeinumber+" - "+"\n\n"+regId);

        initSendVersionNumberUsingAsyncTask(imeinumber, regId, merchant_ref_id, batteryStatus, currentLocation);


    }

    private void initSendVersionNumberUsingAsyncTask(String imeinumber, String gcm_reg_id, String merchant_ref_id, String batteryStatus, String location) {


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

        Log.e("SendVersionCall", "Data: " + new Gson().toJson(sendVersionNumerber));

        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(sendVersionNumerber)));
        String sendVersionFinalUrl = buffer.toString();

        new SendVersionNumberAsyncTask().execute(sendVersionFinalUrl);

    }


    class SendVersionNumberAsyncTask extends AsyncTask<String, Void, String> {

        JSONObject postData;


        public SendVersionNumberAsyncTask() {

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        protected String doInBackground(String... urls) {

            String response = null;

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

                if (statusCode == 200) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    response = convertInputStreamToString(inputStream);

                    // From here you can convert the string to JSON with whatever JSON parser you like to use
                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method

                } else {
                    // Status code is not 200
                    // Do something to handle the error

                    response = null;
                }

            } catch (Exception e) {
                Log.d("Service", e.getLocalizedMessage());


                response = "Exception: " + e.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {

                CustomSharedPreferences.saveStringData(getApplicationContext(), result, CustomSharedPreferences.SP_KEY.SEND_VERSION_RESPONSE);


//                Toast.makeText(getApplicationContext(), "SendVersion Resonse: " + result, Toast.LENGTH_LONG).show();
                Log.e("Send version->", "Response: " + result);
            } else {

                Toast.makeText(getApplicationContext(), "System Error!", Toast.LENGTH_LONG).show();

            }
        }
    }


    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
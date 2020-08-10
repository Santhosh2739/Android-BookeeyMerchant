package wallet.ooredo.com.live.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
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

import coreframework.database.CustomSharedPreferences;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import ycash.wallet.json.pojo.pushnotification.SendVersionNumber;

public class SchedulerJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {

//        Intent service = new Intent(getApplicationContext(), DeviceStatusUpdateService.class);
//        getApplicationContext().startService(service);


        Log.e("Scheduled Jod", "Invoked " + System.currentTimeMillis());
        sendVersionNumber();

        Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


    public void sendVersionNumber() {

        String merchant_ref_id = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);

        String currentLocation = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.CURRENT_LOCATION);

        String batteryStatus =  Integer.toString(getBatteryLevel());

        String regId = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.GCM_REG_ID);

        String imeinumber = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.IMEI_NO);

        Log.e("LocationOct15,Newdevice",merchant_ref_id+" - "+currentLocation+" - "+batteryStatus+" - "+imeinumber+" - "+"\n\n"+regId);

        initSendVersionNumberUsingAsyncTask(imeinumber, regId, merchant_ref_id, batteryStatus, currentLocation);


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
                Log.d(TAG, e.getLocalizedMessage());


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
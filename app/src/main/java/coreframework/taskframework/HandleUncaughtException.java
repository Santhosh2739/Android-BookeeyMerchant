package coreframework.taskframework;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import wallet.ooredo.com.live.BuildConfig;

public class HandleUncaughtException implements Thread.UncaughtExceptionHandler {
    private static Activity myContext = null;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public HandleUncaughtException(Activity context) {
        myContext = context;
    }

    //*********************************************************
    public void reportLogs(final String errorLogs) {
        Log.e("custom error", errorLogs.toString());

        //1.Open Send log activity
        Intent i = new Intent(myContext, SendLogReportsActivity.class);
        i.putExtra("logs", errorLogs);
        myContext.startActivity(i);
        myContext.finish();

        //2.store the logs in internal storage(enable if requires)
//        writeToFile(errorLogs);

        //3.Remote logs to Bugfender
//        String mref = CustomSharedPreferences.getStringData(myContext, CustomSharedPreferences.SP_KEY.USERNAME);
//        writeLogsToBugfender(mref,errorLogs);

        //HyperLog
//        String mref = CustomSharedPreferences.getStringData(myContext, CustomSharedPreferences.SP_KEY.USERNAME);
//        HyperLog.d("Mer: "+mref,errorLogs);



        //Send mail to developer
//        sendMailToDeveloper(errorLogs);

    }

    public void writeLogsToBugfender(String merchantName,String logs){

//        Bugfender.d(merchantName, logs);

//        HyperLog.setURL("API URL");
//
//        File file = HyperLog.getDeviceLogsInFile(myContext);
//
//        HyperLog.pushLogs(myContext, false, new HLCallback() {
//            @Override
//            public void onSuccess(@NonNull Object response) {
//
//            }
//
//            @Override
//            public void onError(@NonNull HLErrorResponse HLErrorResponse) {
//
//            }
//
//
//        });



    }

    private void sendMailToDeveloper(String logs) {
        if (logs != null)
        {


            String mailto = "mailto:tech2@bookeey.com" +
                    "?cc=" + "tech2@bookeey.com" +
                    "&subject=" + Uri.encode("Error reported from BMerchant") +
                    "&body=" + Uri.encode("Log file attached." + logs);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));

            try {
                myContext. startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                //TODO: Handle case where no email app is available
            }

            myContext.finish();
    }
    }

    private void writeToFile(String currentStacktrace) {
        try {
            //Gets the Android external storage directory & Create new folder Crash_Reports
            String PATH = Environment.getExternalStorageDirectory() + "/BMerchant_Crash_Reports/";
            File dir = new File(PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            Date date = new Date();
            String filename = dateFormat.format(date) + "_STACKTRACE" + ".txt";

            // Write the file into the folder
            File reportFile = new File(dir, filename);
            FileWriter fileWriter = new FileWriter(reportFile);
            fileWriter.append(currentStacktrace);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            Log.e("ExceptionHandler", e.getMessage());
        }

    }

    public void prepareLogs(Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************" + LINE_SEPARATOR);
        errorReport.append(stackTrace.toString());
        errorReport.append("************ Timestamp ************"+ LINE_SEPARATOR);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd-MM-yyyy & hh:mm a");
            Date date = new Date();
            String filename = dateFormat.format(date);
            errorReport.append("Date and Time: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        errorReport.append(LINE_SEPARATOR + "************ DEVICE INFORMATION ***********" + LINE_SEPARATOR);
        errorReport.append("Brand: " + Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: " + Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: " + Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: " + Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: " + Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append(LINE_SEPARATOR + "************ BUILD INFO ************" + LINE_SEPARATOR);
        errorReport.append("SDK: " + Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: " + Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: " + Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);


        errorReport.append(LINE_SEPARATOR + "************ APP INFO ************" + LINE_SEPARATOR);

        int versionCode = BuildConfig.VERSION_CODE;
        String version = BuildConfig.VERSION_NAME;

        errorReport.append("VersionCode: " + versionCode);
        errorReport.append(LINE_SEPARATOR);

        errorReport.append("App Version: " + version);
        errorReport.append(LINE_SEPARATOR);

        reportLogs(errorReport.toString());

    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        prepareLogs(throwable);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

}

package wallet.ooredo.com.live.mainmenu;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import coreframework.database.CustomSharedPreferences;
import coreframework.taskframework.GenericActivity;
import wallet.ooredo.com.live.BuildConfig;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.merchantlogin.MerchantLoginActivity;
import ycash.wallet.json.pojo.MerchantAppVersionResponse;

/**
 * Created by 10030 on 11/26/2016.
 */
public class CheckForUpdatesActivity extends GenericActivity {
    String versioncode_str, newVersion = null;
    int versioncode = 0;
    String versionname;
    ProgressDialog dialog = null;
    TextView text;
    MerchantAppVersionResponse merchantAppVersionResponse = null;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String[] permissionsRequired = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,};
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 79;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkforupdates_layout);


        dialog = new ProgressDialog(CheckForUpdatesActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);



        text = (TextView) findViewById(R.id.check_for_updates_version_name);
        String response = getIntent().getStringExtra("data");
        merchantAppVersionResponse = new Gson().fromJson(response, MerchantAppVersionResponse.class);

        ((Button) findViewById(R.id.check_for_updates_close_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckForUpdatesActivity.this.finish();
            }
        });
        versionname = getIntent().getStringExtra("old_versionNumber");

        Double d_new = Double.parseDouble(merchantAppVersionResponse.getCurentVersion());
        Double d_old = new Double(versionname);
        if (d_old < d_new) {
            ((Button) findViewById(R.id.check_for_updates_close_btn)).setText(getString(R.string.update));
            ((Button) findViewById(R.id.check_for_updates_close_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        //clearing persistent data when user going to update the app

                        //Rahman commented 2 lines
//                        CustomSharedPreferences.saveStringData(getBaseContext(), "", CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
//                        ((CoreApplication) getApplication()).setIsUserLoggedIn(false);


                    /*Toast.makeText(getBaseContext(), "Your updated with latest version", Toast.LENGTH_SHORT).show();
                    Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://demo.bookeey.com/apks/Bookeey_Merchant.apk"));
                    startActivity(notificationIntent);*/

                        //live:  https://bookeey.com/apks/Bookeey_Merchant.apk
                        //Demo: https://demo.bookeey.com/apks/Bookeey_Merchant.apk
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                        }*/
                            if (isEnabled) {
//                            InstallAPK downloadAndInstall = new InstallAPK();
//                            downloadAndInstall.execute("https://www.bookeey.com/apks/Bookeey_Merchant.apk");


                                //Delete file
//                            Uri uri =  Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "Bookeey_Merchant.apk"));
//                            File fdelete = new File(uri.getPath());
//                            if (fdelete.exists()) {
//                                if (fdelete.delete()) {
//                                    System.out.println("file Deleted :" + uri.getPath());
//                                } else {
//                                    System.out.println("file not Deleted :" + uri.getPath());
//                                }
//                            }

//                            final File folder = new File(Environment.getExternalStorageDirectory() + "/download/");
//                            final File[] files = folder.listFiles( new FilenameFilter() {
//                                @Override
//                                public boolean accept( final File dir,
//                                                       final String name ) {
//                                    return name.matches( "Bookeey_Merchant.*\\.apk" );
//                                }
//                            } );
//                            for ( final File file : files ) {
//                                if ( !file.delete() ) {
//                                   Log.e("File:" ,"Can't remove " + file.getAbsolutePath() );
//                                }
//                            }


                            /*
                            Log.e("File1:" ,"Download file starts" );
                            //Latest
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bookeey.com/apks/Bookeey_Merchant.apk"));
                            startActivity(browserIntent);

                            Log.e("File1:" ,"Download file end" );
                            */


                                downLoadApkAndInstallForNewDevices();


                            } else {
                                //Toast.makeText(CheckForUpdatesActivity.this, "Storage permission needed. Please allow in App Settings for additional functionality", Toast.LENGTH_LONG).show();
                            }
                        } else {
//                        InstallAPK downloadAndInstall = new InstallAPK();
//                        downloadAndInstall.execute("https://www.bookeey.com/apks/Bookeey_Merchant.apk");


//                        Log.e("File2:" ,"Download file starts" );
//                        //Latest
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bookeey.com/apks/Bookeey_Merchant.apk"));
//                        startActivity(browserIntent);
//                        Log.e("File2:" ,"Download file end" );

                            downLoadApkAndInstallForOldDevices();


                        }


                    } catch (Exception e) {

                        Toast.makeText(CheckForUpdatesActivity.this,"OnClick Exce: "+e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }
            });
//            text.setText("Current Version is : " + versionname +"\n"+ "\nYour Merchant application has been updated,Please download the latest version to work all the features properly.\n\nPlease tap on update to get the latest version");

            text.setText(getString(R.string.current_version_is) + versionname +"\n"+ "\n"+getString(R.string.your_merchant_application_has_been_updated));


        } else {
//            text.setText("Your app is now up to date" + "\n" + "Current Version is : " + versionname);

            text.setText("Your app is now up to date" + "\n" + getString(R.string.current_version_is)  + versionname);
        }

    }

    boolean isEnabled = false;

    @Override
    protected void onResume() {
        super.onResume();

        isEnabled = checkPermissionStorage();
    }


    public void downLoadApkAndInstallForOldDevices(){

        dialog.setMessage("Downloading the app, please wait.");
        dialog.show();


        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "Bookeey_Merchant.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        final File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();



        //Merchant .apk which will sync
        String url = "https://www.bookeey.com/mno/merlogo/Bookeey_Merchant.apk";




        //Demo server
//        String url = "https://demo.bookeey.com/apks/Bookeey_Merchant.apk";

        //Google drive link
//        String url = "https://drive.google.com/open?id=1BUeWHwnGaCvlQphB4ZwSiwPTmFcJZo5D";

        //get url of app on server
//        String url = "https://www.bookeey.com/apks/Bookeey_Merchant.apk";



        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading...");
        request.setTitle("Bookeey app update");

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intentPara ){


                dialog.dismiss();

//                Toast.makeText(CheckForUpdatesActivity.this,"Download finished!",Toast.LENGTH_LONG).show();


                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(
                            Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(intent);
                    unregisterReceiver(this);
                    finish();




                }catch(Exception e){

                    Toast.makeText(CheckForUpdatesActivity.this,"View Exception: "+e.getMessage(),Toast.LENGTH_LONG).show();


                }


            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }


    public void downLoadApkAndInstallForNewDevices(){

        dialog.setMessage("Downloading the app, please wait.");
        dialog.show();

        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "Bookeey_Merchant.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        final File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();



        //Merchant .apk which will sync
        String url = "https://www.bookeey.com/mno/merlogo/Bookeey_Merchant.apk";



        //Demo server
//        String url = "https://demo.bookeey.com/apks/Bookeey_Merchant.apk";

        //Production
//        String url = "https://www.bookeey.com/apks/Bookeey_Merchant.apk";




        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading...");
        request.setTitle("Bookeey app update");

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intentPara ){


                dialog.dismiss();


//                Toast.makeText(CheckForUpdatesActivity.this,"Download finished!",Toast.LENGTH_LONG).show();


                try {

                    Uri uri = FileProvider.getUriForFile(CheckForUpdatesActivity.this, "wallet.ooredo.com.live", file);


                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(uri,
                            manager.getMimeTypeForDownloadedFile(downloadId));
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(install);

                    unregisterReceiver(this);
//                    finish();




                    //WITH PROVIDER

                    //CODE 1
//                //From provider sample working fine going to install option
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri,
//                        "application/vnd.android.package-archive");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getBaseContext().startActivity(intent);


                    /*
//                CODE 2
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");


                    unregisterReceiver(this);

                    startActivity(intent);

                    */


                }catch(Exception e){

                    Toast.makeText(CheckForUpdatesActivity.this,"View Exception: "+e.getMessage(),Toast.LENGTH_LONG).show();


                }


            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private boolean checkPermissionStorage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(CheckForUpdatesActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    private class InstallAPK extends AsyncTask<String, Void, Boolean> {


        int status = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                String PATH = Environment.getExternalStorageDirectory() + "/download/";
                File file = new File(PATH);
                file.mkdirs();
                // Create a file on the external storage under download
                File outputFile = new File(file, "Bookeey_Merchant.apk");
                FileOutputStream fos = new FileOutputStream(outputFile);

                HttpGet m_httpGet = null;
                HttpResponse m_httpResponse = null;

                // Create a http client with the parameters
                HttpClient m_httpClient = new DefaultHttpClient();
                String result = null;

                try {

                    // Create a get object
                    m_httpGet = new HttpGet(strings[0]);

                    // Execute the html request
                    m_httpResponse = m_httpClient.execute(m_httpGet);
                    HttpEntity entity = m_httpResponse.getEntity();

                    // See if we get a response
                    if (entity != null) {

                        InputStream instream = entity.getContent();
                        byte[] buffer = new byte[1024];

                        // Write out the file
                        int len1 = 0;
                        while ((len1 = instream.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);
                        }
                        fos.close();
                        instream.close();// till here, it works fine - .apk is download to my sdcard in download file

                    }

                } catch (ConnectTimeoutException cte) {
//                    Toast.makeText(CheckForUpdatesActivity.this, "Connection Timeout", Toast.LENGTH_SHORT).show();
                    Log.e("InstallAPK doInBground:","cte: "+cte.getMessage());
                    return false;
                } catch (Exception e) {
                    return false;
                } finally {
                    m_httpClient.getConnectionManager().closeExpiredConnections();
                }



                Uri uri = FileProvider.getUriForFile(CheckForUpdatesActivity.this, "wallet.ooredo.com.live", file);

                //CODE 1
//                //From provider sample working fine going to install option
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri,
//                        "application/vnd.android.package-archive");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getBaseContext().startActivity(intent);

//                CODE 2
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                startActivity(intent);



                //Old code with out provider authorities
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(
//                        Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "Bookeey_Merchant.apk")),
//                        "application/vnd.android.package-archive");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getBaseContext().startActivity(intent);


//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(
//                        Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "Bookeey_Merchant.apk")),
//                        "application/vnd.android.package-archive");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getBaseContext().startActivity(intent);


                //Exception
                //InstallAPK doInBground::  file:///storage/emulated/0/download/Bookeey_Merchant.apk exposed beyond app through Intent.getData()


                //March 07 2019

//                File file_new = new File(Environment.getExternalStorageDirectory() + "/download/" + "Bookeey_Merchant.apk");
//                Log.e("InstallAPK File path:"," "+file_new.getAbsolutePath());
//                Uri uri =  Uri.fromFile(file_new);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri, "application/vnd.android.package-archive");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getBaseContext().startActivity(intent);



                ////////////////
                /*
                //get destination to update file and set Uri
                //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
                //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
                //solution, please inform us in comment
                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = "Bookeey_Merchant.apk";
                destination += fileName;

//                final Uri uri = Uri.parse("file://" + destination);



                //Delete update file if exists
                File file_new = new File(destination);

               // final Uri uri = FileProvider.getUriForFile(CheckForUpdatesActivity.this, getApplicationContext().getPackageName() + ".utils", file_new);


                final Uri uri = (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) ?
                        FileProvider.getUriForFile(CheckForUpdatesActivity.this, BuildConfig.APPLICATION_ID + ".utils", file_new) :
                        Uri.fromFile(file_new);

                if (file_new.exists())
                    //file.delete() - test this, I think sometimes it doesnt work
                    file_new.delete();

                //get url of app on server
                String url = "https://www.bookeey.com/apks/Bookeey_Merchant.apk";

                //set downloadmanager
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Your app being updated");
                request.setTitle("App update");

                //set destination
                request.setDestinationUri(uri);

                // get download service and enqueue file
                final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                final long downloadId = manager.enqueue(request);

                //set BroadcastReceiver to install app when .apk is downloaded
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        install.setDataAndType(uri,
                                manager.getMimeTypeForDownloadedFile(downloadId));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(install);

                        unregisterReceiver(this);
                        finish();
                    }
                };
                //register receiver for when .apk download is compete
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
*/


                // System.exit(0);

            } catch (IOException e) {
//                Toast.makeText(CheckForUpdatesActivity.this, "Failed to update new apk", Toast.LENGTH_SHORT).show();
                Log.e("InstallAPK doInBground:"," "+e.getMessage());
                return false;
            } catch (Exception e1) {
//                Toast.makeText(CheckForUpdatesActivity.this, "Failed to update new apk", Toast.LENGTH_SHORT).show();
                Log.e("InstallAPK doInBground:"," "+e1.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialog.dismiss();
        }
    }
}
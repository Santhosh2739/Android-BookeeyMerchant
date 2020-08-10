package push_notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import coreframework.database.CustomSharedPreferences;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.Splash;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.CheckForUpdatesActivity;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't recognize.
             */

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) // If it's a regular GCM message, do some work.
            {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(this.toString(), "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(this.toString(), "Completed work @ " + SystemClock.elapsedRealtime());

                // Post notification of received message.
                String message = (String) extras.get("message");
                sendNotification(message);

                Log.i(this.toString(), "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (msg != null && !msg.isEmpty()) {
            if (msg.contains("Invoice")) {
                //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        //setSmallIcon(R.drawable.icon)
                        .setPriority(NotificationCompat.PRIORITY_HIGH) //HIGH, MAX, FULL_SCREEN and setDefaults(Notification.DEFAULT_ALL) will make it a Heads Up Display Style
                        .setDefaults(Notification.DEFAULT_ALL) // also requires VIBRATE permission
                        .setContentTitle("Bookeey")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        //.setContentText("Your Merchant application has been updated,Please download the latest version to work all the features properly")
                        .setAutoCancel(true);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBuilder.setSmallIcon(R.drawable.bookeey_notification);
                    mBuilder.setColor(getResources().getColor(R.color.app_color));
                } else {
                    mBuilder.setSmallIcon(R.drawable.icon2);
                }
                // mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                Notification notification = mBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                mNotificationManager.notify(1, notification);
                //return;
            } else if(msg.contains("Your Merchant application has been updated,Please download the latest version to work all the features properly")){

                return;

            }else
            {

                //clearing persistent data when user going to update the app
                /*CustomSharedPreferences.saveStringData(getBaseContext(),"",CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
                ((CoreApplication)getApplication()).setIsUserLoggedIn(false);*/

                // Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bookeey.com/apks/Bookeey_Merchant.apk"));

                Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bookeey.com/apks/Bookeey_Merchant.apk"));
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        // setSmallIcon(R.drawable.icon2)
                        .setPriority(NotificationCompat.PRIORITY_HIGH) //HIGH, MAX, FULL_SCREEN and setDefaults(Notification.DEFAULT_ALL) will make it a Heads Up Display Style
                        .setDefaults(Notification.DEFAULT_ALL) // also requires VIBRATE permission
                        .setContentTitle("Bookeey")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        //.setContentText("Your Merchant application has been updated,Please download the latest version to work all the features properly")
                        .setAutoCancel(true);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBuilder.setSmallIcon(R.drawable.bookeey_notification);
                    mBuilder.setColor(getResources().getColor(R.color.app_color));
                } else {
                    mBuilder.setSmallIcon(R.drawable.icon2);
                }
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                Notification notification = mBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                mNotificationManager.notify(1, notification);
                //send(msg);
            }
        }
    }


    private void send(String msg) {

        //For manually download APK and install
        InstallAPK downloadAndInstall = new InstallAPK();
        downloadAndInstall.execute("http://demo.bookeey.com/apks/Bookeey_Merchant.apk");

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "Bookeey_Merchant.apk")),
                "file/*");
        getBaseContext().startActivity(intent);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification noti = new NotificationCompat.Builder(this)
                .setContentTitle("Download completed")
                .setContentText("BookeeyMerchant")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pIntent).build();

        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
    }

    private class InstallAPK extends AsyncTask<String, Void, Boolean> {
        int status = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog.show();
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
                    //Toast.makeText(this, "Connection Timeout", Toast.LENGTH_SHORT).show();
                    return false;
                } catch (Exception e) {
                    return false;
                } finally {
                    m_httpClient.getConnectionManager().closeExpiredConnections();
                }
            } catch (IOException e) {
                //Toast.makeText(CheckForUpdatesActivity.this, "Failed to update new apk", Toast.LENGTH_SHORT).show();
                return false;
            } catch (Exception e1) {
                //Toast.makeText(CheckForUpdatesActivity.this, "Failed to update new apk", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //   dialog.dismiss();

            //manuall install apk after download complets
           /* Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "Bookeey_Merchant.apk")),
                    "file/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getBaseContext().startActivity(intent);*/
        }
    }
}

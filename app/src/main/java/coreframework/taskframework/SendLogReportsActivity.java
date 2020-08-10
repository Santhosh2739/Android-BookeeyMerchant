package coreframework.taskframework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;

import wallet.ooredo.com.live.R;


public class SendLogReportsActivity extends Activity implements View.OnClickListener {
    private AlertDialog alertDialog;
    private String logs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // make a dialog without a titlebar
        setFinishOnTouchOutside(false); // prevent users from dismissing the dialog by tapping outside
        setContentView(R.layout.send_log_reports_activity);
        logs = getIntent().getStringExtra("logs");
        showConfirmation();
    }

    @Override
    public void onClick(View v) {
        // respond to button clicks in your UI
    }

    private void sendLogFile() {
        if (logs == null)
            return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tech2@bookeey.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Error reported from BMerchant");
        intent.putExtra(Intent.EXTRA_TEXT, "Log file attached." + logs); // do this so some email clients don't complain about empty body.
        startActivity(intent);
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
                 startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                //TODO: Handle case where no email app is available
            }

           finish();
        }
    }

    private void showConfirmation() {
        // method as shown above
        alertDialog = new AlertDialog.Builder(SendLogReportsActivity.this).create();
        alertDialog.setTitle(Html.fromHtml("<font color='#000000'>Report Error !</font>"));
        alertDialog.setMessage("The Application BMerchant(wallet.ooredo.com.ooredolive.mx) has stopped unexpectedly.Please 'REPORT' ");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Report", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendMailToDeveloper(logs);
//                SendLogReportsActivity.this.finish();

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
                SendLogReportsActivity.this.finish();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}


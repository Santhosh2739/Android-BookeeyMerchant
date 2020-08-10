package coreframework.taskframework;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import wallet.ooredo.com.live.R;

/**
 * Created by mohit on 02-06-2015.
 */
public class GenericActivity extends Activity implements UndoBarController.UndoListener{

    private UndoBarController mUndoBarController;
    private boolean isUndoBarEnabled = false;

    public boolean isCloseActivityOnAlertDialoguesExecution() {
        return closeActivityOnAlertDialoguesExecution;
    }

    public void setCloseActivityOnAlertDialoguesExecution(boolean closeActivityOnAlertDialoguesExecution) {
        this.closeActivityOnAlertDialoguesExecution = closeActivityOnAlertDialoguesExecution;
    }

    private boolean closeActivityOnAlertDialoguesExecution = false;

    final public void enableUndoBar(){
        mUndoBarController = new UndoBarController(findViewById(R.id.undobar), this);
        if(android.os.Build.VERSION.SDK_INT>=12){
            isUndoBarEnabled = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new HandleUncaughtException(this));
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if(null != mUndoBarController){
            mUndoBarController.onSaveInstanceState(outState);
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(null != mUndoBarController){
            mUndoBarController.onRestoreInstanceState(savedInstanceState);
        }
    }
    @Override
    public void onUndo(Parcelable token) {

    }
    public void showNeutralDialogue(String title, String message) {
        if(isUndoBarEnabled){
            mUndoBarController.showUndoBar(true, message, null);
        }else{
            LayoutInflater li = LayoutInflater.from(getBaseContext());
            View promptsView = li.inflate(R.layout.neutral_alert, null);
            TextView alert_title = (TextView) promptsView.findViewById(R.id.alert_title);
            alert_title.setText(title);
            TextView alert_message = (TextView) promptsView.findViewById(R.id.alert_message);
            alert_message.setText(message);

            AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
            builder.setView(promptsView);

            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    if (closeActivityOnAlertDialoguesExecution) {
                        finish();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}

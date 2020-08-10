package coreframework.taskframework;

import android.app.Activity;
import android.os.Message;

public interface UserInterfaceBackgroundProcessing {
	public String captureURL();
	public void preProcessResponse(Message msg);
	public void processResponse(Message msg);
	public void prePerformUserInterfaceAndDismiss(Activity activity, ProgressDialogFrag dialogueFragment);
	public void performUserInterfaceAndDismiss(Activity activity, ProgressDialogFrag dialogueFragment);
    public boolean isPost();
	public boolean isLocalProcess();
	public void performTask();
	public void handleSessionInvalid(Activity activity, ProgressDialogFrag dialogueFragment);
}
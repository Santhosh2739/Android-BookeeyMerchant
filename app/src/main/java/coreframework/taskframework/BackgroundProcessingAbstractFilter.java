package coreframework.taskframework;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;

import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.merchantlogin.MerchantLoginActivity;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;

/**
 * Created by mohit on 16/1/16.
 */
public abstract class BackgroundProcessingAbstractFilter implements UserInterfaceBackgroundProcessing{


    private boolean _disable_filter = false;

    private boolean _ev_session_expired;
    private boolean _ev_session_invalid;

    @Override
    final public void preProcessResponse(Message msg) {
        //Disable Processing
        if(_disable_filter){
            processResponse(msg);
            return;
        }
        GenericResponse response = new Gson().fromJson((String)msg.obj,GenericResponse.class);
        if(response!=null && (response.getG_errorDescription().trim().equalsIgnoreCase("E116") || response.getG_errorDescription().trim().equalsIgnoreCase("E117"))){
            _ev_session_expired = true;
            _ev_session_invalid = true;
        }
        if(_ev_session_expired || _ev_session_invalid){
            //SAFELY IGNORE PROCESSING OF MESSAGE
        }else{
            processResponse(msg);
        }
    }
    @Override
    final public void prePerformUserInterfaceAndDismiss(Activity activity, ProgressDialogFrag dialogueFragment) {
        if(_disable_filter){
            performUserInterfaceAndDismiss(activity, dialogueFragment);
            return;
        }
        if(_ev_session_expired || _ev_session_invalid){
            handleSessionInvalid(activity, dialogueFragment);
        } else {
            performUserInterfaceAndDismiss(activity, dialogueFragment);
        }
    }
    @Override
    public void handleSessionInvalid(Activity activity, ProgressDialogFrag dialogueFragment) {
        Toast.makeText(activity, "_gl_logged_out", Toast.LENGTH_LONG).show();
        CoreApplication application = (CoreApplication)activity.getApplication();
        application.setIsUserLoggedIn(false);
        application.setMerchantLoginRequestResponse(new MerchantLoginRequestResponse());
        dialogueFragment.dismiss();
        Intent i = new Intent(activity, MerchantLoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("exit", Boolean.TRUE);
        activity.startActivity(i);
    }
    public boolean is_disable_filter() {
        return _disable_filter;
    }
    public void set_disable_filter(boolean _disable_filter) {
        this._disable_filter = _disable_filter;
    }
}

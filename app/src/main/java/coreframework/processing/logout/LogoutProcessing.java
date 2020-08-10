package coreframework.processing.logout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;

import coreframework.database.CustomSharedPreferences;
import coreframework.network.ServerConnection;
import coreframework.taskframework.BackgroundProcessingAbstractFilter;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.merchantlogin.MerchantLoginActivity;
import ycash.wallet.json.pojo.generic.GenericRequest;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;


/**
 * Created by mohit on 16-06-2015.
 */
public class LogoutProcessing extends BackgroundProcessingAbstractFilter {
    private CoreApplication application;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private GenericResponse response;

    private String json_login_request_to_be_removed = CustomSharedPreferences.SIMPLE_NULL;

    public LogoutProcessing(CoreApplication application, boolean isPost) {
        this.isPost = isPost;
        this.application = application;
    }

    @Override
    public String captureURL() {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setG_transType(TransType.LOGOUT_MERCHANT.name());
        String authtoken = CustomSharedPreferences.getStringData(application, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        genericRequest.setG_oauth_2_0_client_token(authtoken);
        // genericRequest.setG_oauth_2_0_client_token(application.getMerchantLoginRequestResponse().getOauth_2_0_client_token());
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.LOGOUT_MERCHANT.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(genericRequest)));
        String serverURL = buffer.toString();
        return serverURL;
    }

    @Override
    public void processResponse(Message msg) {
        if (msg.arg1 == ServerConnection.OPERATION_SUCCESS) {
            String network_response = ((String) msg.obj).trim();
            response = new Gson().fromJson(network_response, GenericResponse.class);
            if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.LOGOUT_MERCHANT_RESPONSE.name())) {
                success = true;
            } else if (response != null && !response.getG_response_trans_type().equalsIgnoreCase(TransType.LOGOUT_MERCHANT_RESPONSE.name())) {
                error_text_header = response.getG_response_trans_type();
                error_text_details = response.getG_errorDescription();
            } else {
                error_text_header = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
                error_text_details = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
            }
        } else if (msg.arg1 == ServerConnection.OPERATION_FAILURE_GENERAL_SERVER) {
            error_text_header = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
            error_text_details = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
        } else if (msg.arg1 == ServerConnection.OPERATION_FAILURE_NETWORK) {
            error_text_header = "ServerConnection.OPERATION_FAILURE_NETWORK";
            error_text_details = "ServerConnection.OPERATION_FAILURE_NETWORK";
        }
    }

    @Override
    public void performUserInterfaceAndDismiss(Activity activity, ProgressDialogFrag dialogueFragment) {
        Toast.makeText(activity, success ? activity.getString(R.string.log_out_successful) : activity.getString(R.string.log_out) , Toast.LENGTH_LONG).show();
        application.setIsUserLoggedIn(false);
        application.setMerchantLoginRequestResponse(new MerchantLoginRequestResponse());

        //clearing all data when logout the application
        String share_db_preference_db = "share_db_preference_db";
        SharedPreferences sharedPreferences = application.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        String selectedLanguage = CustomSharedPreferences.getStringData(application, CustomSharedPreferences.SP_KEY.LANGUAGE);

        editor.clear();
        editor.commit();

        CustomSharedPreferences.saveStringData(application,selectedLanguage,CustomSharedPreferences.SP_KEY.LANGUAGE);



        dialogueFragment.dismiss();
        Intent i = new Intent(activity, MerchantLoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("exit", Boolean.TRUE);
        activity.startActivity(i);
    }

    @Override
    public boolean isPost() {
        return isPost;
    }

    @Override
    public boolean isLocalProcess() {
        return false;
    }

    @Override
    public void performTask() {

    }
}
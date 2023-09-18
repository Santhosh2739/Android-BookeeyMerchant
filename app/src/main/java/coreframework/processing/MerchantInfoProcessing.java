package coreframework.processing;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;

import com.google.gson.Gson;

import coreframework.database.CustomSharedPreferences;
import coreframework.network.ServerConnection;
import coreframework.taskframework.BackgroundProcessingAbstractFilter;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MerchantInfoActivity;
import ycash.wallet.json.pojo.generic.GenericRequest;
import ycash.wallet.json.pojo.generic.GenericResponse;

/**
 * Created by 10030 on 11/25/2016.
 */
public class MerchantInfoProcessing extends BackgroundProcessingAbstractFilter {

    private CoreApplication application;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private GenericResponse response;
    private String response_pojo;

    private String json_login_request_to_be_removed = CustomSharedPreferences.SIMPLE_NULL;

    public MerchantInfoProcessing(CoreApplication application, boolean isPost) {
        this.isPost = isPost;
        this.application = application;
    }

    @Override
    public String captureURL() {
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setG_transType(TransType.MERCHANT_INFO_REQUEST.name());
        //genericRequest.setG_oauth_2_0_client_token(application.getMerchantLoginRequestResponse().getOauth_2_0_client_token());
        String authtoken = CustomSharedPreferences.getStringData(application, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        genericRequest.setG_oauth_2_0_client_token(authtoken);
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.MERCHANT_INFO_REQUEST.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(genericRequest)));
        String serverURL = buffer.toString();
        return serverURL;
    }


    @Override
    public void processResponse(Message msg) {
        if (msg.arg1 == ServerConnection.OPERATION_SUCCESS) {
            String network_response = ((String) msg.obj).trim();
            response = new Gson().fromJson(network_response, GenericResponse.class);
            if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANT_INFO_RESPONSE.name()) && response.getG_status() == 1) {
                this.response_pojo = network_response;
                success = true;
            } else if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANT_INFO_RESPONSE.name()) && response.getG_status() != 1) {
                error_text_header = response.getG_response_trans_type();
                error_text_details = response.getG_errorDescription();
            } else {
                error_text_header = "Failure general server";
                error_text_details = "Failure general server";
            }
        } else if (msg.arg1 == ServerConnection.OPERATION_FAILURE_GENERAL_SERVER) {
            error_text_header = "Failure general server";
            error_text_details = "Failure general server";
        } else if (msg.arg1 == ServerConnection.OPERATION_FAILURE_NETWORK) {
            error_text_header = "Check your internet connection and try again";
            error_text_details = "Check your internet connection and try again";
        }
    }


    @Override
    public void performUserInterfaceAndDismiss(Activity activity, ProgressDialogFrag dialogueFragment) {
        if (success) {
            dialogueFragment.dismiss();
            Intent intent = new Intent(activity, MerchantInfoActivity.class);
            intent.putExtra("view_profile", response_pojo);
            activity.startActivity(intent);

        }
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

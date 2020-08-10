package coreframework.processing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;

import com.google.gson.Gson;

import coreframework.database.CustomSharedPreferences;
import coreframework.network.ServerConnection;
import coreframework.taskframework.BackgroundProcessingAbstractFilter;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.CheckForUpdatesActivity;
import ycash.wallet.json.pojo.MerchantAppVersionResponse;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.pushnotification.SendVersionNumber;

/**
 * Created by 10030 on 11/26/2016.
 */
public class CheckForUpdateProcessing extends BackgroundProcessingAbstractFilter {
    private CoreApplication application;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private GenericResponse response;
    double appVersionDouble = 0d;
    MerchantAppVersionResponse merchantAppVersionResponse=null;

    private String json_login_request_to_be_removed = CustomSharedPreferences.SIMPLE_NULL;

    public CheckForUpdateProcessing(CoreApplication application, boolean isPost) {
        this.isPost = isPost;
        this.application = application;
    }
    @Override
    public String captureURL() {
        PackageInfo p_info;

        try {
            p_info = this.application.getPackageManager().getPackageInfo(this.application.getPackageName(), 0);
            appVersionDouble = Double.parseDouble(p_info.versionName);
        } catch (PackageManager.NameNotFoundException e) {

        }

        String deviceVersion = Build.VERSION.RELEASE;
        SendVersionNumber sendVersionNumber= new SendVersionNumber();
        sendVersionNumber.setCurrentVersionNumber(appVersionDouble);
        sendVersionNumber.setG_transType(TransType.MERCHANTAPPVERSION.name());
        sendVersionNumber.setTypeofAction("checkforupdate");
        sendVersionNumber.setG_oauth_2_0_client_token(application.getMerchantLoginRequestResponse().getOauth_2_0_client_token());
        /*genericRequest.setG_transType(TransType.LOGOUT_MERCHANT.name());
        genericRequest.setG_oauth_2_0_client_token(application.getMerchantLoginRequestResponse().getOauth_2_0_client_token());*/
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.MERCHANTAPPVERSION.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(sendVersionNumber)));
        String serverURL = buffer.toString();
        return serverURL;

    }
    @Override
    public void processResponse(Message msg) {
        if (msg.arg1 == ServerConnection.OPERATION_SUCCESS) {
            String network_response = ((String) msg.obj).trim();
            response = new Gson().fromJson(network_response, GenericResponse.class);
            if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANTAPPVERSION.name())&& response.getG_status()==1) {
                success = true;
                merchantAppVersionResponse= new Gson().fromJson(network_response, MerchantAppVersionResponse.class);

            }else if (response != null && !response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANTAPPVERSION.name())) {
                error_text_header = response.getG_response_trans_type();
                error_text_details = response.getG_errorDescription();
            }
            else {
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
        if(success) {
            dialogueFragment.dismiss();
            Intent i = new Intent(activity, CheckForUpdatesActivity.class);
            i.putExtra("old_versionNumber", "" + appVersionDouble);
            i.putExtra("data", new Gson().toJson(merchantAppVersionResponse));
            activity.startActivity(i);
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

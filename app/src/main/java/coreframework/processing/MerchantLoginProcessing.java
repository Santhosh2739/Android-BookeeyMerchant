package coreframework.processing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import coreframework.database.CustomSharedPreferences;
import coreframework.network.ServerConnection;
import coreframework.taskframework.BackgroundProcessingAbstractFilter;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequest;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;

/**
 * Created by munireddy on 16-06-2015.
 */
public class MerchantLoginProcessing extends BackgroundProcessingAbstractFilter {
    private CoreApplication application;
    private String userName;
    private String password;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private String loginResponse = null;
    MerchantLoginRequest loginRequest = null;
    String imeinumber = null;


    public MerchantLoginProcessing(String imeinumber, String userName, String password, boolean isPost, CoreApplication application) {
        this.userName = userName;
        this.password = password;
        this.isPost = isPost;
        this.imeinumber = imeinumber;
        this.application = application;
    }

    @Override
    public String captureURL() {
        //SET MORE PARAMS HERE STARTS
        //GET APP VERSION
        PackageInfo p_info;
        double appVersionDouble = 0d;
        //double appVersionDouble = 1.0;

        try {
            p_info = this.application.getPackageManager().getPackageInfo(this.application.getPackageName(), 0);
            appVersionDouble = Double.parseDouble(p_info.versionName);
        } catch (PackageManager.NameNotFoundException e) {

        }
        String deviceVersion = Build.VERSION.RELEASE;
        String deviceID = ((CoreApplication) application).getThisDeviceUniqueAndroidId();
        CustomSharedPreferences.saveStringData(application, "" + appVersionDouble, CustomSharedPreferences.SP_KEY.VERSION_N0);
        loginRequest = new MerchantLoginRequest();
        loginRequest.setDeviceIdNumber(deviceID);
        loginRequest.setIpAddress("");
        loginRequest.setImeiNumber(imeinumber);
        loginRequest.setDeviceType(5);//For Android Only|For Windows & IOS It will be different
        loginRequest.setLat(Double.toString(0.00d));//TODO:
        loginRequest.setLon(Double.toString(0.00d));//TODO:
        loginRequest.setGcmId("");
        loginRequest.setWalletApplicationVersion(Double.toString(appVersionDouble));
        loginRequest.setDeviceOsVersionDetails1(deviceVersion);
        loginRequest.setDeviceOsVersionDetails2("Android");

        loginRequest.setG_userId(this.userName);
        loginRequest.setG_password(this.password);
        loginRequest.setG_transType(TransType.LOGIN_MERCHANT.name());


        Log.e("Merchant Login JSON: ",""+new Gson().toJson(loginRequest));

        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.LOGIN_MERCHANT.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(loginRequest)));

        Log.e("Merchant Login URL: ",""+buffer.toString());

        return buffer.toString();
    }

    @Override
    public void processResponse(Message msg) {
        if (msg.arg1 == ServerConnection.OPERATION_SUCCESS) {
            String network_response;
            network_response = ((String) msg.obj).trim();
            GenericResponse response = new Gson().fromJson(network_response, GenericResponse.class);
            if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.LOGIN_MERCHANT_RESPONSE.name()) && response.getG_status() == 1) {
                this.loginResponse = network_response;
                this.success = true;
                application.setIsUserLoggedIn(true);
                application.setMerchantLoginRequestResponse(new Gson().fromJson(network_response, MerchantLoginRequestResponse.class));
                //Save Login Response In Persistent Space:::
                MerchantLoginRequestResponse pojo = new Gson().fromJson(network_response, MerchantLoginRequestResponse.class);
                CustomSharedPreferences.saveboolenData(application, pojo.isMerchantExtraFields(), CustomSharedPreferences.SP_KEY.EXTRA_FIELDS);
                CustomSharedPreferences.saveStringData(application, network_response, CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
                CustomSharedPreferences.saveStringData(application, userName, CustomSharedPreferences.SP_KEY.USERNAME);
                CustomSharedPreferences.saveStringData(application, pojo.getOauth_2_0_client_token(), CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
                CustomSharedPreferences.saveStringData(application, pojo.getMerchantName(), CustomSharedPreferences.SP_KEY.NAME);
                CustomSharedPreferences.saveStringData(application, pojo.getMerchantAddress(), CustomSharedPreferences.SP_KEY.LOCATION);

            } else if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.LOGIN_MERCHANT_RESPONSE.name()) && response.getG_status() != 1) {
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
        if (success) {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("merchantid", userName);
            // intent.putExtra("response",this.loginResponse);
            activity.startActivity(intent);
            activity.finish();
        } else {
            //Handle Any types of failure here:
            if (activity instanceof GenericActivity) {
                ((GenericActivity) activity).showNeutralDialogue(error_text_header, error_text_details);
            } else {
                Toast.makeText(activity, error_text_header + "-" + error_text_details, Toast.LENGTH_SHORT).show();
            }
        }
        dialogueFragment.dismiss();
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

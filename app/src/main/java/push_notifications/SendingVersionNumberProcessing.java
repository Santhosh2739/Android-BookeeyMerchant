package push_notifications;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;

import coreframework.network.ServerConnection;
import coreframework.taskframework.BackgroundProcessingAbstractFilter;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.application.CoreApplication;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.pushnotification.SendVersionNumber;

/**
 * Created by munireddy on 5/20/2016.
 */
public class SendingVersionNumberProcessing extends BackgroundProcessingAbstractFilter {
    private CoreApplication application;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private String loginResponse = null;
    private String gcm_reg_id = "";
    private String merchant_ref_id = "";
    private String batteryStatus = "";
    private String location = "";

    private String imeinumber = null;


    public SendingVersionNumberProcessing(String imeinumber, String gcm_reg_id, String merchant_ref_id, String batteryStatus, String location, boolean isPost, CoreApplication application) {
        this.isPost = isPost;
        this.application = application;
        this.gcm_reg_id = gcm_reg_id;
        this.merchant_ref_id = merchant_ref_id;
        this.imeinumber = imeinumber;
        this.batteryStatus = batteryStatus;
        this.location = location;

    }

    @Override
    public String captureURL() {
        //SET MORE PARAMS HERE STARTS
        //GET APP VERSION
        PackageInfo p_info;
        double appVersionDouble = 0d;
        try {
            p_info = this.application.getPackageManager().getPackageInfo(this.application.getPackageName(), 0);
            appVersionDouble = Double.parseDouble(p_info.versionName);
        } catch (PackageManager.NameNotFoundException e) {

        }
        String deviceVersion = Build.VERSION.RELEASE;
        String deviceID = ((CoreApplication) application).getThisDeviceUniqueAndroidId();

        SendVersionNumber sendVersionNumerber = new SendVersionNumber();
        sendVersionNumerber.setImeiNumber(imeinumber);
        sendVersionNumerber.setGCMRegistrationId(gcm_reg_id);
        sendVersionNumerber.setBatteryStatus(batteryStatus);
        sendVersionNumerber.setCurrentLocation(location);
        sendVersionNumerber.setMerchantRefNumber(merchant_ref_id);
        sendVersionNumerber.setG_transType(TransType.MERCHANTAPPVERSION.name());
        sendVersionNumerber.setCurrentVersionNumber(appVersionDouble);
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.MERCHANTAPPVERSION.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(sendVersionNumerber)));
        return buffer.toString();
    }

    @Override
    public void processResponse(Message msg) {
        if (msg.arg1 == ServerConnection.OPERATION_SUCCESS) {
            String network_response;
            network_response = ((String) msg.obj).trim();
            GenericResponse response = new Gson().fromJson(network_response, GenericResponse.class);

            if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANTAPPVERSION.name()) && response.getG_status() == 1) {
                this.loginResponse = network_response;
                this.success = true;
                application.setGenericResponse(response);

            } else if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANTAPPVERSION.name()) && response.getG_status() != 1) {
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
        try {
            dialogueFragment.dismiss();
        } catch (Exception e) {
            Toast.makeText(activity.getBaseContext(), "Please wait..2 seconds and continue", Toast.LENGTH_SHORT).show();
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

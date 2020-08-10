package coreframework.processing;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import coreframework.database.CustomSharedPreferences;
import coreframework.network.ServerConnection;
import coreframework.taskframework.BackgroundProcessingAbstractFilter;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import ycash.wallet.json.pojo.consolidatedreports.MerchantReportRequest;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;

/**
 * Created by 30099 on 3/2/2016.
 */
public class ReportmailProcessing extends BackgroundProcessingAbstractFilter {

    private MerchantReportRequest request;
    private String response_json = null;
    private CoreApplication application;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private MerchantLoginRequestResponse merchantLoginRequestResponse = null;

    public ReportmailProcessing(MerchantReportRequest request, CoreApplication application, boolean isPost){
        this.request = request;
        this.application = application;
        this.isPost = isPost;
        merchantLoginRequestResponse = application.getMerchantLoginRequestResponse();
    }
    @Override
    public String captureURL() {
        //Load Security Params to the request
        //this.request.setG_oauth_2_0_client_token(merchantLoginRequestResponse.getOauth_2_0_client_token());
        String authtoken = CustomSharedPreferences.getStringData(application, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        this.request.setG_oauth_2_0_client_token(authtoken);
        this.request.setG_transType(TransType.MERCHANT_REPORT_EMAIL_REQUEST.name());
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.MERCHANT_REPORT_EMAIL_REQUEST.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(this.request)));

        Log.e("Reports Req",""+new Gson().toJson(this.request));

        String send_money_url = buffer.toString();

        Log.e("Reports URL",""+send_money_url);

        return send_money_url;
    }
    @Override
    public void processResponse(Message msg) {
        if(msg.arg1 == ServerConnection.OPERATION_SUCCESS){
            String network_response = ((String)msg.obj).trim();
            Log.e("Reports Res",""+network_response);
            GenericResponse response = new Gson().fromJson(network_response,GenericResponse.class);
            if(response!=null && response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANT_REPORT_EMAIL_RESPONSE.name())&&response.getG_status()==1){
                this.response_json = network_response;
                this.success = true;
                error_text_header= response.getG_status_description();


            }else if(response!=null && response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANT_REPORT_EMAIL_RESPONSE.name())){
                error_text_header = response.getG_status_description();
                error_text_details = response.getG_errorDescription();
            }else{
                error_text_header = "Failure general server";
                error_text_details = "Failure general server";
            }
        }else if(msg.arg1 == ServerConnection.OPERATION_FAILURE_GENERAL_SERVER){
            error_text_header = "Failure general server";
            error_text_details = "Failure general server";
        }else if(msg.arg1 == ServerConnection.OPERATION_FAILURE_NETWORK){
            error_text_header = "Please check your network connection";
            error_text_details = "Please check your network connection";
        }
    }
    @Override
    public void performUserInterfaceAndDismiss(Activity activity, ProgressDialogFrag dialogueFragment) {
        dialogueFragment.dismiss();
        if(success){
            Toast.makeText(activity, error_text_header, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();

        }else{
            //Handle Any types of failure here:
           /* if(activity instanceof GenericActivity){
                ((GenericActivity)activity).showNeutralDialogue(error_text_header,error_text_details);
            }else{
                Toast.makeText(activity, error_text_header + "-" + error_text_details, Toast.LENGTH_SHORT).show();
            }*/
            Toast.makeText(activity, error_text_header, Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public boolean isPost() {
        return false;
    }

    @Override
    public boolean isLocalProcess() {
        return false;
    }

    @Override
    public void performTask() {

    }
}
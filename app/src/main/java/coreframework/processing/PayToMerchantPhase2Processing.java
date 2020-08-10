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
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.barcodepaymentcollection.QRCodePaymentCollectionFinalScreen;
import wallet.ooredo.com.live.mainmenu.MainActivity;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantCommitRequest;

/**
 * Created by munireddy on 29-06-2015.
 */
public class PayToMerchantPhase2Processing extends BackgroundProcessingAbstractFilter {

    private PayToMerchantCommitRequest request;
    private String response_json = null;
    private CoreApplication application;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private String error_status_discription="";
    private MerchantLoginRequestResponse merchantLoginRequestResponse = null;
    GenericResponse response= null;

    public PayToMerchantPhase2Processing(PayToMerchantCommitRequest request, CoreApplication application, boolean isPost){
        this.request = request;
        this.application = application;
        this.isPost = isPost;
        merchantLoginRequestResponse = application.getMerchantLoginRequestResponse();
    }
    @Override
    public String captureURL() {
        //this.request.setG_oauth_2_0_client_token(merchantLoginRequestResponse.getOauth_2_0_client_token());
        String authtoken = CustomSharedPreferences.getStringData(application, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        this.request.setG_oauth_2_0_client_token(authtoken);
        this.request.setG_transType(TransType.PAY_TO_MERCHANT_COMMIT_REQUEST.name());

        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.PAY_TO_MERCHANT_COMMIT_REQUEST.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(this.request)));
        String send_money_url = buffer.toString();

        Log.e("Accept Payment L2"," URL"+send_money_url);

//        send_money_url = null;


        return send_money_url;
    }
    @Override
    public void processResponse(Message msg) {
        if(msg.arg1 == ServerConnection.OPERATION_SUCCESS){
            String network_response = ((String)msg.obj).trim();
            response = new Gson().fromJson(network_response,GenericResponse.class);
            if(response!=null && response.getG_response_trans_type().equalsIgnoreCase(TransType.PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE.name())&&response.getG_status()==1){
                this.response_json = network_response;
                this.success = true;
            }else if(response!=null && response.getG_response_trans_type().equalsIgnoreCase(TransType.PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE.name())&&response.getG_status()!=1){
                error_text_header= response.getG_status_description();
                error_text_details = response.getG_errorDescription();
            }else{

                if(response!=null) {

                    error_text_header = response.getG_status_description();
                    error_text_details = response.getG_errorDescription();

                }else{

                    error_text_header = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
                    error_text_details = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
                }
            }
        }else if(msg.arg1 == ServerConnection.OPERATION_FAILURE_GENERAL_SERVER){
            error_text_header = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
            error_text_details = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
        }else if(msg.arg1 == ServerConnection.OPERATION_FAILURE_NETWORK){
            error_text_header = "ServerConnection.OPERATION_FAILURE_NETWORK";
            error_text_details = "ServerConnection.OPERATION_FAILURE_NETWORK";
        }
    }
    @Override
    public void performUserInterfaceAndDismiss(Activity activity, ProgressDialogFrag dialogueFragment) {
        dialogueFragment.dismiss();
        if(success){
            Intent intent = new Intent(activity, QRCodePaymentCollectionFinalScreen.class);
            intent.putExtra("response", this.response_json);
            activity.startActivity(intent);
            activity.finish();
        }else{
            //Handle Any types of failure here:
            if(activity instanceof GenericActivity){
//                ((GenericActivity)activity).showNeutralDialogue(error_text_details, error_text_header);


                if(error_text_header.contains("Cancelled")){
                    Toast.makeText(activity, activity.getString(R.string.cancelled_by_merchant), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, error_text_header, Toast.LENGTH_LONG).show();
                }

                Intent intent= new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
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
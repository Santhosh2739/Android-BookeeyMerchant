package coreframework.processing;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
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
import wallet.ooredo.com.live.barcodepaymentcollection.QRCodePaymentCollectionLeg2RequestScreen;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantRequest;

/**
 * Created by munireddy on 29-06-2015.
 */
public class PayToMerchantPhase1Processing extends BackgroundProcessingAbstractFilter {

    private PayToMerchantRequest request;
    private String response_json = null;
    private CoreApplication application;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private MerchantLoginRequestResponse merchantLoginRequestResponse = null;

    public PayToMerchantPhase1Processing(PayToMerchantRequest request, CoreApplication application, boolean isPost){
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

        Log.e("L1 Url authtoken: ",""+authtoken);

        this.request.setG_transType(TransType.PAY_TO_MERCHANT.name());
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.PAY_TO_MERCHANT.getURL());

        Log.e("L1 Url data: ",""+new Gson().toJson(this.request));


        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(this.request)));
        String send_money_url = buffer.toString();

        Log.e("L1 Url: ",""+send_money_url);

        return send_money_url;
    }
    @Override
    public void processResponse(Message msg) {



            if(msg.arg1 == ServerConnection.OPERATION_SUCCESS ){

                Log.e("L1 Response: "," "+((String)msg.obj).trim());

            String network_response = ((String)msg.obj).trim();
            GenericResponse response = new Gson().fromJson(network_response,GenericResponse.class);
            if(response!=null && response.getG_response_trans_type().equalsIgnoreCase(TransType.PAY_TO_MERCHANT_RESPONSE.name())&&response.getG_status()!=-1){
                this.response_json = network_response;
                this.success = true;
                Log.e("L1 success", response.toString());
                }else if(response!=null && response.getG_response_trans_type().equalsIgnoreCase(TransType.PAY_TO_MERCHANT_RESPONSE.name())){
                error_text_header = response.getG_response_trans_type();
                error_text_details = response.getG_errorDescription();
            }else{
                error_text_header = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
                error_text_details = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
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


        try {

            Log.e("L1 result", success + "");
            if (success) {
                Intent intent = new Intent(activity, QRCodePaymentCollectionLeg2RequestScreen.class);
                intent.putExtra("response", this.response_json);
                activity.startActivity(intent);
                activity.finish();
            } else {



                //Handle Any types of failure here:
                if (activity instanceof GenericActivity) {

                    Toast.makeText(application,error_text_details,Toast.LENGTH_LONG).show();

//                    ((GenericActivity) activity).showNeutralDialogue(error_text_header, error_text_details);


                    ((GenericActivity) activity).finish();

                } else {
                    Toast.makeText(activity, error_text_header + "-" + error_text_details, Toast.LENGTH_SHORT).show();
                }
            }


        }catch (Exception e){
            Toast.makeText(application,"System Error! "+e.getMessage(),Toast.LENGTH_LONG).show();

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
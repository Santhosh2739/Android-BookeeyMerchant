package coreframework.processing;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;

import com.google.gson.Gson;

import coreframework.database.CustomSharedPreferences;
import coreframework.network.ServerConnection;
import coreframework.taskframework.BackgroundProcessingAbstractFilter;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.offer_redeem.OfferRedeemFinalScreen;
import wallet.ooredo.com.live.offer_redeem.OfferRedeemSuccessFinal;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.offerredeem.MerchantBarcodeRequest;

/**
 * Created by munireddy on 5/2/2016.
 */
public class QRCodeOfferRedeemCommitProcessing extends BackgroundProcessingAbstractFilter {
    private CoreApplication application;
    private String offer_redeem_qrdata;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private String offer_redeem_validate_response = null;

    private MerchantLoginRequestResponse merchantLoginRequestResponse = null;
    public QRCodeOfferRedeemCommitProcessing(String _offer_redeem_qrdata, CoreApplication application) {
        this.offer_redeem_qrdata = _offer_redeem_qrdata;
        this.application = application;
        merchantLoginRequestResponse = application.getMerchantLoginRequestResponse();
    }
    @Override
    public String captureURL() {
        // String scanned_data =  "{"customerId":"00003A","dummyString":"aaaaa","offerId":"3"}"
        MerchantBarcodeRequest loginRequest = new MerchantBarcodeRequest();

//        try {
//            JSONObject scanned_data_jo = new JSONObject(offer_redeem_qrdata);
//            loginRequest.setCustomerId(scanned_data_jo.getString("customerId"));
//            loginRequest.setOfferId(Long.parseLong(scanned_data_jo.getString("offerId")));
//         loginRequest.setMer_reference(CustomSharedPreferences.getStringData(application.getApplicationContext(), CustomSharedPreferences.SP_KEY.USERNAME));
//
//            loginRequest.setG_oauth_2_0_client_token(scanned_data_jo.getString("auth_token_id"));
//
//        } catch(Exception e){
//            Toast.makeText(application.getApplicationContext(), "System Error!\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }


        String[] str_array = offer_redeem_qrdata.split("-");
        String auth_token = str_array[0];
        String offer_id = str_array[1];
        String static_id = str_array[2];
        loginRequest.setCustomerId(static_id);
        loginRequest.setOfferId(Long.parseLong(offer_id));
        loginRequest.setMer_reference(CustomSharedPreferences.getStringData(application.getApplicationContext(), CustomSharedPreferences.SP_KEY.USERNAME));
        loginRequest.setG_oauth_2_0_client_token(auth_token);


        loginRequest.setG_transType(TransType.OFFER_COMMIT_REQUEST.name());
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.OFFER_COMMIT_REQUEST.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(loginRequest)));
        return buffer.toString();
    }
    @Override
    public void processResponse(Message msg) {
        if (msg.arg1 == ServerConnection.OPERATION_SUCCESS) {
            String network_response;
            network_response = ((String) msg.obj).trim();
            GenericResponse response = new Gson().fromJson(network_response, GenericResponse.class);
            if (response!=null && response.getG_response_trans_type().equalsIgnoreCase(TransType.OFFER_COMMIT_RESPONSE.name()) && response.getG_status()==1) {
                this.offer_redeem_validate_response = network_response;
                this.success = true;

            } else if (response!=null && response.getG_response_trans_type().equalsIgnoreCase(TransType.OFFER_COMMIT_RESPONSE.name()) && response.getG_status()!=1) {
                error_text_header = response.getG_response_trans_type();
                error_text_details = response.getG_errorDescription();
            }else {
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
        dialogueFragment.dismiss();
        if(success){

           // Toast.makeText(activity, "Offer successfully redeemed", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(activity, OfferRedeemSuccessFinal.class);
           // intent.putExtra("response",this.offer_redeem_validate_response);
            activity.startActivity(intent);
            activity.finish();
        }else{
            //Handle Any types of failure here:
            if(activity instanceof GenericActivity){
                //  ((GenericActivity)activity).showNeutralDialogue(error_text_header,error_text_details);
               // Toast.makeText(activity, error_text_header + "-" + error_text_details, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, OfferRedeemFinalScreen.class);
                intent.putExtra(OfferRedeemFinalScreen.KEY_DESCRIPTION,this.error_text_details);
                intent.putExtra(OfferRedeemFinalScreen.KEY_HEADER,this.error_text_header);
                activity.startActivity(intent);
                activity.finish();

            }else{
               // Toast.makeText(activity, error_text_header + "-" + error_text_details, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, OfferRedeemFinalScreen.class);
                intent.putExtra(OfferRedeemFinalScreen.KEY_DESCRIPTION,this.error_text_details);
                intent.putExtra(OfferRedeemFinalScreen.KEY_HEADER,this.error_text_header);
                activity.startActivity(intent);
                activity.finish();

            }
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

package coreframework.processing;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
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
import wallet.ooredo.com.live.offers.OffersListActivity;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.offerredeem.OfferPreviewRequest;
import ycash.wallet.json.pojo.offerredeem.OffersResponsePojo;

/**
 * Created by 10037 on 21-Nov-17.
 */

public class OffersProcessing extends BackgroundProcessingAbstractFilter {

    private OfferPreviewRequest request;
    private String response_json = null;
    private CoreApplication application;
    private boolean isPost = false;
    private boolean success = false;
    private String error_text_header = "";
    private String error_text_details = "";
    private String status_code;
    private MerchantLoginRequestResponse merchantLoginRequestResponse = null;

    public OffersProcessing(OfferPreviewRequest request, CoreApplication application, boolean isPost) {
        this.request = request;
        this.application = application;
        this.isPost = isPost;
        merchantLoginRequestResponse = application.getMerchantLoginRequestResponse();
    }

    @Override
    public String captureURL() {
        //Load Security Params to the request
       // this.request.setG_oauth_2_0_client_token(merchantLoginRequestResponse.getOauth_2_0_client_token());
        String authtoken = CustomSharedPreferences.getStringData(application, CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        this.request.setG_oauth_2_0_client_token(authtoken);
        this.request.setG_transType(TransType.MERCHANT_OFFER_REQUEST.name());
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.MERCHANT_OFFER_REQUEST.getURL());
        buffer.append("?d=" + URLUTF8Encoder.encode(new Gson().toJson(this.request)));
        String invoice_url = buffer.toString();
        return invoice_url;
    }

    @Override
    public void processResponse(Message msg) {
        if (msg.arg1 == ServerConnection.OPERATION_SUCCESS) {
            String network_response = ((String) msg.obj).trim();
            if (!network_response.isEmpty()) {
                GenericResponse response = new Gson().fromJson(network_response, GenericResponse.class);
                if (response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANT_OFFER_RESPONSE.name()) && response.getG_status() == 1) {
                    this.response_json = network_response;
                    this.success = true;

                } else if (response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANT_OFFER_RESPONSE.name()) && response.getG_status() != 1) {

                    error_text_header = response.getG_response_trans_type();
                    error_text_details = response.getG_errorDescription();





                } else {
                    error_text_header = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
                    error_text_details = "ServerConnection.OPERATION_FAILURE_GENERAL_SERVER";
                }
            } else {


                error_text_header = "No offers available!";
                error_text_details = "No offers available!";

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
        if (success) {
            //Toast.makeText(activity, error_text_details, Toast.LENGTH_SHORT).show();

           /* Intent intent = new Intent(activity, OffersListActivity.class);
            intent.putExtra("OFFERS_DATA", response_json);
            activity.startActivity(intent);*/
            OffersResponsePojo response = new Gson().fromJson(response_json, OffersResponsePojo.class);
            if(response.getOfferList().size()!=0){
                Intent intent = new Intent(activity, OffersListActivity.class);
                intent.putExtra("OFFERS_DATA", response_json);
                activity.startActivity(intent);
            }else{
                Toast.makeText(activity, ""+application.getString(R.string.no_offers_available), Toast.LENGTH_SHORT).show();

            }

        } else {
            //Handle Any types of failure here:
            if (activity instanceof GenericActivity) {
                ((GenericActivity) activity).showNeutralDialogue(error_text_header, error_text_details);
            } else {
                Toast.makeText(activity, error_text_details, Toast.LENGTH_SHORT).show();
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
package wallet.ooredo.com.live.application;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import coreframework.database.CustomSharedPreferences;
import coreframework.network.ServerConnection;
import coreframework.utils.TransType;
import coreframework.utils.URLUTF8Encoder;
import wallet.ooredo.com.live.forceface.TransTypeInterface;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceTranHistoryResponsePojo;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantCommitRequestResponse;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantRequestResponse;
import ycash.wallet.json.pojo.transactionhistory.TransactionHistoryRequest;
import ycash.wallet.json.pojo.transactionhistory.TransactionHistoryResponse;

/**
 * Created by mohit on 10-12-2015.
 */
public class SyncService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_SESSION_INVALID = 3;

    public static final String TAG = "SyncService";

    public static final int TYPE_TRANS_HIST = 1;
    public static final int TYPE_USER_LOGGED_IN_STATUS = 2;
    public static final String TYPE_TRANS_HIST_FOR_SEARCH = "TYPE_TRANS_HIST_FOR_SEARCH";


    public static final String TYPE_TRANS_TYPE_FOR_HIST_FOR_SEARCH = "TYPE_TRANS_TYPE_FOR_HIST_FOR_SEARCH";

    public static final String TYPE_MOBILE_NO_FOR_HIST_FOR_SEARCH = "TYPE_MOBILE_NO_FOR_HIST_FOR_SEARCH";


    private String tran_type_selected,mobile_no_for_search = "";

    public SyncService() {
        super(SyncService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        int type = intent.getIntExtra("type", -1);
        boolean search = intent.getBooleanExtra(TYPE_TRANS_HIST_FOR_SEARCH, false);


        tran_type_selected = intent.getStringExtra(TYPE_TRANS_TYPE_FOR_HIST_FOR_SEARCH);

        mobile_no_for_search = intent.getStringExtra(TYPE_MOBILE_NO_FOR_HIST_FOR_SEARCH);


        Bundle bundle = new Bundle();
        if (type == -1) {
            //SEND ERROR HERE
            bundle.putString(Intent.EXTRA_TEXT, "INVALID REQUEST TYPE");
            receiver.send(STATUS_ERROR, bundle);
            return;
        }
        if (type == TYPE_TRANS_HIST) {

//            if(search){
                refreshTransactionHistory(intent,search,tran_type_selected,mobile_no_for_search);
//            }else {
//                refreshTransactionHistory(intent,search,tran_type_selected,mobile_no_for_search);
//            }
        } else if (type == TYPE_USER_LOGGED_IN_STATUS) {
            //checkUserLoggedInStatus(intent);
        } else {
            bundle.putString(Intent.EXTRA_TEXT, "REQUEST TYPE NOT SUPPORTED");
            receiver.send(STATUS_ERROR, bundle);
        }
        this.stopSelf();
    }

    private void refreshTransactionHistory(Intent intent, boolean search,String tran_type_selected, String mobile_no_for_search) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Bundle bundle = new Bundle();

        long from = intent.getLongExtra("from", -1L);
        if (from == -1L) {
            bundle.putString(Intent.EXTRA_TEXT, "FROM REQUIRED");
            receiver.send(STATUS_ERROR, bundle);
            return;
        }
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        MerchantLoginRequestResponse onlineLoginResponse = ((CoreApplication) getApplicationContext()).getMerchantLoginRequestResponse();
        TransactionHistoryRequest transactionHistoryRequest = new TransactionHistoryRequest();
        transactionHistoryRequest.setG_transType(TransType.MERCHANT_TRAN_HISTORY_REQUEST.name());
        String authtoken = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.AUTH_TOKEN);
        transactionHistoryRequest.setG_oauth_2_0_client_token(authtoken);
        transactionHistoryRequest.setFrom((int) from);



        if(tran_type_selected!=null && tran_type_selected.length()>0){



            transactionHistoryRequest.setTranType(tran_type_selected);

            if(mobile_no_for_search!=null && mobile_no_for_search.length()==8) {

                transactionHistoryRequest.setMobileNo(mobile_no_for_search);

            }


        }else{

            transactionHistoryRequest.setTranType("all");
        }



        String json = new Gson().toJson(transactionHistoryRequest);
        StringBuffer buffer = new StringBuffer();
        buffer.append(TransType.MERCHANT_TRAN_HISTORY_REQUEST.getURL());

        buffer.append("?d=" + URLUTF8Encoder.encode(json));



//        if(search && mobile_no_for_search!=null && mobile_no_for_search.length()==8) {
//
//            buffer.append("?d={" + URLUTF8Encoder.encode(json));
//
//        }else if(search){

//            buffer.append("?d={" + URLUTF8Encoder.encode(json)+"}");

//            buffer.append("?d=" + URLUTF8Encoder.encode(json));
//        }
//        else{
//            buffer.append("?d=" + URLUTF8Encoder.encode(json));
//        }



//        if(mobile_no_for_search!=null && mobile_no_for_search.length()==8 ) {
//
//        String mobileJoStr = "";
//        try {
//            JSONObject mobileNoJo = new JSONObject();
//            mobileNoJo.put("mobileNo", "98037942");
//            mobileJoStr = mobileNoJo.toString();
//
//            mobileJoStr = mobileJoStr.replace("{","");
//        }catch(Exception e){
//
//        }
//
//            buffer.append(","+mobileJoStr);
//        }


        Message msg = ServerConnection.directNonHandlerHTTPClient(buffer.toString(), -1);
        if (msg.arg1 == ServerConnection.OPERATION_SUCCESS) {
            try {
                GenericResponse response = new Gson().fromJson((String) msg.obj, GenericResponse.class);
                if (null != response) {
                    if (response != null && (response.getG_errorDescription().trim().equalsIgnoreCase("E116") || response.getG_errorDescription().trim().equalsIgnoreCase("E117"))) {
                        receiver.send(STATUS_SESSION_INVALID, Bundle.EMPTY);
                        return;
                    }
                    if (response != null && response.getG_response_trans_type().equalsIgnoreCase(TransType.MERCHANT_TRAN_HISTORY_RESPONSE.name()) && response.getG_status() != 1) {
                        bundle.putString(Intent.EXTRA_TEXT, response.getG_errorDescription());
                        receiver.send(STATUS_ERROR, bundle);
                        return;
                    }

                    //MOHIT 11-12-2015
                    //There was problem in Mobile Device While De Serializing this,
                    //Now Performming manual JSON DE PARSE FOR ENTIRE
                    //BEST IS TO CREATE DE SERIALIZE IMPL IN GSON --> FOR FUTURE
                    //TransactionHistoryResponse res = new Gson().fromJson((String)msg.obj,TransactionHistoryResponse.class);
                    TransactionHistoryResponse res = new TransactionHistoryResponse();
                    //res.getRecords().clear();
                    JSONObject jsonObject = new JSONObject((String) msg.obj);
                    JSONArray jsonArray = jsonObject.getJSONArray("records");
                    if (jsonArray.length() > 0) {
                        ArrayList<TransTypeInterface> all = new ArrayList<TransTypeInterface>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            TransType type = TransType.valueOf(jsonArray.getJSONObject(i).getString("g_response_trans_type"));
                            String string = jsonArray.getJSONObject(i).toString();
                            switch (type) {//P2P received
                                case PAY_TO_MERCHANT_RESPONSE:
                                    all.add(new Gson().fromJson(string, PayToMerchantRequestResponse.class));
                                    break;
                                case PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE:
                                    all.add(new Gson().fromJson(string, PayToMerchantCommitRequestResponse.class));
                                    break;
                                case INVOICE_TRAN_RESPONSE:
                                    all.add(new Gson().fromJson(string, InvoiceTranHistoryResponsePojo.class));
                                    break;
                                default:
                                    all.add(new Gson().fromJson(string, GenericResponse.class));
                                    break;
                            }
                        }
                        res.setRecords(all);
                        res.setTotalNoOfTransactions(jsonObject.getLong("totalNoOfTransactions"));
                        res.setTo(jsonObject.getLong("to"));
                        res.setFrom(jsonObject.getLong("from"));
                        TransactionHistoryResponse coreResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();
                        if (coreResponse.getRecords().size() == 0) {
                            //BLANKET OVERWRITE
                            ((CoreApplication) getApplicationContext()).setTransactionHistoryResponse(res);
                        } else {


                            if(search) {

                                //ONLY SEAR TRANSACTIONS
                                coreResponse.getRecords().clear();
                                coreResponse.getRecords().addAll(all);
                                coreResponse.setFrom(res.getFrom());
                                coreResponse.setTo(res.getTo());
                                coreResponse.setTotalNoOfTransactions(res.getTotalNoOfTransactions());

                            }else {
                                //CONTENT ADDITION
                                coreResponse.getRecords().addAll(all);
                                coreResponse.setFrom(res.getFrom());
                                coreResponse.setTo(res.getTo());
                                coreResponse.setTotalNoOfTransactions(res.getTotalNoOfTransactions());
                            }
                        }
                        bundle.putInt("status", msg.arg1);
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                }
            } catch (Exception e) {
                bundle.putInt("status", ServerConnection.OPERATION_FAILURE_GENERAL_SERVER);
                //Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
                bundle.putString(Intent.EXTRA_TEXT, "");
                receiver.send(STATUS_ERROR, bundle);
            }
        } else if (msg.arg1 == ServerConnection.OPERATION_FAILURE_GENERAL_SERVER) {
            bundle.putInt("status", msg.arg1);
            bundle.putString(Intent.EXTRA_TEXT, "SERVER ERROR");
            receiver.send(STATUS_ERROR, bundle);
        } else if (msg.arg1 == ServerConnection.OPERATION_FAILURE_NETWORK) {
            bundle.putInt("status", msg.arg1);
            bundle.putString(Intent.EXTRA_TEXT, "NETWORK ERROR");
            receiver.send(STATUS_ERROR, bundle);
        }
    }
//    void checkUserLoggedInStatus(Intent intent){
//        //Check if the user is still logged in
//        //if not close all activities and inform:
//        CoreApplication application = (CoreApplication)getApplicationContext();
//        MerchantLoginRequestResponse onlineLoginResponse = application.getMerchantLoginRequestResponse();
//        if(application.isUserLoggedIn()){
//            GenericRequest request = new GenericRequest();
//            request.setG_oauth_2_0_client_token(onlineLoginResponse.getToken());
//            request.setG_transType(TransType.VALIDATE_TOKEN.name());
//            StringBuffer buffer = new StringBuffer();
//            buffer.append((((CoreApplication) getApplicationContext()).getGlobalAppProperty("ONLINE_SERVER")));
//            buffer.append(URLUTF8Encoder.encode(new Gson().toJson(request)));
//            Message msg = ServerConnection.directNonHandlerHTTPClient(buffer.toString(),-1);
//            if(msg.arg1==ServerConnection.OPERATION_SUCCESS){
//                GenericResponse response = new Gson().fromJson((String)msg.obj,GenericResponse.class );
//                if(response!=null && response.getG_status()== MWalletStatusCodesAndDescriptionsEnum.SUCCESS.getStatusCode()){
//                    //DON'T DO ANY THING| ALL IS WELL
//                }else{
//                    //LOG OFF THE USER, TAKE THE USER TO LOGIN SCREEN:
//                    application.setIsUserLoggedIn(false);
//                    application.setOnlineLoginResponse(new OnlineLoginResponse());
//                    sendBroadcast(new Intent(SecMsgGenericActivity.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
//                    Intent intent1 = new Intent();
//                    intent1.putExtra("session_out",Boolean.TRUE);
//                    intent1.putExtra("force_crash",Boolean.TRUE);
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent1.setClass(getBaseContext(), OnlineLoginActivity.class);
//                    startActivity(intent1);
//                }
//            }
//        }else{
//            ((CoreApplication)getApplicationContext()).cancelUserLoggedInStatusAlarmManager();
//        }
//    }
}

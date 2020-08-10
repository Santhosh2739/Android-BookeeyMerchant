package wallet.ooredo.com.live.transactionhistory;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import coreframework.database.CustomSharedPreferences;
import coreframework.taskframework.YPCHeadlessCallback;
import coreframework.utils.PriceFormatter;
import coreframework.utils.TimeUtils;
import coreframework.utils.TransType;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.application.DownloadResultReceiver;
import wallet.ooredo.com.live.application.SyncService;
import wallet.ooredo.com.live.forceface.TransTypeInterface;
import wallet.ooredo.com.live.merchantlogin.MerchantLoginActivity;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.invoice_pojo.InvoiceTranHistoryResponsePojo;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantCommitRequestResponse;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantRequestResponse;
import ycash.wallet.json.pojo.transactionhistory.TransactionHistoryResponse;

/**
 * Created by mohit on 6/1/16.
 */
public class TransactionHistoryActivity extends ListActivity implements DownloadResultReceiver.Receiver, YPCHeadlessCallback , AdapterView.OnItemSelectedListener {

    private Handler handler = new Handler();
    private CustomList adapter = null;
    private DownloadResultReceiver mReceiver = null;
    private View loadMoreView;
    boolean loadingMore = false;
    boolean errorMsg = true;
    private TextView txn_refresh;
    private ImageView txn_refresh_image;
    boolean refreshClicked = false;
    ProgressBar Pbar;

    // //////////////////////SRCODES////////////////////////////////////////////////////////
    public static final byte SRCODE_0B_INSUFFICIENT_BALANCE = 1;
    public static final byte SRCODE_0C_DUPLICATE_COUNTER = 2;
    public static final byte SRCODE_0D_SUCCESS = 5;
    public static final byte SRCODE_0E_REJECTED_BY_MERCHANT = 6;

    public static final byte SRCODE_0EA_PROBLEM_IN_PROCESSING = 7;

    public static final byte SRCODE_0F_USER_INACTIVE = 10;

    public static final byte SRCODE_0_PROCESSED_TIMED_OUT = 11;

    public static final byte SRCODE_00A_SEND_MONEY_INSTAPAY_AUTHORIZED = 12;

    public static final byte SRCODE_0A_RECHARGE_SUCCESS = 13;


    // Sender Wallet Expired = 24
    public static final byte SRCODE_1_SENDER_WALLET_EXPIRED = 24;// L1
    // P2P REC Wallet Balance Reached = 30

    public static final byte SRCODE_1A_TRANSACTION_AMOUNT_LESS_THAN_MINIMUM = 29;// L1

    // Re charge declined by merchant
    public static final byte SRCODE_1ZA_RECHARGE_DECLINED_BY_MERCHANT = 25;
    // Transaction daily limit reached
    public static final byte SRCODE_1ZB_TRANSACTION_DAILY_LIMIT_REACHED = 26;
    // Transaction Monthly Limit Reached
    public static final byte SRCODE_1ZC_TRANSACTION_MONTHLY_LIMIT_REACHED = 27;
    // Transaction Single Limit Reached
    public static final byte SRCODE_1ZD_TRANSACTION_SINGLE_LIMIT_REACHED = 28;

    public static final byte SRCODE_2_SMM_RECEIVER_BALANCE_BREACHED = 30;// L1

    // receiver balance upon re charge is not reaching a defined limit
    public static final byte SRCODE_2A_SMM_RECEIVER_RECHARGE_AMOUNT_LESS_THAN_MIN_BALANCE = 31;

    // Operation Performed Success
    public static final byte SRCODE_3_OPERATION_SUCCESS = 35;
    // Condition Of Usage Not Satisfied
    public static final byte SRCODE_4_CONDITION_NOT_SATISFIED = 36;
    // Waiting For Approval - 37
    public static final byte SRCODE_5_SMM_WAITING_FOR_APPROVAL = 37;// L1

    // Waiting For bus booking  Approval - 12
    public static final byte SRCODE_5_SMM_WAITING_FOR_BUS_BOOKING_APPROVAL = 12;// L1

    // Sender or Current User Inactive
    public static final byte SRCODE_6_USER_INAVTIVE = 38;// L1
    // Remote USer Inactive = 39
    public static final byte SRCODE_7_SMM_RECEIVER_NOT_ACIVE_DORMANT = 39;// L1
    /**
     * This is time error ; breached minimum up difference and low difference
     * Please check the system time
     */
    public static final byte SRCODE_7A_SMM_TIME_ERROR = 40;

//    public static final byte SRCODE_8_TPIN_STATE_BLOCKED = TRANSACTION_PIN_STATE.STATE_BLOCKED;// L1//42
//    public static final byte SRCODE_9_TPIN_STATE_CHANGE_ENFORCED = TRANSACTION_PIN_STATE.STATE_CHANGE_FORCED;// L1//43
//    public static final byte SRCODE_10_TPIN_STATE_INITIALIZED = TRANSACTION_PIN_STATE.STATE_INITIALIZED;// /44
//    public static final byte SRCODE_11_TPIN_STATE_NOT_INITIALIZED = TRANSACTION_PIN_STATE.STATE_NOT_INITIALIZED;// L1//45

    public static final byte SRCODE_11A_WRONG_TPIN = 46;// L2

    // Receiver RX Mobile Not Registered = 47
    public static final byte SRCODE_12_SMM_RECEIVER_NOT_REGISTERED = 47;// L1
    // Sender Balance Insufficient = 48
    public static final byte SRCODE_13_SMM_SENDER_BALANCE_INSUFFICIENT = 48;// L1
    // Sender Daily Limit Reached = 49
    public static final byte SRCODE_14_SMM_SENDER_DAILY_LIMIT_REACHED = 49;// L1
    // Sender Monthly Limit Reached = 50
    public static final byte SRCODE_15_SMM_SENDER_MONTHLY_LIMIT_REACHED = 50;// L1
    // P2P Receiver Daily Limit Reached = 51
    public static final byte SRCODE_16_SMM_RECEIVER_DAILY_LIMIT_REACHED = 51;// L1
    // P2P REC Monthly Limit Reached = 52
    public static final byte SRCODE_17_SMM_RECEIVER_MONTHLY_LIMIT_REACHED = 52;// L1
    // P2P User Not Allowed Send = 53
    public static final byte SRCODE_18_SMM_USER_NOT_ALLOWED_TO_SEND = 53;// L1
    // Internal Error - Rare Event
    public static final byte SRCODE_19_INTERNAL_ERROR = 54;
    // Duplicate Request - 55
    public static final byte SRCODE_20_DUPLICATE_REQUEST = 55;// L1

    public static final byte SRCODE_20A_OLD_AND_NEW_TPIN_ARE_SAME = 56;

    // Sender Single Transaction Exceeded = 57
    public static final byte SRCODE_21_SMM_SENDER_SINGLE_TRANSACTION_EXCEEDED = 57;// L1
    // Transaction Amount Less Than Minimum = 58
    public static final byte SRCODE_22_SMM_TRANSACTION_AMOUNT_LESS_THAN_MINIMUM = 58;// L1
    // Sender And Receiver Are Same = 59
    public static final byte SRCODE_23_SMM_SENDER_RECEIVER_SAME = 59;// L1
    // Receiver Wallet Expired = 60
    public static final byte SRCODE_24_RECEIVER_WALLET_EXPIRED = 60;// L1
    // P2P Receiver Not Allowed To Received = 61
    public static final byte SRCODE_25_SMM_USER_NOT_ALLOWED_TO_RECEIVE = 61;// L1

    public static final byte SRCODE_26_SMM_TRANSACTION_DECLINED_BY_SENDER = 62;// L2

    public static final byte SRCODE_27_SMM_USER_RECHARGE_NOT_SUCCESSFULL = 63;
    // //////////////////////SRCODES////////////////////////////////////////////////////////
    // /////////////INSTAPAY
    // CODES//////////////////////////////////////////////////////////////
    // IP_TRANSACTION_SUCCESSFUL_WALLET_DEBITED (68, "TXN",
    // "Transaction Successful, Wallet Debited		    	",
    // "Transaction Successful, Wallet Debited			"),
    // IP_TRANSACTION_UNDER_PROCESS_WALLET_DEBITED (69, "TUP",
    // "Transaction Under Process, Wallet Debited		",
    // "Transaction Under Process, Wallet Debited		"),
    // IP_INVALID_REFILL_AMOUNT (70, "IRA", "Invalid Refill Amount					",
    // "Invalid Refill Amount					"),
    // IP_REFILL_BARRED_TEMPORARILY_CONTACT_SERVICE_PROVIDER(71, "RBT",
    // "Refill Barred Temporarily, Contact Service Provider	",
    // "Refill Barred Temporarily, Contact Service Provider	"),
    // IP_INVALID_ACCOUNT_NUMBER (72, "IAN", "Invalid Account Number					",
    // "Invalid Account Number					"),
    // IP_INSUFFICIENT_AGENT_BALANCE (73, "IAB",
    // "Insufficient Agent Balance				", "Internal error,try again later				"),
    // IP_DUPLICATE_TRANSACTION_TRY_AFTER_60_MINUTES (74, "DTX",
    // "Duplicate Transaction, Try After 60 Minutes		",
    // "Duplicate Transaction, Try After 60 Minutes		"),
    // IP_SYSTEM_ERROR_TRY_AGAIN_LATER (75, "ISE",
    // "System Error, Try Again Later				",
    // "System Error, Try Again Later				"),
    // IP_INVALID_ACCESS_TOKEN (76, "IAT", "Invalid Access Token					",
    // "Internal error,try again later				"),
    // IP_SERVICE_PROVIDER_DOWNTIME (77, "SPD", "Service Provider Downtime				",
    // "Service Provider Downtime				"),
    // IP_SERVICE_PROVIDER_ERROR_TRY_AGAIN_LATER (78, "SPE",
    // "Service Provider Error, Try Again Later		",
    // "Service Provider Error, Try Again Later		"),
    // IP_INVALID_TRANSACTION_ID (79, "ITI", "Invalid Transaction Id					",
    // "Internal error,try again later				"),
    // IP_DENOMINATION_TEMPORARILY_BARRED (80, "DTB",
    // "Denomination Temporarily Barred			",
    // "Denomination Temporarily Barred			"),
    // IP_TRANSACTION_STATUS_UNAVAILABLE_TRY_AGAIN (81, "TSU",
    // "Transaction Status Unavailable, Try Again		",
    // "Transaction Status Unavailable, Try Again		"),
    // IP_INVALID_SERVICE_PROVIDER (82, "ISP", "Invalid Service Provider				",
    // "Invalid Service Provider				"),
    // IP_REQUEST_PARAMETERS_ARE_INVALID_OR_INCOMPLETE (83, "RPI",
    // "Request Parameters are Invalid or Incomplete		",
    // "Internal error,try again later				"),
    // IP_AGENT_ACCOUNT_BLOCKED_CONTACT_HELPDESK (84, "AAB",
    // "Agent Account Blocked, Contact Helpdesk		",
    // "Internal error,try again later				"),
    // IP_UNKNOWN_ERROR_DESCRIPTION_CONTACT_HELPDESK (85, "UED",
    // "Unknown Error Description, Contact Helpdesk		",
    // "Unknown Error Description, Contact Helpdesk		"),
    // IP_INVALID_ERROR_CODE (86, "IEC", "Invalid Error Code					",
    // "Internal error,try again later				"),
    // IP_INVALID_RESPONSE_TYPE (87, "IRT", "Invalid Response Type					",
    // "Internal error,try again later				"),
    // IP_INTERNAL_PROCESSING_ERROR (88, "IPE", "Internal Processing Error				",
    // "Internal error,try again later				"),
    // IP_INVALID_AGENT_CREDENTIALS (89, "IAC", "Invalid Agent Credentials				",
    // "Internal error,try again later				"),
    // IP_USER_ACCESS_DENIED (90, "UAD", "User Access Denied					",
    // "Internal error,try again later				"),
    // IP_TRANSACTION_REFUND_PROCESSED_WALLET_CREDITED (91, "TRP",
    // "Transaction Refund Processed, Wallet Credited          ",
    // "Transaction Refund Processed, Wallet Credited		");

    public static final byte SRCODE_28_IP_TRANSACTION_SUCCESSFUL_WALLET_DEBITED = 68;// "TXN",
    // "Transaction Successful, Wallet Debited		    	",
    // "Transaction Successful, Wallet Debited			"),
    public static final byte SRCODE_29_IP_TRANSACTION_UNDER_PROCESS_WALLET_DEBITED = 69;// "TUP",
    // "Transaction Under Process, Wallet Debited		",
    // "Transaction Under Process, Wallet Debited		"),
    public static final byte SRCODE_30_IP_INVALID_REFILL_AMOUNT = 70;// "IRA",
    // "Invalid Refill Amount					",
    // "Invalid Refill Amount					"),
    public static final byte SRCODE_31_IP_REFILL_BARRED_TEMPORARILY_CONTACT_SERVICE_PROVIDER = 71;// "RBT",
    // "Refill Barred Temporarily, Contact Service Provider	",
    // "Refill Barred Temporarily, Contact Service Provider	"),
    public static final byte SRCODE_32_IP_INVALID_ACCOUNT_NUMBER = 72;// "IAN",
    // "Invalid Account Number					",
    // "Invalid Account Number					"),
    public static final byte SRCODE_33_IP_INSUFFICIENT_AGENT_BALANCE = 73;// "IAB",
    // "Insufficient Agent Balance				",
    // "Internal error,try again later				"),
    public static final byte SRCODE_34_IP_DUPLICATE_TRANSACTION_TRY_AFTER_60_MINUTES = 74;// "DTX",
    // "Duplicate Transaction, Try After 60 Minutes		",
    // "Duplicate Transaction, Try After 60 Minutes		"),
    public static final byte SRCODE_35_IP_SYSTEM_ERROR_TRY_AGAIN_LATER = 75;// "ISE",
    // "System Error, Try Again Later				",
    // "System Error, Try Again Later				"),
    public static final byte SRCODE_36_IP_INVALID_ACCESS_TOKEN = 76;// "IAT",
    // "Invalid Access Token					",
    // "Internal error,try again later				"),
    public static final byte SRCODE_37_IP_SERVICE_PROVIDER_DOWNTIME = 77;// "SPD",
    // "Service Provider Downtime				",
    // "Service Provider Downtime				"),
    public static final byte SRCODE_38_IP_SERVICE_PROVIDER_ERROR_TRY_AGAIN_LATER = 78;// "SPE",
    // "Service Provider Error, Try Again Later		",
    // "Service Provider Error, Try Again Later		"),
    public static final byte SRCODE_39_IP_INVALID_TRANSACTION_ID = 79;// "ITI",
    // "Invalid Transaction Id					",
    // "Internal error,try again later				"),
    public static final byte SRCODE_40_IP_DENOMINATION_TEMPORARILY_BARRED = 80;// "DTB",
    // "Denomination Temporarily Barred			",
    // "Denomination Temporarily Barred			"),
    public static final byte SRCODE_41_IP_TRANSACTION_STATUS_UNAVAILABLE_TRY_AGAIN = 81;// "TSU",
    // "Transaction Status Unavailable, Try Again		",
    // "Transaction Status Unavailable, Try Again		"),
    public static final byte SRCODE_42_IP_INVALID_SERVICE_PROVIDER = 82;// "ISP",
    // "Invalid Service Provider				",
    // "Invalid Service Provider				"),
    public static final byte SRCODE_43_IP_REQUEST_PARAMETERS_ARE_INVALID_OR_INCOMPLETE = 83;// "RPI",
    // "Request Parameters are Invalid or Incomplete		",
    // "Internal error,try again later				"),
    public static final byte SRCODE_44_IP_AGENT_ACCOUNT_BLOCKED_CONTACT_HELPDESK = 84;// "AAB",
    // "Agent Account Blocked, Contact Helpdesk		",
    // "Internal error,try again later				"),
    public static final byte SRCODE_45_IP_UNKNOWN_ERROR_DESCRIPTION_CONTACT_HELPDESK = 85;// "UED",
    // "Unknown Error Description, Contact Helpdesk		",
    // "Unknown Error Description, Contact Helpdesk		"),
    public static final byte SRCODE_46_IP_INVALID_ERROR_CODE = 86;// "IEC",
    // "Invalid Error Code					",
    // "Internal error,try again later				"),
    public static final byte SRCODE_47_IP_INVALID_RESPONSE_TYPE = 87;// "IRT",
    // "Invalid Response Type					",
    // "Internal error,try again later				"),
    public static final byte SRCODE_48_IP_INTERNAL_PROCESSING_ERROR = 88;// "IPE",
    // "Internal Processing Error				",
    // "Internal error,try again later				"),
    public static final byte SRCODE_49_IP_INVALID_AGENT_CREDENTIALS = 89;// "IAC",
    // "Invalid Agent Credentials				",
    // "Internal error,try again later				"),
    public static final byte SRCODE_50_IP_USER_ACCESS_DENIED = 90;// "UAD",
    // "User Access Denied					",
    // "Internal error,try again later				"),
    public static final byte SRCODE_51_IP_TRANSACTION_REFUND_PROCESSED_WALLET_CREDITED = 91;// "TRP",
    // "Transaction Refund Processed, Wallet Credited          ",
    // "Transaction Refund Processed, Wallet Credited		");

    public static final byte SRCODE_52_P2B_TUP_WALLET_DEBITED_TRANSACTION_IN_PROCESS = 93;
    public static final byte SRCODE_53_P2B_TRANSFER_SUCCESS = 94;
    public static final byte SRCODE_54_P2B_TRANSFER_FAILED = 95;
    public static final byte SRCODE_55_P2E_TXN_AUTHOURIZE_BY_CUST = 96;

    //New Entry |started as special case for bus booking
    public static final byte SRCODE_14_RECORD_NOT_FOUND = 14;

    // 96 97 98 99 {IFSC, ACCOUNT NUMBER, BEN NAME, ACCOUNT TYPE}

    //This values are mapped as paired with the document specification in peer to peer payment scheme:
    public static final int STATE_NOT_INITIALIZED = 0x2D;//45
    public static final int STATE_INITIALIZED = 0x2C;//44
    public static final int STATE_BLOCKED = 0x2A;//42
    public static final int STATE_CHANGE_FORCED = 0x2B;//43
    //This is an internal state and shall never send as server response to client for querying the pin state
    public static final int STATE_UNKNOWN = 0x80;

    List<String> tran_types = new ArrayList<String>();
//    String[] tran_types = { "Filter by payment status","All",  "SUCCESS","PENDING", "FAILED"};
    private EditText edit_search_by_mobile_no;

    String tran_type_selected = "";
    String mobile_no_for_search= "";

    boolean search = false;
    Spinner spin = null;

    private static final String KEY_TRANTYPE_SELECTED_INDEX = "KEY_TRANTYPE_SELECTED_INDEX";

//    private ProgressDialog dialog;

    private boolean searchEnabled = false;
    int listsize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        dialog = new ProgressDialog(this);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.txn_history_list);
        // test=(TextView)findViewById(R.id.test);
        txn_refresh = (TextView) findViewById(R.id.txn_refresh);
        txn_refresh_image=(ImageView)findViewById(R.id.txn_refresh_image);
        Pbar = (ProgressBar)findViewById(R.id.progressBar1);



//        tran_types.add("Filter by payment status");
        tran_types.add("All");
        tran_types.add("SUCCESS");
        tran_types.add("PENDING");
        tran_types.add("REJECTED");
        tran_types.add("FAILED");

        listsize = tran_types.size() - 1;


        spin = (Spinner) findViewById(R.id.txn_filter);
//        spin.setPrompt("Filter by payment status");
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
//        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,tran_types);
        spin.setAdapter(new CustomAdapter(this, tran_types));
//        spin.setSelection(listsize);

        int initialposition = spin.getSelectedItemPosition();
        spin.setSelection(initialposition, false);


//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
//        spin.setAdapter(aa);

        edit_search_by_mobile_no = (EditText)findViewById(R.id.edit_search_by_mobile_no);


        edit_search_by_mobile_no.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    hideKeyBoard();


                    String enteredMobileNo = edit_search_by_mobile_no.getText().toString();


                    if (enteredMobileNo.length()>0 && enteredMobileNo.length() == 8) {




                        searchEnabled = true;

                        CustomSharedPreferences.saveStringData(getApplicationContext(), enteredMobileNo, CustomSharedPreferences.SP_KEY.KEY_SEARCH_MOBILE_NO);

                        //Search will starts
                        search = true;

                        mobile_no_for_search = edit_search_by_mobile_no.getText().toString().trim();

                        requestLoadingOfList(false, true, tran_type_selected, mobile_no_for_search);


                        refreshClicked = true;
                        adapter.clear();
                        ((CoreApplication) getApplication()).setTransactionHistoryResponse(new TransactionHistoryResponse());
                        TransactionHistoryResponse coreResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();
                        coreResponse.setRecords(new ArrayList<TransTypeInterface>());
                        TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplication()).getTransactionHistoryResponse();
                        adapter = new CustomList((Activity) TransactionHistoryActivity.this, transactionHistoryResponse.getRecords());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                setListAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });


                    }else{

                        Toast.makeText(TransactionHistoryActivity.this,"Please enter valid mobile no",Toast.LENGTH_LONG).show();


                    }


                    if (enteredMobileNo.length() == 0) {


                        requestLoadingOfList(false, true, tran_type_selected, mobile_no_for_search);


                        refreshClicked = true;
                        adapter.clear();
                        ((CoreApplication) getApplication()).setTransactionHistoryResponse(new TransactionHistoryResponse());
                        TransactionHistoryResponse coreResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();
                        coreResponse.setRecords(new ArrayList<TransTypeInterface>());
                        TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplication()).getTransactionHistoryResponse();
                        adapter = new CustomList((Activity) TransactionHistoryActivity.this, transactionHistoryResponse.getRecords());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                setListAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });


                    }





                    return true;
                }
                return false;
            }
        });


//TEST
//        edit_search_by_mobile_no.setText("60064534");

//        edit_search_by_mobile_no.setText("65727940");




        edit_search_by_mobile_no.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {












            }
        });



//        ActionBar abar = getActionBar();
//        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_new,
//                null);
//        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
//                // Center the textview in the ActionBar !
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
//
//        TextView textviewTitle = (TextView) viewActionBar
//                .findViewById(R.id.actionbar_textview);
//
//        textviewTitle.setText("Transaction Summary");
//        abar.setCustomView(viewActionBar, params);
//        abar.setDisplayShowCustomEnabled(true);
//        abar.setDisplayShowTitleEnabled(false);
//        abar.setDisplayHomeAsUpEnabled(false);
//        abar.setDisplayUseLogoEnabled(true);
//        abar.setLogo(R.drawable.icon);
//        abar.setHomeButtonEnabled(true);

        txn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txn_refresh_image.performClick();
            }
        });
        txn_refresh_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txn_refresh_image.setVisibility(View.GONE);
                Pbar.setVisibility(View.VISIBLE);
                refreshClicked = true;
                adapter.clear();
                ((CoreApplication) getApplication()).setTransactionHistoryResponse(new TransactionHistoryResponse());
                TransactionHistoryResponse coreResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();
                coreResponse.setRecords(new ArrayList<TransTypeInterface>());
                TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplication()).getTransactionHistoryResponse();
                adapter = new CustomList((Activity) TransactionHistoryActivity.this, transactionHistoryResponse.getRecords());
                runOnUiThread(new Runnable() {
                    public void run() {
                        setListAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        if (!refreshClicked) {
            TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplication()).getTransactionHistoryResponse();
            adapter = new CustomList((Activity) TransactionHistoryActivity.this, transactionHistoryResponse.getRecords());
            runOnUiThread(new Runnable() {
                public void run() {
                    setListAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            });
        }
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        loadMoreView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loadmore, null, false);

        runOnUiThread(new Runnable() {
            public void run() {
                getListView().addFooterView(loadMoreView);
                notifyAdapter();

            }
        });



       /* loadMoreView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.loadmore, null);
        getListView().addFooterView(loadMoreView, null, false);*/
            getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                    if ((lastInScreen == totalItemCount) && !(loadingMore)) {

//                        if(search) {
//                            requestLoadingOfList(false, true,tran_type_selected,mobile_no_for_search);
//                        }else{
//                            requestLoadingOfList(false, false,tran_type_selected,mobile_no_for_search);
//                        }

//                        hideKeyBoard();
                        searchEnabled = false;
                        requestLoadingOfList(false, false,tran_type_selected,mobile_no_for_search);

                    }

                }
            });
            initializeIcons();


        Button transaction_history_search = (Button)findViewById(R.id.transaction_history_search);
        transaction_history_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                search = true;
                mobile_no_for_search = edit_search_by_mobile_no.getText().toString().trim();

                if (mobile_no_for_search.length() == 8) {

                    requestLoadingOfList(false, true, tran_type_selected, mobile_no_for_search);


                    refreshClicked = true;
                    adapter.clear();
                    ((CoreApplication) getApplication()).setTransactionHistoryResponse(new TransactionHistoryResponse());
                    TransactionHistoryResponse coreResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();
                    coreResponse.setRecords(new ArrayList<TransTypeInterface>());
                    TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplication()).getTransactionHistoryResponse();
                    adapter = new CustomList((Activity) TransactionHistoryActivity.this, transactionHistoryResponse.getRecords());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            setListAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });

                }else{

                    Toast.makeText(TransactionHistoryActivity.this,"Please enter valid mobile number", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }



    private class CustomAdapter extends BaseAdapter {
        Context context;
        List<String> item_list;
        String[] images;
        LayoutInflater inflter;

        public CustomAdapter(Context applicationContext, List<String> tranType_list) {
            this.context = applicationContext;
            this.item_list = tranType_list;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return item_list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = inflter.inflate(R.layout.spin_item, null);
            TextView namess = (TextView) view.findViewById(R.id.tvCust);
            namess.setText(item_list.get(position));
            return view;

        }
    }


    public void hideKeyBoard(){

       // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//        Toast.makeText(TransactionHistoryActivity.this,"Selected: "+parent.getSelectedItem().toString(),Toast.LENGTH_LONG).show();


        hideKeyBoard();

        searchEnabled = true;

        if(position>0){

            CustomSharedPreferences.saveIntData(getApplicationContext(), position, CustomSharedPreferences.SP_KEY.KEY_TRANTYPE_SELECTED_INDEX);

//            tran_type_selected = parent.getSelectedItem().toString().toLowerCase();

            tran_type_selected  = tran_types.get(position).toString().toLowerCase();

            if(tran_type_selected.equalsIgnoreCase("REJECTED")) {

                tran_type_selected = "reject";

            }




            //Search will starts
            search = true;
            mobile_no_for_search = edit_search_by_mobile_no.getText().toString().trim();

//            if (mobile_no_for_search.length() == 8) {

                requestLoadingOfList(false, true, tran_type_selected, mobile_no_for_search);


                refreshClicked = true;
                adapter.clear();
                ((CoreApplication) getApplication()).setTransactionHistoryResponse(new TransactionHistoryResponse());
                TransactionHistoryResponse coreResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();
                coreResponse.setRecords(new ArrayList<TransTypeInterface>());
                TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplication()).getTransactionHistoryResponse();
                adapter = new CustomList((Activity) TransactionHistoryActivity.this, transactionHistoryResponse.getRecords());
                runOnUiThread(new Runnable() {
                    public void run() {
                        setListAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });

//            }else{

//                Toast.makeText(TransactionHistoryActivity.this,"Please enter valid mobile number", Toast.LENGTH_LONG).show();
//                return;
//            }

        }else{

            tran_type_selected = "all";


            //Search will starts
            search = true;
            mobile_no_for_search = edit_search_by_mobile_no.getText().toString().trim();

//            if (mobile_no_for_search.length() == 8) {

            requestLoadingOfList(false, true, tran_type_selected, mobile_no_for_search);


            refreshClicked = true;
            adapter.clear();
            ((CoreApplication) getApplication()).setTransactionHistoryResponse(new TransactionHistoryResponse());
            TransactionHistoryResponse coreResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();
            coreResponse.setRecords(new ArrayList<TransTypeInterface>());
            TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplication()).getTransactionHistoryResponse();
            adapter = new CustomList((Activity) TransactionHistoryActivity.this, transactionHistoryResponse.getRecords());
            runOnUiThread(new Runnable() {
                public void run() {
                    setListAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });



        }


//        hideKeyBoard();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }





    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }*/

    void requestLoadingOfList(boolean requestRefresh, boolean search, String tran_type_selected, String mobile_no_for_search) {

//        dialog.setMessage("Fetching data, please wait.");
//        dialog.show();


        long from = -1;
        TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplication()).getTransactionHistoryResponse();
        if (transactionHistoryResponse.getRecords().size() == 0) {
            from = 1;
        } else if (transactionHistoryResponse.getRecords().size() >= transactionHistoryResponse.getTotalNoOfTransactions()) {
            from = -1;
        } else if (transactionHistoryResponse.getTo() < transactionHistoryResponse.getTotalNoOfTransactions()) {
            from = transactionHistoryResponse.getTo() + 1L;
        }

        if (from != -1L) {
            /* Starting Download Service */
            loadingMore = true;
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
            intent.putExtra("receiver", mReceiver);
            intent.putExtra("type", SyncService.TYPE_TRANS_HIST);

            if(searchEnabled) {
                intent.putExtra("from", 1);
            }else{
                intent.putExtra("from", from);
            }

            intent.putExtra(SyncService.TYPE_TRANS_TYPE_FOR_HIST_FOR_SEARCH,tran_type_selected);
            intent.putExtra(SyncService.TYPE_MOBILE_NO_FOR_HIST_FOR_SEARCH,mobile_no_for_search);

//            if(search) {
//                intent.putExtra(SyncService.TYPE_TRANS_HIST_FOR_SEARCH, true);
//                intent.putExtra(SyncService.TYPE_TRANS_TYPE_FOR_HIST_FOR_SEARCH,tran_type_selected);
//                intent.putExtra(SyncService.TYPE_MOBILE_NO_FOR_HIST_FOR_SEARCH,mobile_no_for_search);
//                intent.putExtra("from", 1L);
//            }

            startService(intent);


        } else {
            loadingMore = true;
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
            intent.putExtra("receiver", mReceiver);
            intent.putExtra("type", SyncService.TYPE_TRANS_HIST);

            if(searchEnabled) {
                intent.putExtra("from", 1);
            }else{
                intent.putExtra("from", from);
            }


            intent.putExtra(SyncService.TYPE_TRANS_TYPE_FOR_HIST_FOR_SEARCH,tran_type_selected);
            intent.putExtra(SyncService.TYPE_MOBILE_NO_FOR_HIST_FOR_SEARCH,mobile_no_for_search);

//            if(search) {
//                intent.putExtra(SyncService.TYPE_TRANS_HIST_FOR_SEARCH, true);
//                intent.putExtra(SyncService.TYPE_TRANS_TYPE_FOR_HIST_FOR_SEARCH,tran_type_selected);
//                intent.putExtra(SyncService.TYPE_MOBILE_NO_FOR_HIST_FOR_SEARCH,mobile_no_for_search);
//                intent.putExtra("from", 1L);
//            }

            startService(intent);
        }




    }

    private void notifyAdapter() {
        TransactionHistoryActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("onDestry","onDestry");

        CustomSharedPreferences.saveStringData(getApplicationContext(),"",CustomSharedPreferences.SP_KEY.KEY_SEARCH_MOBILE_NO);
        CustomSharedPreferences.saveIntData(getApplicationContext(),0,CustomSharedPreferences.SP_KEY.KEY_TRANTYPE_SELECTED_INDEX);

        Log.e("onDestry","onDestry");
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());

//        MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
//        new DownloadImageTask(back_logo).execute(merchantLoginRequestResponse.getMerchantLogo());


//        int selected_tran_type_index =  CustomSharedPreferences.getIntData(getApplicationContext(),CustomSharedPreferences.SP_KEY.KEY_TRANTYPE_SELECTED_INDEX);
//
//        if(selected_tran_type_index>=0) {
//            spin.setSelection(selected_tran_type_index);
//        }
//
//        String searched_mobile_no =  CustomSharedPreferences.getStringData(getApplicationContext(),CustomSharedPreferences.SP_KEY.KEY_SEARCH_MOBILE_NO);
//
//        if(searched_mobile_no.length()==8) {
//            edit_search_by_mobile_no.setText(searched_mobile_no);
//        }

    }

    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onProgressComplete() {

    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            pd.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
//            pd.dismiss();
            bmImage.setImageBitmap(result);
        }
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

//        dialog.dismiss();

//        hideKeyBoard();

        notifyAdapter();
        switch (resultCode) {
            case SyncService.STATUS_RUNNING:
                setProgressBarIndeterminateVisibility(true);
                break;
            case SyncService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                //setProgressBarIndeterminateVisibility(false);
                /* Update ListView with result */
                txn_refresh_image.setVisibility(View.VISIBLE);
                Pbar.setVisibility(View.GONE);
                    runOnUiThread(new Runnable() {
                        public void run() {


                            TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();

                            if (adapter.getCount() < transactionHistoryResponse.getRecords().size()) {
                                int start = adapter.getCount();
                                int count = transactionHistoryResponse.getRecords().size() - adapter.getCount();
                                while (count != 0) {


                                    adapter.add(transactionHistoryResponse.getRecords().get(start));

                                    adapter.notifyDataSetChanged();
                                    start++;
                                    count--;
                                }

                                //loadingMore = false;
                            }
                            loadingMore = false;
                        }
                    });

                /*TransactionHistoryResponse transactionHistoryResponse = ((CoreApplication) getApplicationContext()).getTransactionHistoryResponse();
                if (adapter.getCount() < transactionHistoryResponse.getRecords().size()) {
                    int start = adapter.getCount();
                    int count = transactionHistoryResponse.getRecords().size() - adapter.getCount();
                    while (count != 0) {
                        adapter.add(transactionHistoryResponse.getRecords().get(start));
                        adapter.notifyDataSetChanged();
                        start++;
                        count--;
                    }

                    //loadingMore = false;
                }
                loadingMore = false;*/
                break;
            case SyncService.STATUS_ERROR:
                /* Handle the error */

                /*runOnUiThread(new Runnable() {
                    public void run() {
                        getListView().removeFooterView(loadMoreView);
                        adapter.notifyDataSetChanged();
                    }
                });*/
                String errorMessage = "";
                for (String key : resultData.keySet()) {
                    errorMessage = "" + resultData.get(key);
                }
//                Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT).show();
                setProgressBarIndeterminateVisibility(false);
                String error = resultData.getString(Intent.EXTRA_TEXT);
                getListView().removeFooterView(loadMoreView);
                adapter.notifyDataSetChanged();
                //test.setVisibility(View.GONE);
                //Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
                if (errorMessage.equalsIgnoreCase("FROM REQUIRED")) {
                } else {
                    if (errorMsg)
                        Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    errorMsg = false;
                }
                loadingMore = false;
                break;
            case SyncService.STATUS_SESSION_INVALID:
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(getBaseContext(), "_gl_logged_out", Toast.LENGTH_LONG).show();
                CoreApplication application = (CoreApplication) getApplication();
                application.setIsUserLoggedIn(false);
                application.setMerchantLoginRequestResponse(new MerchantLoginRequestResponse());
                Intent i = new Intent(getBaseContext(), MerchantLoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("exit", Boolean.TRUE);
                startActivity(i);
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Object o = v.getTag();

        if (null == o) {
            //Toast.makeText(getBaseContext(), "Probably Footer Is Clicked:" + id + " &footerId=" + loadMoreView.getId(), Toast.LENGTH_LONG).show();
            return;
        }
        TransTypeInterface transTypeInterface = ((ViewHolder) v.getTag()).getTransTypeInterface();
        TransType type = TransType.valueOf(transTypeInterface.getG_response_trans_type());
        String json = null;
        String transType = null;
        Intent intent = new Intent();
        if (type.toString() != null) {

            switch (type) {
                case PAY_TO_MERCHANT_RESPONSE:
                    json = new Gson().toJson((PayToMerchantRequestResponse) transTypeInterface);
                    transType = type.name();
                    intent.putExtra("transaction", json);
                    intent.putExtra("type", transType);
                    intent.setClass(TransactionHistoryActivity.this, TransactionHistoryDisplayActivity.class);
                    startActivity(intent);
                    break;
                case PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE:
                    json = new Gson().toJson((PayToMerchantCommitRequestResponse) transTypeInterface);
                    transType = type.name();
                    intent.putExtra("transaction", json);
                    intent.putExtra("type", transType);
                    intent.setClass(TransactionHistoryActivity.this, TransactionHistoryDisplayActivity.class);
                    startActivity(intent);
                    break;
                case INVOICE_TRAN_RESPONSE:

                    json = new Gson().toJson((InvoiceTranHistoryResponsePojo) transTypeInterface);
                    transType = type.name();
                    intent.putExtra("transaction", json);
                    intent.putExtra("type", transType);
                    intent.setClass(TransactionHistoryActivity.this, TransactionHistoryDisplayActivity.class);
                    startActivity(intent);
                    break;

                default:
                    Toast.makeText(TransactionHistoryActivity.this, "Not Yet Implemented-" + type, Toast.LENGTH_SHORT).show();
                    break;
            }

        }


    }

    class CustomList extends ArrayAdapter<TransTypeInterface> {
        public CustomList(Activity context, ArrayList<TransTypeInterface> genericResponses) {
            super(context, R.layout.list_row, genericResponses);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            notifyAdapter();
            ViewHolder holder;
            // Get the data item for this position
            TransTypeInterface genericResponse = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.textView1);
                holder.icon = (ImageView) convertView.findViewById(R.id.imageView1);
                //holder.layout 		= (LinearLayout)convertView.findViewById(R.id.generic_list_view_layout);
                holder.textNextLine = (TextView) convertView.findViewById(R.id.textView2);
                holder.amount = (TextView) convertView.findViewById(R.id.textView3);
                holder.invoice_image = (ImageView) convertView.findViewById(R.id.invoice_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TransType type = TransType.valueOf(genericResponse.getG_response_trans_type());



            String send_version_response = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.SEND_VERSION_RESPONSE);
            String time_zone_str =  "";

            try {
                JSONObject send_version_response_jo = new JSONObject(send_version_response);
                time_zone_str = send_version_response_jo.getString("g_servertime");
            }catch(Exception e){

//                Toast.makeText(getApplicationContext(),"TimeZone Ex: "+e.getMessage(),Toast.LENGTH_LONG).show();

            }


            switch (type) {




                case PAY_TO_MERCHANT_RESPONSE:

                    PayToMerchantRequestResponse payToMerchantRequestResponse = (PayToMerchantRequestResponse) genericResponse;
                    holder.text.setText(payToMerchantRequestResponse.getCustomerMobileNumber());
                    holder.icon.setImageBitmap(getYPCCustomerLogsIcon(payToMerchantRequestResponse.getG_status()));
                    holder.textNextLine.setVisibility(View.VISIBLE);
                    holder.textNextLine.setText(TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(payToMerchantRequestResponse.getServerTime())));
                    holder.amount.setText("Amount"+" " + PriceFormatter.format(payToMerchantRequestResponse.getTxnAmount(), 3, 3));
                    holder.invoice_image.setVisibility(View.GONE);
                    break;
                case PAY_TO_MERCHANT_COMMIT_REQUEST_RESPONSE:


                    PayToMerchantCommitRequestResponse payToMerchantCommitRequestResponse = (PayToMerchantCommitRequestResponse) genericResponse;
                    holder.text.setText(payToMerchantCommitRequestResponse.getCustomerMobileNumber());
                    holder.icon.setImageBitmap(getYPCCustomerLogsIcon(payToMerchantCommitRequestResponse.getG_status()));
                    holder.textNextLine.setVisibility(View.VISIBLE);
                    holder.textNextLine.setText(TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(payToMerchantCommitRequestResponse.getServerTime())));
                    holder.amount.setText("Amount"+" " + PriceFormatter.format(payToMerchantCommitRequestResponse.getTxnAmount(), 3, 3));
                    holder.invoice_image.setVisibility(View.GONE);
                    break;
                case INVOICE_TRAN_RESPONSE:
                    InvoiceTranHistoryResponsePojo tran_invoice = (InvoiceTranHistoryResponsePojo) genericResponse;
                    holder.text.setText(tran_invoice.getRecipientMobileNumber());
                    //holder.textNextLine.setText("" + TimeUtils.getDisplayableDateWithSeconds(tran_invoice.getG_servertime(), new Date(tran_invoice.getServerTime())));

                    //holder.textNextLine.setText(TimeUtils.getDisplayableDateWithSeconds(tran_invoice.getG_servertime(), new Date(tran_invoice.getServerTime())));

                    //holder.icon.setImageBitmap(getYPCCustomerLogsIcon(tran_invoice.getG_status()));
                    switch (tran_invoice.getPaymentStatus()) {
                        case 0:
                            holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.exclaimation_android52));
                            //payment_confirm_status.setText("PENDING");
                            break;
                        case 1:
                            holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.mer_fail));
                            //payment_confirm_status.setText("HOLD");
                            break;
                        case 2:
                            holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.mer_green));
                            //payment_confirm_status.setText("SUCCESS");
                            break;
                        case 3:
                            holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.mer_exc));
                            //payment_confirm_status.setText("REJECTED");
                        case 4:
                            holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.mer_exc));
                            //payment_confirm_status.setText("FAILED");
                            break;
                        default:
                            break;
                    }
                    if (tran_invoice.getServerTime() != 0) {
                        holder.textNextLine.setVisibility(View.VISIBLE);
                        holder.textNextLine.setText("" + TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(tran_invoice.getServerTime())));
                    } else {
                        holder.textNextLine.setVisibility(View.GONE);
                    }
                    holder.amount.setText("Invoice Amount " + PriceFormatter.format(Double.parseDouble(tran_invoice.getRechargeAmt()), 3, 3));
                    holder.invoice_image.setVisibility(View.VISIBLE);
                    // holder.amount.setText("Amount "+PriceFormatter.format(tran_invoice.getTxnAmount(), 3, 3));

                    break;

                default:
                    holder.text.setText("UNKNOWN");
                    GenericResponse genericResponse1 = (GenericResponse) genericResponse;
                    holder.icon.setImageBitmap(getYPCCustomerLogsIcon(genericResponse1.getG_status()));
                    break;
            }

            holder.transTypeInterface = genericResponse;
            return convertView;
        }
    }

    Bitmap[] iconArray = null;

    private void initializeIcons() {
        iconArray = new Bitmap[5];
        ypc_cc_success = iconArray[0] = BitmapFactory.decodeResource(
                getResources(), R.drawable.mer_green);
        ypc_cc_failure = iconArray[1] = BitmapFactory.decodeResource(
                getResources(), R.drawable.mer_fail);
        ypc_cc_rejected = iconArray[2] = BitmapFactory.decodeResource(
                getResources(), R.drawable.mer_fail);
        ypc_cc_duplicate = iconArray[3] = BitmapFactory.decodeResource(
                getResources(), R.drawable.mer_exc);
        ypc_cc_waiting = iconArray[4] = BitmapFactory.decodeResource(
                getResources(), R.drawable.mer_q);
    }

    //@@romeo
    public class ViewHolder {
        TextView text;
        ImageView icon;
        TextView textNextLine;
        TextView amount;
        ImageView invoice_image;
        TransTypeInterface transTypeInterface;


        public TransTypeInterface getTransTypeInterface() {
            return transTypeInterface;
        }
    }

    Bitmap ypc_cc_success, ypc_cc_failure, ypc_cc_rejected, ypc_cc_duplicate, ypc_cc_waiting;

    private Bitmap getYPCCustomerLogsIcon(int value) {
        Bitmap bMap = null;
        switch (value) {
            case SRCODE_0EA_PROBLEM_IN_PROCESSING:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_0_PROCESSED_TIMED_OUT:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_00A_SEND_MONEY_INSTAPAY_AUTHORIZED:
                bMap = ypc_cc_waiting;
                break;
            case SRCODE_0A_RECHARGE_SUCCESS:
                bMap = ypc_cc_success;
                break;
            case 1:
                bMap = ypc_cc_success;
                break;
            case -1:
                bMap = ypc_cc_rejected;
                break;
            case SRCODE_0C_DUPLICATE_COUNTER:
                bMap = ypc_cc_duplicate;
                break;
            case SRCODE_0D_SUCCESS:
                bMap = ypc_cc_success;
                break;
            case SRCODE_0E_REJECTED_BY_MERCHANT:
                bMap = ypc_cc_rejected;
                break;
            case SRCODE_0F_USER_INACTIVE:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_1_SENDER_WALLET_EXPIRED:
            case SRCODE_1ZA_RECHARGE_DECLINED_BY_MERCHANT:
            case SRCODE_1ZB_TRANSACTION_DAILY_LIMIT_REACHED:
            case SRCODE_1ZC_TRANSACTION_MONTHLY_LIMIT_REACHED:
            case SRCODE_1ZD_TRANSACTION_SINGLE_LIMIT_REACHED:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_1A_TRANSACTION_AMOUNT_LESS_THAN_MINIMUM:
            case SRCODE_2_SMM_RECEIVER_BALANCE_BREACHED:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_2A_SMM_RECEIVER_RECHARGE_AMOUNT_LESS_THAN_MIN_BALANCE:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_3_OPERATION_SUCCESS:
                bMap = ypc_cc_success;
                break;
            case SRCODE_4_CONDITION_NOT_SATISFIED:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_5_SMM_WAITING_FOR_APPROVAL:
                bMap = ypc_cc_waiting;
                break;
            case SRCODE_6_USER_INAVTIVE:
            case SRCODE_7_SMM_RECEIVER_NOT_ACIVE_DORMANT:
            case SRCODE_7A_SMM_TIME_ERROR:
            case STATE_BLOCKED:
            case STATE_CHANGE_FORCED:
            case STATE_INITIALIZED:
            case STATE_NOT_INITIALIZED:
            case SRCODE_11A_WRONG_TPIN:
            case SRCODE_12_SMM_RECEIVER_NOT_REGISTERED:
            case SRCODE_13_SMM_SENDER_BALANCE_INSUFFICIENT:
            case SRCODE_14_SMM_SENDER_DAILY_LIMIT_REACHED:
            case SRCODE_15_SMM_SENDER_MONTHLY_LIMIT_REACHED:
            case SRCODE_16_SMM_RECEIVER_DAILY_LIMIT_REACHED:
            case SRCODE_17_SMM_RECEIVER_MONTHLY_LIMIT_REACHED:
            case SRCODE_18_SMM_USER_NOT_ALLOWED_TO_SEND:
            case SRCODE_19_INTERNAL_ERROR:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_20_DUPLICATE_REQUEST:
                bMap = ypc_cc_duplicate;
                break;
            case SRCODE_20A_OLD_AND_NEW_TPIN_ARE_SAME:
                bMap = ypc_cc_duplicate;
                break;
            case SRCODE_21_SMM_SENDER_SINGLE_TRANSACTION_EXCEEDED:
            case SRCODE_22_SMM_TRANSACTION_AMOUNT_LESS_THAN_MINIMUM:
            case SRCODE_23_SMM_SENDER_RECEIVER_SAME:
            case SRCODE_24_RECEIVER_WALLET_EXPIRED:
            case SRCODE_25_SMM_USER_NOT_ALLOWED_TO_RECEIVE:
            case SRCODE_26_SMM_TRANSACTION_DECLINED_BY_SENDER:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_27_SMM_USER_RECHARGE_NOT_SUCCESSFULL:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_28_IP_TRANSACTION_SUCCESSFUL_WALLET_DEBITED:
                bMap = ypc_cc_success;
                break;
            case SRCODE_29_IP_TRANSACTION_UNDER_PROCESS_WALLET_DEBITED:
                bMap = ypc_cc_waiting;
                break;
            case SRCODE_30_IP_INVALID_REFILL_AMOUNT:
            case SRCODE_31_IP_REFILL_BARRED_TEMPORARILY_CONTACT_SERVICE_PROVIDER:
            case SRCODE_32_IP_INVALID_ACCOUNT_NUMBER:
            case SRCODE_33_IP_INSUFFICIENT_AGENT_BALANCE:
            case SRCODE_34_IP_DUPLICATE_TRANSACTION_TRY_AFTER_60_MINUTES:
            case SRCODE_35_IP_SYSTEM_ERROR_TRY_AGAIN_LATER:
            case SRCODE_36_IP_INVALID_ACCESS_TOKEN:
            case SRCODE_37_IP_SERVICE_PROVIDER_DOWNTIME:
            case SRCODE_38_IP_SERVICE_PROVIDER_ERROR_TRY_AGAIN_LATER:
            case SRCODE_39_IP_INVALID_TRANSACTION_ID:
            case SRCODE_40_IP_DENOMINATION_TEMPORARILY_BARRED:
            case SRCODE_41_IP_TRANSACTION_STATUS_UNAVAILABLE_TRY_AGAIN:
            case SRCODE_42_IP_INVALID_SERVICE_PROVIDER:
            case SRCODE_43_IP_REQUEST_PARAMETERS_ARE_INVALID_OR_INCOMPLETE:
            case SRCODE_44_IP_AGENT_ACCOUNT_BLOCKED_CONTACT_HELPDESK:
            case SRCODE_45_IP_UNKNOWN_ERROR_DESCRIPTION_CONTACT_HELPDESK:
            case SRCODE_46_IP_INVALID_ERROR_CODE:
            case SRCODE_47_IP_INVALID_RESPONSE_TYPE:
            case SRCODE_48_IP_INTERNAL_PROCESSING_ERROR:
            case SRCODE_49_IP_INVALID_AGENT_CREDENTIALS:
            case SRCODE_50_IP_USER_ACCESS_DENIED:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_51_IP_TRANSACTION_REFUND_PROCESSED_WALLET_CREDITED:
                bMap = ypc_cc_waiting;
                break;
            case SRCODE_52_P2B_TUP_WALLET_DEBITED_TRANSACTION_IN_PROCESS:
                bMap = ypc_cc_waiting;
                break;
            case SRCODE_53_P2B_TRANSFER_SUCCESS:
                bMap = ypc_cc_success;
                break;
            case SRCODE_54_P2B_TRANSFER_FAILED:
                bMap = ypc_cc_failure;
                break;
            case SRCODE_55_P2E_TXN_AUTHOURIZE_BY_CUST:
                bMap = ypc_cc_success;
                break;
            default:
                break;
        }
        return bMap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }
}

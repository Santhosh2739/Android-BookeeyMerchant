package wallet.ooredo.com.live.barcodepaymentcollection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import coreframework.database.CustomSharedPreferences;
import coreframework.processing.PayToMerchantPhase2Processing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import coreframework.utils.PriceFormatter;
import coreframework.utils.TimeUtils;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import ycash.wallet.json.pojo.generic.CommitType;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantCommitRequest;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantRequestResponse;

public class QRCodePaymentCollectionLeg2RequestScreen extends GenericActivity implements YPCHeadlessCallback, View.OnClickListener {

    private String response = null;
    private PayToMerchantRequestResponse response_obj = null;

    TableRow ypcm2_txn_discount_per_head_tbl_row,
            ypcm2_txn_discount_amt_head_tbl_row,
            ypcm2_txn_deducted_head_tbl_row,
            ypcm2_txn_offer_head_tbl_row;
    TextView ypcm2_discount_per,
            ypcm2_txn_discount_amt,
            ypcm2_txn_deducted,
            ypcm2_txn_offerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_money_leg2_request);
        enableUndoBar();
        response = getIntent().getExtras().getString("response");
        response_obj = new Gson().fromJson(response, PayToMerchantRequestResponse.class);
        Button ypcm_accept = (Button) findViewById(R.id.ypcm_accept);
        ypcm_accept.setOnClickListener(this);


        Button ypcm_decline = (Button) findViewById(R.id.ypcm_decline);
        ypcm_decline.setOnClickListener(this);

        TextView ypcm2_status = (TextView) findViewById(R.id.ypcm2_status);
        TextView ypcm2_amount = (TextView) findViewById(R.id.ypcm2_amount);
        TextView ypcm2_cxid = (TextView) findViewById(R.id.ypcm2_cxid);
        TextView ypcm2_txnid = (TextView) findViewById(R.id.ypcm2_txnid);
        TextView ypcm2_txn_time = (TextView) findViewById(R.id.ypcm2_txn_time);
        TextView ypcm2_bal = (TextView) findViewById(R.id.ypcm2_bal);
        TextView ypcm2_amount_head = (TextView) findViewById(R.id.ypcm2_amount_head);


        //For offers related
        ypcm2_txn_discount_per_head_tbl_row = (TableRow) findViewById(R.id.ypcm2_txn_discount_per_head_tbl_row);
        ypcm2_txn_discount_amt_head_tbl_row = (TableRow) findViewById(R.id.ypcm2_txn_discount_amt_head_tbl_row);
        ypcm2_txn_deducted_head_tbl_row = (TableRow) findViewById(R.id.ypcm2_txn_deducted_head_tbl_row);
        ypcm2_txn_offer_head_tbl_row = (TableRow) findViewById(R.id.ypcm2_txn_offer_head_tbl_row);

        ypcm2_discount_per = (TextView) findViewById(R.id.ypcm2_discount_per);
        ypcm2_txn_discount_amt = (TextView) findViewById(R.id.ypcm2_txn_discount_amt);
        ypcm2_txn_deducted = (TextView) findViewById(R.id.ypcm2_txn_deducted);
        ypcm2_txn_offerID = (TextView) findViewById(R.id.ypcm2_txn_offerID);

        ypcm2_status.setText("" + response_obj.getG_status_description());
        /*if (response_obj.getOfferId() != 0 && response_obj.getDiscount() != 0) {
                ypcm2_amount_head.setText("Amount Due");
                ypcm2_amount.setText("KWD " + PriceFormatter.format(response_obj.getTxnAmount(), 3, 3));
            } else {
                ypcm2_amount.setText("KWD " + PriceFormatter.format(response_obj.getTxnAmount(), 3, 3));
            }*/
        ypcm2_amount.setText("KWD " + PriceFormatter.format(response_obj.getTxnAmount(), 3, 3));
        ypcm2_cxid.setText("" + response_obj.getCustomerMobileNumber());
        ypcm2_txnid.setText("" + response_obj.getTransactionId());


        String send_version_response = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.SEND_VERSION_RESPONSE);
        String time_zone_str = null;

        try {
            JSONObject send_version_response_jo = new JSONObject(send_version_response);
            time_zone_str = send_version_response_jo.getString("g_servertime");


        } catch (Exception e) {

//            Toast.makeText(getApplicationContext(),"TimeZone Ex: "+e.getMessage(),Toast.LENGTH_LONG).show();

        }

        ypcm2_txn_time.setText(TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(response_obj.getServerTime())));
        ypcm2_bal.setText("KWD " + PriceFormatter.format(response_obj.getMer_balance(), 3, 3));
        ypcm2_bal.setVisibility(View.GONE);
        /*if (response_obj.getDiscount() != 0) {
            ypcm2_txn_discount_per_head_tbl_row.setVisibility(View.VISIBLE);
            ypcm2_discount_per.setText("" + response_obj.getDiscount() + "%");
        }*/
        if (response_obj.getDiscountPrice() != 0) {
            ypcm2_txn_discount_amt_head_tbl_row.setVisibility(View.VISIBLE);
            ypcm2_txn_discount_amt.setText("" + PriceFormatter.format(response_obj.getDiscountPrice(), 3, 3) + " KWD");
        }
        if (response_obj.getAfterDiscountAmount() != 0) {
            ypcm2_txn_deducted_head_tbl_row.setVisibility(View.VISIBLE);
            ypcm2_txn_deducted.setText("" + PriceFormatter.format(response_obj.getAfterDiscountAmount(), 3, 3) + " KWD");
        }
        if (response_obj.getOfferDescription() != null) {
            ypcm2_txn_offer_head_tbl_row.setVisibility(View.VISIBLE);
            ypcm2_txn_offerID.setText("" + response_obj.getOfferDescription());
        }


        //For bus
//        ypcm_accept.performClick();

    }

    @Override
    protected void onResume() {
        super.onResume();

        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());
        ;
        /*MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
        new DownloadImageTask(back_logo).execute(merchantLoginRequestResponse.getMerchantLogo());*/


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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("response", response);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if (null != savedInstanceState) {
            response = savedInstanceState.getString("response");
            response_obj = new Gson().fromJson(response, PayToMerchantRequestResponse.class);
        }
    }

    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onProgressComplete() {

    }

    @Override
    public void onClick(View v) {
        PayToMerchantCommitRequest commitRequest = null;
        switch (v.getId()) {
            case R.id.ypcm_accept:
                //Button Clicks without user entry, hence no validations required for any input fields
                commitRequest = new PayToMerchantCommitRequest();
                commitRequest.setAccept_code(CommitType.COMMIT_TYPE_ACCEPT);
                String timezone = ((CoreApplication) getApplication()).getGenericResponse().getG_servertime();
                Calendar cal = null;
                if (timezone != null) {
                    cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
                } else {
                    cal = Calendar.getInstance(TimeZone.getDefault());
                }
                if (response.contains("offerId"))
                    if (response_obj.getOfferId() != 0)
                        commitRequest.setOfferId(response_obj.getOfferId());
                long currentLocalTime = cal.getTimeInMillis();
                commitRequest.setClientDate(currentLocalTime);
                commitRequest.setTransactionId(response_obj.getTransactionId());
                break;
            case R.id.ypcm_decline:
                //Button Clicks without user entry, hence no validations required for any input fields
                commitRequest = new PayToMerchantCommitRequest();
                commitRequest.setAccept_code(CommitType.COMMIT_TYPE_FALSE);
                commitRequest.setTransactionId(response_obj.getTransactionId());
                if (response.contains("offerId"))
                    if (response_obj.getOfferId() != 0)
                        commitRequest.setOfferId(response_obj.getOfferId());
                //XXX
                break;
            default:
                return;
        }
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new PayToMerchantPhase2Processing(commitRequest, application, true));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(true);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }
}

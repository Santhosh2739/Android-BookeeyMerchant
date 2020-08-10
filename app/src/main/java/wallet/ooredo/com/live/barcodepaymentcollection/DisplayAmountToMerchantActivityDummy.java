package wallet.ooredo.com.live.barcodepaymentcollection;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import coreframework.processing.PayToMerchantPhase1Processing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import coreframework.utils.Hex;
import coreframework.utils.PriceFormatter;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import ycash.wallet.json.pojo.decodedbarcode.DecodedQrPojo;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantRequest;
import ycash.wallet.json.pojo.sendmoney.SendMoneyRequest;

/**
 * @author mohit
 */
public class DisplayAmountToMerchantActivityDummy extends GenericActivity implements YPCHeadlessCallback{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_amount_to_merchant_dummy);
        enableUndoBar();
        TextView ypcm2_amount = (TextView)findViewById(R.id.ypcm2_amount);
        TextView ypcm2_customer_number_view = (TextView)findViewById(R.id.ypcm2_cust_no);
        final DecodedQrPojo decodedQrPojo = getIntent().getParcelableExtra("data");
        ypcm2_amount.setText("KWD "+ PriceFormatter.format(decodedQrPojo.getBcodeHeaderEncoder().amount,3,3));
        //ypcm2_customer_number_view.setText(""+ Hex.byteArrayToLong(decodedQrPojo.getBcodeHeaderEncoder().staticId));
        ypcm2_customer_number_view.setText(Hex.toHex(decodedQrPojo.getBcodeHeaderEncoder().staticId));



        //Direct L2

        PayToMerchantRequest payToMerchantRequest = new PayToMerchantRequest();
        payToMerchantRequest.setAmount(decodedQrPojo.getBcodeHeaderEncoder().amount);
        payToMerchantRequest.setCustomerId(Hex.toHex(decodedQrPojo.getBcodeHeaderEncoder().staticId));
        payToMerchantRequest.setBarcodeData(Hex.toHex(decodedQrPojo.getCompleteCode()));
        String timezone= ((CoreApplication)getApplication()).getGenericResponse().getG_servertime();
        Calendar cal= null;
        if(timezone!=null) {
            cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        }else{
            cal= Calendar.getInstance(TimeZone.getDefault());
        }
        long currentLocalTime = cal.getTimeInMillis();
        payToMerchantRequest.setClientDate(currentLocalTime);

        //Code to start server thread and display the progress fragment dialogue (retained)
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new PayToMerchantPhase1Processing(payToMerchantRequest, application, true));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(true);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");











        findViewById(R.id.pay_to_merchat_accept).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                PayToMerchantRequest payToMerchantRequest = new PayToMerchantRequest();
                payToMerchantRequest.setAmount(decodedQrPojo.getBcodeHeaderEncoder().amount);
                payToMerchantRequest.setCustomerId(Hex.toHex(decodedQrPojo.getBcodeHeaderEncoder().staticId));
                payToMerchantRequest.setBarcodeData(Hex.toHex(decodedQrPojo.getCompleteCode()));
                String timezone= ((CoreApplication)getApplication()).getGenericResponse().getG_servertime();
                Calendar cal= null;
                if(timezone!=null) {
                    cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
                }else{
                    cal= Calendar.getInstance(TimeZone.getDefault());
                }
                long currentLocalTime = cal.getTimeInMillis();
                payToMerchantRequest.setClientDate(currentLocalTime);

                //Code to start server thread and display the progress fragment dialogue (retained)
                CoreApplication application = (CoreApplication) getApplication();
                String uiProcessorReference = application.addUserInterfaceProcessor(new PayToMerchantPhase1Processing(payToMerchantRequest, application, true));
                ProgressDialogFrag progress = new ProgressDialogFrag();
                Bundle bundle = new Bundle();
                bundle.putString("uuid", uiProcessorReference);
                progress.setCancelable(true);
                progress.setArguments(bundle);
                progress.show(getFragmentManager(), "progress_dialog");
            }
        });
        findViewById(R.id.pay_to_merchat_decline).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ImageView  back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo()); ;
//        MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
//        new DownloadImageTask(back_logo).execute(merchantLoginRequestResponse.getMerchantLogo());


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
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onProgressComplete() {

    }
}
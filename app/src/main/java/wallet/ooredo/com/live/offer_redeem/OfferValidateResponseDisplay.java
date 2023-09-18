package wallet.ooredo.com.live.offer_redeem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import coreframework.processing.QRCodeOfferRedeemCommitProcessing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;

public class OfferValidateResponseDisplay extends GenericActivity implements YPCHeadlessCallback, View.OnClickListener {

  private String offer_redeem_qrdata = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_validate_response_display);
        enableUndoBar();

      TextView ypcm2_status_head = (TextView)findViewById(R.id.ypcm2_status_head);

        offer_redeem_qrdata =  getIntent().getStringExtra("offer_redeem_qrdata");
//        try {
//            JSONObject offer_redeem_qrdata_jo = new JSONObject(offer_redeem_qrdata);
//        }catch(Exception e){
//
//        }

        Button proceed_to_offer_redeem = (Button)findViewById(R.id.proceed_to_offer_redeem);
        proceed_to_offer_redeem.setOnClickListener(this);
        Button proceed_to_offer_cancel = (Button)findViewById(R.id.proceed_to_offer_cancel);
        proceed_to_offer_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.proceed_to_offer_redeem:

                CoreApplication application = (CoreApplication) getApplication();
                String uiProcessorReference = application.addUserInterfaceProcessor(new QRCodeOfferRedeemCommitProcessing(offer_redeem_qrdata,application));
                ProgressDialogFrag progress = new ProgressDialogFrag();
                Bundle bundle = new Bundle();
                bundle.putString("uuid", uiProcessorReference);
                progress.setCancelable(true);
                progress.setArguments(bundle);
                progress.show(getFragmentManager(), "progress_dialog");
                break;
            case R.id.proceed_to_offer_cancel:
                finish();
                break;
        }

    }
    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
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
    public void onProgressComplete() {

    }
}

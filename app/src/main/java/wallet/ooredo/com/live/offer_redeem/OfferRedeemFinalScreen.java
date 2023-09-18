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

import coreframework.taskframework.GenericActivity;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;

public class OfferRedeemFinalScreen extends GenericActivity implements View.OnClickListener{

    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";
    public static final String KEY_HEADER = "KEY_HEADER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_redeem_final);
//        enableUndoBar();

        ImageView payment_success_img_id = (ImageView)findViewById(R.id.payment_success_img_id);
        payment_success_img_id.setOnClickListener(this);

       TextView payment_confirm_total_payment_id = (TextView)findViewById(R.id.payment_confirm_total_payment_id);
        payment_confirm_total_payment_id.setText(getIntent().getStringExtra(KEY_DESCRIPTION));

        Button close_btn = (Button)findViewById(R.id.close_btn);
        close_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.payment_success_img_id:

                finish();
                break;
            case R.id.close_btn:

                finish();
                break;
        }

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


}

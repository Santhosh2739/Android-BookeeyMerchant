package wallet.ooredo.com.live.barcodepaymentcollection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import coreframework.database.CustomSharedPreferences;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.YPCHeadlessCallback;
import coreframework.utils.PriceFormatter;
import coreframework.utils.TimeUtils;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wangpos.sdk4.libbasebinder.Printer;
import ycash.wallet.json.pojo.paytomerchant.PayToMerchantCommitRequestResponse;
/**
 * Created by munireddy on 08-06-2015.
 */
public class QRCodePaymentCollectionFinalScreen extends GenericActivity implements YPCHeadlessCallback, View.OnClickListener {
    private static final int MESSAGE_PRINT = 0x00;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_PRINT) {
                String message = (String) msg.obj;
                showMessage(message);
            }
        }

        ;
    };
    int status = 0;
    TableRow payment_confirm_total_amt_row, payment_confirm_discount_amt_row;
    TextView payment_confirm_total_amt_value, payment_confirm_discount_amt_value;
    Button payment_confirm_print_btn;
    private String response = null;
    private PayToMerchantCommitRequestResponse response_obj = null;
    private Message message = null;
    private Printer mPrinter;
    private boolean bloop = false;
    private boolean bthreadrunning = false;
    //For Bus

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_confirmation);
        new Thread() {
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }.start();
        ImageView ypcm_close = (ImageView) findViewById(R.id.payment_success_img_id);
        ypcm_close.setOnClickListener(this);
        TextView payment_confirm_status = (TextView) findViewById(R.id.payment_confirm_status);
        TextView payment_confirm_txn_id = (TextView) findViewById(R.id.payment_confirm_txn_id);
        TextView payment_confirm_date_id = (TextView) findViewById(R.id.payment_confirm_date_id);
        TextView payment_confirm_total_payment_id = (TextView) findViewById(R.id.payment_confirm_total_payment_id);
        TextView payment_confirm_wallet_number_id = (TextView) findViewById(R.id.payment_confirm_wallet_number_id);
        //offres
        payment_confirm_total_amt_row = (TableRow) findViewById(R.id.payment_confirm_total_amt_row);
        payment_confirm_discount_amt_row = (TableRow) findViewById(R.id.payment_confirm_discount_amt_row);
        payment_confirm_total_amt_value = (TextView) findViewById(R.id.payment_confirm_total_amt_value);
        payment_confirm_discount_amt_value = (TextView) findViewById(R.id.payment_confirm_discount_amt_value);
        //print
        payment_confirm_print_btn = (Button) findViewById(R.id.payment_confirm_print_btn);
        if (((CoreApplication) getApplication()).isPOS() || ((CoreApplication) getApplication()).isNewPOS()) {
            payment_confirm_print_btn.setVisibility(View.VISIBLE);
        } else {
            payment_confirm_print_btn.setVisibility(View.GONE);
        }
        //This is the place where the response from leg1 is received as intent data
        //start
        response = getIntent().getExtras().getString("response");
        response_obj = new Gson().fromJson(response, PayToMerchantCommitRequestResponse.class);
        //end
        if (response_obj.getG_status_description().contains("success")) {
            status = 0;
            payment_confirm_status.setText("" + getString(R.string.transaction_success));
        } else {
            status = 3;
            payment_confirm_status.setText("" + response_obj.getG_status_description());
        }
        TextView payment_confirm_wallet_number_header_id = (TextView) findViewById(R.id.payment_confirm_wallet_number_header_id);
        TextView payment_confirm_txn_header_id = (TextView) findViewById(R.id.payment_confirm_txn_header_id);
        if (response_obj.getTransactionId() == null || response_obj.getTransactionId().length() == 0) {
            payment_confirm_txn_header_id.setVisibility(View.GONE);
        }
        if (response_obj.getCustomerMobileNumber() == null || response_obj.getCustomerMobileNumber().length() == 0) {
            payment_confirm_wallet_number_header_id.setVisibility(View.GONE);
        }
        if (response_obj.getTotalPrice() != 0) {
            payment_confirm_total_amt_row.setVisibility(View.VISIBLE);
            payment_confirm_total_amt_value.setText("" + PriceFormatter.format(response_obj.getTotalPrice(), 3, 3) + " KWD");
        }
        if (response_obj.getDiscountPrice() != 0) {
            payment_confirm_discount_amt_row.setVisibility(View.VISIBLE);
            payment_confirm_discount_amt_value.setText("" + PriceFormatter.format(response_obj.getDiscountPrice(), 3, 3) + " KWD");
        }
        payment_confirm_txn_id.setText("" + response_obj.getTransactionId());
        payment_confirm_wallet_number_id.setText("" + response_obj.getCustomerMobileNumber());
        String send_version_response = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.SEND_VERSION_RESPONSE);
        String time_zone_str = null;
        try {
            JSONObject send_version_response_jo = new JSONObject(send_version_response);
            time_zone_str = send_version_response_jo.getString("g_servertime");
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(),"TimeZone Ex: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        payment_confirm_date_id.setText("" + TimeUtils.getDisplayableDateWithSeconds(time_zone_str, new Date(response_obj.getServerTime())));
        switch (response_obj.getG_status()) {
            case 1:
                ypcm_close.setBackgroundResource(R.drawable.success);
                break;
            case -1:
                ypcm_close.setBackgroundResource(R.drawable.red_cross);
                break;
        }
        payment_confirm_total_payment_id.setText(getString(R.string.total_paymnet) + "   KWD " + PriceFormatter.format(response_obj.getTxnAmount(), 3, 3));
        payment_confirm_print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CoreApplication) getApplication()).isPOS()) {
                    int[] status = new int[1];
                    int ret = -1;
                    try {
                        ret = mPrinter.getPrinterStatus(status);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (ret == 0) {
                        startPrint();
                    } else {
                        Toast.makeText(getApplicationContext(), "Printer not available on device..!" + ret, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //startSDKPrint();
                }
            }
        });
    }

    public void showMessage(String str) {
        Toast.makeText(QRCodePaymentCollectionFinalScreen.this, str + "", Toast.LENGTH_SHORT).show();
    }

    private void startPrint() {
        bloop = false;
        if (bthreadrunning == false)
            new PrintThread().start();
    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//        ImageView back_logo=null;
//        try {
//            back_logo = (ImageView) findViewById(R.id.back_logo);
//            MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
//
//            Bitmap decodedByte = BitmapFactory.decodeByteArray(merchantLoginRequestResponse.getMerchantLogo(), 0, merchantLoginRequestResponse.getMerchantLogo().length);
//            if(decodedByte!=null)
//                back_logo.setImageBitmap(decodedByte);
//        }catch (Exception e){
//            back_logo.setVisibility(View.INVISIBLE);
//        }
//    }
    @Override
    protected void onResume() {
        super.onResume();
        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        //Facebook
//        AppEventsLogger logger = AppEventsLogger.newLogger(this);
//        logger.logEvent("MX_P2M_Final");
        final ImageView back_logo = (ImageView) findViewById(R.id.back_logo);
        back_logo.setImageBitmap(((CoreApplication) getApplication()).getMerchnat_logo());
//    MerchantLoginRequestResponse merchantLoginRequestResponse = ((CoreApplication) getApplication()).getMerchantLoginRequestResponse();
//    new DownloadImageTask(back_logo).execute(merchantLoginRequestResponse.getMerchantLogo());
        //For Bus
//        Runnable mMyRunnable = new Runnable()
//        {
//            @Override
//            public void run()
//            {
//
//
//                payment_confirm_print_btn.performClick();
//
//
//            }
//        };
//        Handler myHandler = new Handler();
//        myHandler.postDelayed(mMyRunnable, 1000);
    }

    @Override
    public void onProgressUpdate(int progress) {
    }

    @Override
    public void onProgressComplete() {
    }

    @Override
    public void onClick(View v) {
        finish();
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
            response_obj = new Gson().fromJson(response, PayToMerchantCommitRequestResponse.class);
        }
    }

    private void merchantLogo(String pic) {
        try {
            InputStream inputStream = null;
            Bitmap bitmap = null;
            //inputStream = getAssets().open("icon2.png");
            //bitmap = BitmapFactory.decodeStream(inputStream);
            String image = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.MERCHANT_LOGO);
            if (image.length() != 0) {
                bitmap = stringToBitmap(image);
                mPrinter.printImageBase(bitmap, 100, 100, Printer.Align.CENTER, 0);
            }
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Print

    private Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void transactionDetails(int result) {
        try {
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Status : " + response_obj.getG_status_description(), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Date : " + TimeUtils.getDisplayableDateWithSeconds(response_obj.getG_servertime(), new Date(response_obj.getServerTime())), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Transaction ID : " + response_obj.getTransactionId(), 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Cust Mobile No : " + maskString(response_obj.getCustomerMobileNumber(), 0, 4, 'X'), 25, Printer.Align.LEFT, false, false);
            if (response_obj.getTotalPrice() != 0) {
                result = mPrinter.printString("Txn Amount : " + PriceFormatter.format(response_obj.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }
            /*if (response_obj.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt : " + PriceFormatter.format(response_obj.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }*/
            result = mPrinter.printString("Total Payment : " + PriceFormatter.format(response_obj.getTxnAmount(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Thank you for using", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("Bookeey Wallet", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("www.bookeey.com", 25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Smart Way2Pay", 25, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void statusIcon() {
        try {
            InputStream inputStream = null;
            Bitmap icon;
            if (status == 0)
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.success);
            else if (status == 1)
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.txn_hold);
            else if (status == 2)
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.expired);
            else
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.red_cross);
            mPrinter.printImageBase(icon, 50, 50, Printer.Align.CENTER, 0);
            icon.recycle();
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void printHeaderDetails(int result) {
        String name = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.NAME);
        String branch = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.LOCATION);
        String merchantId = CustomSharedPreferences.getStringData(getBaseContext(), CustomSharedPreferences.SP_KEY.USERNAME);
        try {
            //default content print
            //result = mPrinter.printString("Bookeey Wallet", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString(name, 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString(branch, 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString(merchantId, 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void transactionDetailsArabic(int result) {
        try {
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            if (response_obj.getG_status_description().contains("success")) {
                result = mPrinter.printString(getString(R.string.status) + " : " + getString(R.string.transaction_success), 25, Printer.Align.RIGHT, false, false);
            } else {
                result = mPrinter.printString(getString(R.string.status) + " : " + response_obj.getG_status_description(), 25, Printer.Align.RIGHT, false, false);
            }
            result = mPrinter.printString(getString(R.string.date) + " : " + TimeUtils.getDisplayableDateWithSeconds(response_obj.getG_servertime(), new Date(response_obj.getServerTime())), 25, Printer.Align.RIGHT, false, false);
            result = mPrinter.printString(getString(R.string.transaction_id) + " : " + response_obj.getTransactionId(), 25, Printer.Align.RIGHT, false, false);
            result = mPrinter.printString(getString(R.string.cust_mobile_no) + " : " + maskString(response_obj.getCustomerMobileNumber(), 0, 4, 'X'), 25, Printer.Align.RIGHT, false, false);
            if (response_obj.getTotalPrice() != 0) {
                result = mPrinter.printString(getString(R.string.transaction_amount) + " : " + PriceFormatter.format(response_obj.getTotalPrice(), 3, 3) + " KWD", 25, Printer.Align.RIGHT, false, false);
            }
            /*if (response_obj.getDiscountPrice() != 0) {
                result = mPrinter.printString("Discount Amt : " + PriceFormatter.format(response_obj.getDiscountPrice(), 3, 3) + " KWD", 25, Printer.Align.LEFT, false, false);
            }*/
            result = mPrinter.printString(getString(R.string.total_paymnet) + " : " + PriceFormatter.format(response_obj.getTxnAmount(), 3, 3) + " KWD", 25, Printer.Align.RIGHT, false, false);
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
//            result = mPrinter.printString("Thank you for using", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString(getString(R.string.thank_you_for_using_bookeey_wallet), 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("www.bookeey.com", 25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Smart Way2Pay", 25, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bookeeyLogo(String logo) {
        try {
            InputStream inputStream = null;
            Bitmap bitmap = null;
            inputStream = getAssets().open("icon2.png");
            bitmap = BitmapFactory.decodeStream(inputStream);
            mPrinter.printImageBase(bitmap, 100, 100, Printer.Align.CENTER, 0);
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private String maskString(String strText, int start, int end, char maskChar) {
        if (strText == null || strText.equals(""))
            return "";
        if (start < 0)
            start = 0;
        if (end > strText.length())
            end = strText.length();
        int maskLength = end - start;
        if (maskLength == 0)
            return strText;
        StringBuilder sbMaskString = new StringBuilder(maskLength);
        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }
        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
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

    private class PrintThread extends Thread {
        @Override
        public void run() {
            bthreadrunning = true;
            int datalen = 0;
            int result = 0;
            byte[] senddata = null;
            do {
                try {
                    result = mPrinter.printInit();
                    //clear print cache
                    mPrinter.clearPrintDataCache();
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                try {
                    //To print the logo
                    merchantLogo("logo");
                    printHeaderDetails(result);
                    statusIcon();
                    String selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);
                    if (selectedLanguage.equalsIgnoreCase("ar")) {
                        transactionDetailsArabic(result);
                    } else {
                        transactionDetails(result);
                    }
                    //To print the logo
                    bookeeyLogo("logo");
                    //print end reserve height
                    result = mPrinter.printPaper(100);
                    //Detecting the in-place status of the card during printing
                    //mPrinter.printPaper_trade(5,100);
                    result = mPrinter.printFinish();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } while (bloop);
            bthreadrunning = false;
        }
    }
}

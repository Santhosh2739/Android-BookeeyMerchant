package wallet.ooredo.com.live.consolidatedreports;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import coreframework.database.CustomSharedPreferences;
import coreframework.processing.ReportmailProcessing;
import coreframework.processing.ReportsProcessing;
import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.ProgressDialogFrag;
import coreframework.taskframework.YPCHeadlessCallback;
import coreframework.utils.PriceFormatter;
import coreframework.utils.TimeUtils;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.transactionhistory.TransactionHistoryDisplayActivity;
import wangpos.sdk4.libbasebinder.Printer;
import ycash.wallet.json.pojo.consolidatedreports.MerchantReportRequest;
import ycash.wallet.json.pojo.consolidatedreports.MerchantReportResponse;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;

/**
 * Created by 30099 on 1/18/2016.
 */
public class Consolidated_Reports_Selection_Display extends GenericActivity implements YPCHeadlessCallback {

    private Printer mPrinter;
    private boolean bloop = false;
    private boolean bthreadrunning = false;

    private String fromDate,fromTime,toDate,toTime;
    MerchantReportResponse merchantReportResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consolidated_report_display);



        new Thread() {
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }.start();


        TextView reports_date_Tv = (TextView)findViewById(R.id.reports_date_Tv);
        TextView reports_from_time_and_to_time_Tv = (TextView)findViewById(R.id.reports_from_time_and_to_time_Tv);
        TextView report_display_count_of_txn_tv = (TextView)findViewById(R.id.report_display_count_of_txn_tv);
        TextView report_display_sum_of_txn_tv = (TextView)findViewById(R.id.report_display_sum_of_txn_tv);
        TextView report_display_amnt_credited_tv = (TextView)findViewById(R.id.report_display_amnt_credited_tv);
        Button consolidated_report_display_send_mail_btn= (Button) findViewById(R.id.consolidated_report_display_send_mail_btn);

        String response = getIntent().getStringExtra("response");
        final String date = getIntent().getStringExtra("date");
        final String todate= getIntent().getStringExtra("todate");
        final String fromtime = getIntent().getStringExtra("fromtime");
        final String totime = getIntent().getStringExtra("totime");

         fromDate = getIntent().getStringExtra("date");
        toDate= getIntent().getStringExtra("todate");
        fromTime = getIntent().getStringExtra("fromtime");
         toTime = getIntent().getStringExtra("totime");

        reports_date_Tv.setText(date+ " : "+fromtime);
        reports_from_time_and_to_time_Tv.setText(todate+" : "+totime);

        merchantReportResponse = new Gson().fromJson(response, MerchantReportResponse.class);

        report_display_count_of_txn_tv.setText(""+merchantReportResponse.getTxnCount());
        report_display_sum_of_txn_tv.setText(""+ PriceFormatter.format(merchantReportResponse.getSumOfTxnAmount(), 3, 3));
        report_display_amnt_credited_tv.setText(""+PriceFormatter.format(merchantReportResponse.getSumOfAmtToBeCredited(),3,3));


        consolidated_report_display_send_mail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MerchantReportRequest merchantReportRequest = new MerchantReportRequest();
                merchantReportRequest.setProcessDateFrom(date);
                merchantReportRequest.setProcessDateTo(todate);
                merchantReportRequest.setFromTime(fromtime);
                merchantReportRequest.setToTime(totime);
                CoreApplication application = (CoreApplication)getApplication();
                String uiProcessorReference = application.addUserInterfaceProcessor(new ReportmailProcessing(merchantReportRequest,application,true));
                ProgressDialogFrag progress = new ProgressDialogFrag();
                Bundle bundle = new Bundle();
                bundle.putString("uuid", uiProcessorReference);
                progress.setCancelable(true);
                progress.setArguments(bundle);
                progress.show(getFragmentManager(), "progress_dialog");
            }
        });

        Button consolidated_report_display_print_btn = (Button)findViewById(R.id.consolidated_report_display_print_btn);


        if(((CoreApplication) getApplication()).isPOS()) {

            consolidated_report_display_print_btn.setVisibility(View.VISIBLE);

        }else{
            consolidated_report_display_print_btn.setVisibility(View.GONE);
        }


        consolidated_report_display_print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int[] status = new int[1];
                int ret = -1;
                try {
                    ret = mPrinter.getPrinterStatus(status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //GETPrinterstatus(int[] status) 状态值	N	HEX1
                // 00 打印机正常
                // 0x01：参数错误
                // 0x06：不可执行
                // 0x8A：缺纸,
                // 0x8B：过热
                if (ret == 0) {
                    //tvshow.setText("Printer status: " + status[0]);
                    startPrint();
                } else {
                    //tvshow.setText("Fail");
                    Toast.makeText(getApplicationContext(), "Printer not available on device..!"+ret, Toast.LENGTH_SHORT).show();

//                    startPrint();

                    // Get a PrintManager instance
//                    PrintManager printManager = (PrintManager) TransactionHistoryDisplayActivity.this
//                            .getSystemService(Context.PRINT_SERVICE);
//
//                    // Set job name, which will be displayed in the print queue
//                    String jobName = TransactionHistoryDisplayActivity.this.getString(R.string.app_name) + " Document";
//
//                    // Start a print job, passing in a PrintDocumentAdapter implementation
//                    // to handle the generation of a print document
//                    printManager.print(jobName, new MyPrintDocumentAdapter(getActivity()),
//                            null);

//                    startPrintPax();
                }

            }
        });

    }

    private void startPrint() {
        bloop = false;
        if (bthreadrunning == false)
            new PrintThread().start();
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
                    //To print thr text


                    String selectedLanguage = CustomSharedPreferences.getStringData(getApplicationContext(), CustomSharedPreferences.SP_KEY.LANGUAGE);


                        if (selectedLanguage.equalsIgnoreCase("en")) {

                            printConsolidatedReport(result);

                        } else if(selectedLanguage.equalsIgnoreCase("ar")){

                            printConsolidatedReportArabic(result);
                        }else{
                            printConsolidatedReport(result);
                        }




                    //To print the logo
                    bookeeyLogo("logo");
                    //print end reserve height
                    result = mPrinter.printPaper(70);
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



    private void merchantLogo(String logo) {
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


    private void printConsolidatedReport(int result) {
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

            result = mPrinter.printString("From",25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString(fromDate+": " + fromTime, 25, Printer.Align.CENTER, false, false);

            result = mPrinter.printString("To",25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString(toDate+": " + toTime, 25, Printer.Align.CENTER, false, false);

            result = mPrinter.printString("Count of Transaction  " + merchantReportResponse.getTxnCount(), 25, Printer.Align.CENTER, false, false);

            result = mPrinter.printString("Sum of Transaction  " + PriceFormatter.format(merchantReportResponse.getSumOfTxnAmount(), 3, 3),25, Printer.Align.CENTER, false, false);



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


    private void printConsolidatedReportArabic(int result) {
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





            result = mPrinter.printString(getString(R.string.from),25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString(fromDate+": " + fromTime, 25, Printer.Align.CENTER, false, false);

            result = mPrinter.printString(getString(R.string.to),25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString(toDate+": " + toTime, 25, Printer.Align.CENTER, false, false);


            result = mPrinter.printString(getString(R.string.count_of_transaction) + "   "+merchantReportResponse.getTxnCount(), 25, Printer.Align.CENTER, false, false);

            result = mPrinter.printString(getString(R.string.sum_of_transaction) + "   "+PriceFormatter.format(merchantReportResponse.getSumOfTxnAmount(), 3, 3),25, Printer.Align.CENTER, false, false);





            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, false, false);
            result = mPrinter.printString(getString(R.string.thank_you_for_using_bookeey_wallet), 25, Printer.Align.CENTER, true, false);
//            result = mPrinter.printString("Bookeey Wallet", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("  ", 15, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("www.bookeey.com", 25, Printer.Align.CENTER, false, false);
            result = mPrinter.printString("Smart Way2Pay", 25, Printer.Align.CENTER, false, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


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

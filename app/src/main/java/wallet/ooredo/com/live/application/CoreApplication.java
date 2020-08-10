package wallet.ooredo.com.live.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.util.HashMap;
import java.util.UUID;
import coreframework.database.CustomSharedPreferences;
import coreframework.taskframework.UserInterfaceBackgroundProcessing;
import ycash.wallet.json.pojo.TelephonyInfo;
import ycash.wallet.json.pojo.consolidatedreports.MerchantReportResponse;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.transactionhistory.TransactionHistoryResponse;

/**
 * Created by mohit on 01-06-2015.
 */
public class CoreApplication extends Application{

    private HashMap<String, UserInterfaceBackgroundProcessing> processingHolder = new HashMap<String, UserInterfaceBackgroundProcessing>();
    private TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
    private MerchantLoginRequestResponse merchantLoginRequestResponse = new MerchantLoginRequestResponse();
    private boolean isUserLoggedIn = false;
    private GenericResponse genericResponse= new GenericResponse();
    private String typeOfMerchant;

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

//        Bugfender.init(this, "4I3MMbodalXiab7B4lrVHXYm7SCdg5m0", BuildConfig.DEBUG);
//        Bugfender.enableCrashReporting();
//        Bugfender.enableUIEventLogging(this);
//        Bugfender.enableLogcatLogging();



        //For Remote logs
//        HyperLog.initialize(this);
//        HyperLog.setLogLevel(Log.VERBOSE);


    }


    public boolean isPOS() {
        return isPOS;
    }

    public void setPOS(boolean POS) {
        isPOS = POS;
    }

    //isPOS
    public boolean isPOS = false;

    public String getTypeOfMerchant() {
        return typeOfMerchant;
    }

    public void setTypeOfMerchant(String typeOfMerchant) {
        this.typeOfMerchant = typeOfMerchant;
    }

    public GenericResponse getGenericResponse() {
        return genericResponse;
    }

    public void setGenericResponse(GenericResponse genericResponse) {
        this.genericResponse = genericResponse;
    }

    public Bitmap getMerchnat_logo() {


        return merchnat_logo;
    }





    public void setMerchnat_logo(Bitmap merchnat_logo) {
        this.merchnat_logo = merchnat_logo;
    }

    private Bitmap merchnat_logo;

    public MerchantReportResponse getMerchantReportResponse() {
        return merchantReportResponse;
    }

    public void setMerchantReportResponse(MerchantReportResponse merchantReportResponse) {
        this.merchantReportResponse = merchantReportResponse;
    }

    private MerchantReportResponse merchantReportResponse  = new MerchantReportResponse();


    public String addUserInterfaceProcessor(UserInterfaceBackgroundProcessing uiProcessor){

        String random = UUID.randomUUID().toString();
        processingHolder.put(random, uiProcessor);
        return random;
    }
    public UserInterfaceBackgroundProcessing getUserInterfaceProcessor(String processid){
        return processingHolder.get(processid);
    }
    public UserInterfaceBackgroundProcessing removeUserInterfaceProcessor(String processid){
        return processingHolder.remove(processid);
    }
    public int getSizeOfUserInterfaceProcessors(){
        return processingHolder.size();
    }
    public TransactionHistoryResponse getTransactionHistoryResponse() {
        return transactionHistoryResponse;
    }
    public void setTransactionHistoryResponse(TransactionHistoryResponse transactionHistoryResponse) {
        this.transactionHistoryResponse = transactionHistoryResponse;
    }
    public MerchantLoginRequestResponse getMerchantLoginRequestResponse() {
        return merchantLoginRequestResponse;
    }
    public void setMerchantLoginRequestResponse(MerchantLoginRequestResponse merchantLoginRequestResponse) {
        this.merchantLoginRequestResponse = merchantLoginRequestResponse;
    }



    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }
    public void setIsUserLoggedIn(boolean isUserLoggedIn) {
        this.isUserLoggedIn = isUserLoggedIn;
        if(isUserLoggedIn){
            //TODO: Initialize Alaram to check if still logged in
        }else{
            CustomSharedPreferences.saveStringData(this,CustomSharedPreferences.SIMPLE_NULL, CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
        }
    }
    public String getThisDeviceUniqueAndroidId(){
        String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        android_id=android_id.length()%2==1?android_id+"FA0A1":android_id;
       /* TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
       String android_id=  mngr.getDeviceId();*/

        if(checkPermission()) {
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
            String imsiSIM1 = telephonyInfo.getImsiSIM1();
            String imsiSIM2 = telephonyInfo.getImsiSIM2();
        }
        return android_id;

    }
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

}

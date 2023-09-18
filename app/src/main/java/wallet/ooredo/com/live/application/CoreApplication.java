package wallet.ooredo.com.live.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.UUID;

import coreframework.database.CustomSharedPreferences;
import coreframework.taskframework.UserInterfaceBackgroundProcessing;
import ycash.wallet.json.pojo.consolidatedreports.MerchantReportResponse;
import ycash.wallet.json.pojo.generic.GenericResponse;
import ycash.wallet.json.pojo.merchantlogin.MerchantLoginRequestResponse;
import ycash.wallet.json.pojo.transactionhistory.TransactionHistoryResponse;

public class CoreApplication extends Application {
    private static Context appContext;
    //isPOS
    public boolean isPOS = false;
    public boolean isNewPOS = false;
    private HashMap<String, UserInterfaceBackgroundProcessing> processingHolder = new HashMap<String, UserInterfaceBackgroundProcessing>();
    private TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse();
    private MerchantLoginRequestResponse merchantLoginRequestResponse = new MerchantLoginRequestResponse();
    private boolean isUserLoggedIn = false;
    private GenericResponse genericResponse = new GenericResponse();
    private String typeOfMerchant;
    private Bitmap merchnat_logo;
    private MerchantReportResponse merchantReportResponse = new MerchantReportResponse();

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public boolean isPOS() {
        return isPOS;
    }

    public void setPOS(boolean POS) {
        isPOS = POS;
    }

    public boolean isNewPOS() {
        return isNewPOS;
    }

    public void setNewPOS(boolean POS) {
        isNewPOS = POS;
    }

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

    public MerchantReportResponse getMerchantReportResponse() {
        return merchantReportResponse;
    }

    public void setMerchantReportResponse(MerchantReportResponse merchantReportResponse) {
        this.merchantReportResponse = merchantReportResponse;
    }

    public String addUserInterfaceProcessor(UserInterfaceBackgroundProcessing uiProcessor) {
        String random = UUID.randomUUID().toString();
        processingHolder.put(random, uiProcessor);
        return random;
    }

    public UserInterfaceBackgroundProcessing getUserInterfaceProcessor(String processid) {
        return processingHolder.get(processid);
    }

    public UserInterfaceBackgroundProcessing removeUserInterfaceProcessor(String processid) {
        return processingHolder.remove(processid);
    }

    public int getSizeOfUserInterfaceProcessors() {
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
        if (isUserLoggedIn) {
            //TODO: Initialize Alaram to check if still logged in
        } else {
            CustomSharedPreferences.saveStringData(this, CustomSharedPreferences.SIMPLE_NULL, CustomSharedPreferences.SP_KEY.LOGIN_RESPONSE);
        }
    }

    public String getThisDeviceUniqueAndroidId() {
        String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        android_id = android_id.length() % 2 == 1 ? android_id + "FA0A1" : android_id;
       /* TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
       String android_id=  mngr.getDeviceId();*/

       /* if(checkPermission()) {
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
            String imsiSIM1 = telephonyInfo.getImsiSIM1();
            String imsiSIM2 = telephonyInfo.getImsiSIM2();
        }*/
        return android_id;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}

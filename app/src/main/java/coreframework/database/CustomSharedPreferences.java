package coreframework.database;

import android.content.Context;
import android.content.SharedPreferences;

public class CustomSharedPreferences {

    public final static String SIMPLE_NULL = "";
    public final static boolean SIMPLE_FALSE = false;

    public enum SP_KEY{
        //LOGIN_REQUEST,
        LOGIN_RESPONSE,
        USERNAME,
        PASSWORD,
        GCM_REG_ID,
        VERSION_N0,
        NAME,
        LOCATION,
        AUTH_TOKEN,
        CURRENT_LOCATION,
        EXTRA_FIELDS,
        LANGUAGE,
        SEND_VERSION_RESPONSE,
        MERCHANT_LOGO,
        KEY_CAMERA_FACING,
        IMEI_NO,

        RECENT_INVOICE_NO,
        RECENT_INVOICE_AMOUNT,
        RECENT_INVOICE_MOBILE_NO,
        KEY_TRANTYPE_SELECTED_INDEX,
        KEY_SEARCH_MOBILE_NO;
    }

    public static final String share_db_preference_db = "share_db_preference_db";

    static public void saveGCMRegId(Context context,String data, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(spKey.name(), data);
        editor.commit();
    }
    static public String getGCMRegId(Context context, SP_KEY spKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        String sourceId = sharedPreferences.getString(spKey.name(), SIMPLE_NULL);
        return sourceId;
    }

    static public void saveStringData(Context context,String data, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(spKey.name(), data);
        editor.commit();
    }
    static public void saveIntData(Context context,int data, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(spKey.name(), data);
        editor.commit();
    }
    static public void saveLongData(Context context,long data, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(spKey.name(), data);
        editor.commit();
    }
    static public String getStringData(Context context, SP_KEY spKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        String sourceId = sharedPreferences.getString(spKey.name(), SIMPLE_NULL);
        return sourceId;
    }
    static public int getIntData(Context context,SP_KEY spKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        int value = sharedPreferences.getInt(spKey.name(), -1);
        return value;
    }
    static public long getLongData(Context context,SP_KEY spKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        long value = sharedPreferences.getLong(spKey.name(), -1);
        return value;
    }

    //newly added
    public static void saveboolenData(Context context, boolean status, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(spKey.name(), status);
        editor.commit();
    }

    public static boolean getbooleanData(Context context, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preference_db, Context.MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean(spKey.name(), SIMPLE_FALSE);
        return status;
    }

}

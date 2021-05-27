package coreframework.taskframework;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import coreframework.processing.CheckForUpdateProcessing;
import coreframework.processing.MerchantInfoProcessing;
import coreframework.processing.logout.LogoutProcessing;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.Contact_Us_Activity;


/**
 * Created by 10030 on 11/25/2016.
 */
public class MainGenericActivity extends FragmentActivity implements YPCHeadlessCallback {

//    private Menu optionsMenu;
//    final private FragmentManager fragmentManager = getSupportFragmentManager();
//    private static final String TAG = MainGenericActivity.class.getSimpleName();
//    public KioskMode kioskMode;
//    protected View mDecorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayUseLogoEnabled(false);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);



//        kioskMode = KioskMode.getKioskMode();
//        mDecorView = getWindow().getDecorView();
//        hideSystemUI();
//
//        setUpKioskMode();


        Thread.setDefaultUncaughtExceptionHandler(new HandleUncaughtException(this));
    }

//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        //move current activity to front if required
//        Log.i(TAG, "onUserLeaveHint()...");
//        kioskMode.moveTaskToFront(this);
//    }
//
//    private void hideSystemUI() {
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//    }
    private boolean showMenu= true;
    public void showMenu(boolean show){
        showMenu=show;


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(showMenu) {
//            this.optionsMenu=menu;
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            return super.onCreateOptionsMenu(menu);
        }else{
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_merchant_info:
                merchantInfo();
                return true;

            case R.id.action_bar_check_for_updates:

                checkforupdate();
                //Toast.makeText(MainGenericActivity.this, "Disabled.", Toast.LENGTH_LONG).show();
                 return true;
            case R.id.action_bar_contact_us:
                Intent intent= new Intent(MainGenericActivity.this, Contact_Us_Activity.class);
                startActivity(intent);
                return true;
//            case R.id.action_bar_settings:
//                showSettingsDialog();
//                return  true;
            case R.id.exit_app:
                performSecureLogOff();
                //this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * show settings dialog
     */
//    private void showSettingsDialog() {
//        SettingFragment settingFragment = new SettingFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(SettingFragment.LOCKED_BUNDLE_KEY, kioskMode.isLocked(this));
//        settingFragment.setArguments(args);
//        settingFragment.show(fragmentManager, settingFragment.getClass().getSimpleName());
//        settingFragment.setActionHandler(new SettingFragment.IActionHandler() {
//            @Override
//            public void isLocked(boolean isLocked) {
//                int msg = isLocked ? R.string.setting_device_locked : R.string.setting_device_unlocked;
//                kioskMode.lockUnlock(MainGenericActivity.this, isLocked);
//                Toast.makeText(MainGenericActivity.this, "Hi: "+msg, Toast.LENGTH_LONG).show();
//
//                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
//            }
//        });
//    }

    //Kiosk

//    @Override
//    public void onBackPressed() {
//
//        if (!kioskMode.isLocked(this)) {
//            super.onBackPressed();
//
//            Toast.makeText(MainGenericActivity.this,"1111",Toast.LENGTH_LONG).show();
//        }
//
//
//        Toast.makeText(MainGenericActivity.this,"2222",Toast.LENGTH_LONG).show();
//    }

//    private void setUpKioskMode() {
//        if (!MySharedPreferences.isAppLaunched(this)) {
//            Log.d(TAG, "onCreate() locking the app first time");
//            kioskMode.lockUnlock(this, true);
//            MySharedPreferences.saveAppLaunched(this, true);
//        } else {
//            //check if app was locked
//            if (MySharedPreferences.isAppInKioskMode(this)) {
//                Log.d(TAG, "onCreate() locking the app");
//                kioskMode.lockUnlock(this, true);
//            }
//        }
//    }

    private void checkforupdate() {
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new CheckForUpdateProcessing( application, false));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(true);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }

    private void performSecureLogOff() {
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new LogoutProcessing( application, false));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(true);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }

    private void merchantInfo() {

        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new MerchantInfoProcessing(application, true));
        ProgressDialogFrag progress = new ProgressDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uiProcessorReference);
        progress.setCancelable(true);
        progress.setArguments(bundle);
        progress.show(getFragmentManager(), "progress_dialog");
    }

    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onProgressComplete() {

    }
}

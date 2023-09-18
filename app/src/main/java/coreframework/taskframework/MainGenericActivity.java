package coreframework.taskframework;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import coreframework.processing.MerchantInfoProcessing;
import coreframework.processing.logout.LogoutProcessing;
import wallet.ooredo.com.live.R;
import wallet.ooredo.com.live.application.CoreApplication;
import wallet.ooredo.com.live.mainmenu.Contact_Us_Activity;

public class MainGenericActivity extends FragmentActivity implements YPCHeadlessCallback {

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
        Thread.setDefaultUncaughtExceptionHandler(new HandleUncaughtException(this));
    }

    private boolean showMenu = true;

    public void showMenu(boolean show) {
        showMenu = show;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showMenu) {
//            this.optionsMenu=menu;
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
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
                //if (!((CoreApplication) getApplication()).isPOS())
                    //checkforupdate();
                //Toast.makeText(MainGenericActivity.this, "Disabled.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_bar_contact_us:
                Intent intent = new Intent(MainGenericActivity.this, Contact_Us_Activity.class);
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

    private void performSecureLogOff() {
        CoreApplication application = (CoreApplication) getApplication();
        String uiProcessorReference = application.addUserInterfaceProcessor(new LogoutProcessing(application, false));
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

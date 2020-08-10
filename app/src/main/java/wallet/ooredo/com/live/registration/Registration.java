package wallet.ooredo.com.live.registration;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import coreframework.taskframework.GenericActivity;
import coreframework.taskframework.YPCHeadlessCallback;
import wallet.ooredo.com.live.R;

public class Registration extends GenericActivity implements YPCHeadlessCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
//		ActionBar actionBar= getActionBar();
//		actionBar.hide();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(((EditText) findViewById(R.id.firstnameedit)).getWindowToken(), 0);
        imm.hideSoftInputFromInputMethod(((EditText) findViewById(R.id.lastnameedit)).getWindowToken(), 0);
        imm.hideSoftInputFromInputMethod(((EditText) findViewById(R.id.pincodeedit)).getWindowToken(), 0);
        imm.hideSoftInputFromInputMethod(((EditText) findViewById(R.id.emaileedit)).getWindowToken(), 0);

        ((Button) findViewById(R.id.submit_registration_info_button)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(Registration.this, Activate.class);
                startActivity(in);
            }
        });
    }
    @Override
    public void onProgressUpdate(int progress) {

    }
    @Override
    public void onProgressComplete() {

    }
}
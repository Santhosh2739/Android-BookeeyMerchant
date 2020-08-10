package wallet.ooredo.com.live.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import coreframework.taskframework.GenericActivity;
import wallet.ooredo.com.live.R;
import ycash.wallet.json.pojo.MerchantInfoResponse;

/**
 * Created by 10030 on 11/25/2016.
 */
public class MerchantInfoActivity extends GenericActivity {
    TextView view_profile_mobilenumber_text, view_profile_merchantname_text, view_profile_branchname_text, view_profile_emailid_text;

    Button view_profile_close_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_info_layout);
        view_profile_mobilenumber_text  = (TextView) findViewById(R.id.view_profile_mobilenumber_text);
        view_profile_branchname_text    = (TextView) findViewById(R.id.view_profile_branchname_text);
        view_profile_merchantname_text  = (TextView) findViewById(R.id.view_profile_merchantname_text);
        view_profile_emailid_text       = (TextView) findViewById(R.id.view_profile_emailid_text);

        view_profile_close_btn          = (Button) findViewById(R.id.view_profile_close_btn);
        String data= getIntent().getStringExtra("view_profile");
        MerchantInfoResponse merchantInfoResponse= new Gson().fromJson(data, MerchantInfoResponse.class);

        view_profile_mobilenumber_text.setText(merchantInfoResponse.getMobileNumber());
        view_profile_branchname_text.setText(merchantInfoResponse.getBranch());
        view_profile_merchantname_text.setText(merchantInfoResponse.getMerchantName());
        view_profile_emailid_text.setText(merchantInfoResponse.getMerchantEmailId());

        view_profile_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}

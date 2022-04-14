package wallet.ooredo.com.live.mainmenu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import coreframework.taskframework.GenericActivity;
import wallet.ooredo.com.live.R;

/**
 * Created by 10030 on 11/29/2016.
 */
public class Contact_Us_Activity extends GenericActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);

        ((Button)findViewById(R.id.contact_us_screen_connect_executive_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phNumber = ((TextView)findViewById(R.id.contact_us_screen_mobile_number_text)).getText().toString().trim();
                Intent intent = new Intent("android.intent.action.DIAL");
                intent.setData(Uri.parse("tel:" + phNumber));
                startActivity(intent);
            }
        });
    }
}

package wallet.ooredo.com.live.registration;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import wallet.ooredo.com.live.R;

public class Activate extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activate);
		ActionBar actionBar= getActionBar();
		actionBar.hide();
		((Button)findViewById(R.id.gen_act_data_btn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(Activate.this, ServiceActivated.class);
				startActivity(in);
			}
		});
	}

}

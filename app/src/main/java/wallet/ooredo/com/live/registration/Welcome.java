package wallet.ooredo.com.live.registration;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import wallet.ooredo.com.live.R;

public class Welcome extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		ActionBar actionBar= getActionBar();
		actionBar.hide();
		((Button)findViewById(R.id.continue_tcBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in= new Intent(Welcome.this, Registration.class);
				startActivity(in);
			}
		});
	}

}

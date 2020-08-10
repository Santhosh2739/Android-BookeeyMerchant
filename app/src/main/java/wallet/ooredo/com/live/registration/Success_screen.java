package wallet.ooredo.com.live.registration;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import wallet.ooredo.com.live.R;

public class Success_screen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.success_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.success_screen, menu);
		return true;
	}

}

package wallet.ooredo.com.live.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyIntentChooserReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);
        // do something here

        Toast.makeText(context,"Intent selected "+intent.getAction(),Toast.LENGTH_LONG).show();
    }
}
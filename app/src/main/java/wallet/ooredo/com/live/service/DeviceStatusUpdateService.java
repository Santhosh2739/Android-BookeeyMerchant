package wallet.ooredo.com.live.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DeviceStatusUpdateService extends IntentService {

    public DeviceStatusUpdateService() {
        super("DeviceStatusUpdateService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(this, "DeviceStatusUpdateService", Toast.LENGTH_SHORT).show();
        Log.e("DeviceStatusUpdate1","DeviceStatusUpdateService1");
        while (true) {
            //one minute is 60*1000
            try {
                Thread.sleep(5 * 1000);
                Toast.makeText(getApplicationContext(), "getting app count", Toast.LENGTH_LONG).show();
                Log.e("DeviceStatusUpdate2","DeviceStatusUpdateService2");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
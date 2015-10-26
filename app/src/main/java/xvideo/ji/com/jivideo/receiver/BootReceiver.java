package xvideo.ji.com.jivideo.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xvideo.ji.com.jivideo.service.CoreService;
import xvideo.ji.com.jivideo.utils.JiLog;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    public static final String CORE_SERVICE = "xvideo.ji.com.jivideo.CoreReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            JiLog.error(TAG, "boot complete");
            Intent service = new Intent(context, CoreService.class);
            service.setAction(CORE_SERVICE);
            context.startService(service);
        }
    }
}

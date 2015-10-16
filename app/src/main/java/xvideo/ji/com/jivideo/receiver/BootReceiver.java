package xvideo.ji.com.jivideo.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xvideo.ji.com.jivideo.utils.JiLog;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            JiLog.error(TAG, "boot complete");
        }
    }
}

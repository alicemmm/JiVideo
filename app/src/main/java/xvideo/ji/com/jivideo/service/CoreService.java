package xvideo.ji.com.jivideo.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import xvideo.ji.com.jivideo.request.AdvertiseApi;
import xvideo.ji.com.jivideo.request.AliveApi;

public class CoreService extends Service {
    private static final String TAG = CoreService.class.getSimpleName();

    public static final String ACTION_START = "xvideo.ji.com.intent.action.START";

    public static final String ACTION_ALIVE_REQ = "xvideo.ji.com.intent.action.ALIVE_REQ";
    public static final String ACTION_SHOW_AD = "xvideo.ji.com.intent.action.SHOW_AD";

    private static final int HANDLER_ALIVE_REQ = 1;
    private static final int HANDLER_SHOW_AD = 2;

    private Context mContext;
    private AlivePolling mAlivePolling;
    private AdvertisePolling mAdvertisePolling;

    private volatile Looper mCoreLooper;
    private volatile AliveHandler mCoreHandler;

    private final class AliveHandler extends Handler {
        public AliveHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_ALIVE_REQ:
                    AliveApi.getInstance().req();
                    break;
                case HANDLER_SHOW_AD:
                    AdvertiseApi.getInstance().showAd();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        HandlerThread thread = new HandlerThread("CoreService - core");
        thread.start();

        mCoreLooper = thread.getLooper();
        mCoreHandler = new AliveHandler(mCoreLooper);

        if (mAlivePolling == null) {
            mAlivePolling = new AlivePolling(mContext);
            mAlivePolling.start();
        }

        if (mAdvertisePolling == null) {
            mAdvertisePolling = new AdvertisePolling(mContext);
            mAdvertisePolling.start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        do {
            if (intent == null) {
                Log.w(TAG, "intent == null");
                break;
            }

            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                break;
            }

            if (action.equals(ACTION_START)) {

            } else if (action.equals(ACTION_ALIVE_REQ)) {
                mCoreHandler.sendEmptyMessage(HANDLER_ALIVE_REQ);
            } else if (action.equals(ACTION_SHOW_AD)) {
                mCoreHandler.sendEmptyMessage(HANDLER_SHOW_AD);
            }
        } while (false);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCoreLooper.quit();

        if (mAlivePolling != null) {
            mAlivePolling.stop();
            mAlivePolling = null;
        }

        if (mAdvertisePolling != null) {
            mAdvertisePolling.stop();
            mAdvertisePolling = null;
        }
    }
}

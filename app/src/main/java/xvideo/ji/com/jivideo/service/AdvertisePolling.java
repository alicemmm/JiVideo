package xvideo.ji.com.jivideo.service;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import xvideo.ji.com.jivideo.request.AdvertiseApi;
import xvideo.ji.com.jivideo.utils.Utils;

public class AdvertisePolling implements Runnable{
    private final String TAG = AdvertisePolling.class.getSimpleName();

    private static final int AD_DEFAULT_TIME = (int) (1000 );

    private static final int STATUS_FLAG_INIT = 0;
    private static final int STATUS_FLAG_RUN = 1;
    private static final int STATUS_FLAG_STOP_NORMAL = 2;
    private static final int STATUS_FLAG_STOP_EXCEPTION = 3;

    Context mContext;

    private AtomicInteger mAtomicInteger = new AtomicInteger(0);

    Thread mThread;

    int mThreadFlag;

    public AdvertisePolling(Context context) {
        mContext = context;

        mThread = new Thread(this);
        mThread.setName(TAG);

        mThreadFlag = STATUS_FLAG_INIT;
    }

    public void start() {
        if (mThread != null && mThreadFlag == STATUS_FLAG_INIT) {
            mThread.start();
        }
    }

    public void stop() {
        mThreadFlag = STATUS_FLAG_STOP_NORMAL;

        mAtomicInteger.incrementAndGet();
    }

    public void forceStop() {
        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
        }
    }

    public boolean isStopped() {
        if (mThread == null) {
            return true;
        }

        return !mThread.isAlive();
    }

    public void run() {
        mThreadFlag = STATUS_FLAG_RUN;

        Log.e(TAG, "thread run");

        while (mAtomicInteger.get() == 0) {
            try {
                Thread.sleep(100);

                if (!Utils.isNetworkConnected(mContext)) {
                    Thread.sleep(1000 * 60);
                    continue;
                }

                //TODO
                AdvertiseApi.getInstance().showAd();

                Thread.sleep(AD_DEFAULT_TIME);

            } catch (InterruptedException e) {
                mThreadFlag = STATUS_FLAG_STOP_NORMAL;
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mThreadFlag != STATUS_FLAG_STOP_NORMAL) {
            mThreadFlag = STATUS_FLAG_STOP_EXCEPTION;
        }
    }}

package xvideo.ji.com.jivideo.service;


import android.content.Context;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import xvideo.ji.com.jivideo.request.AliveApi;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class AlivePolling implements Runnable{
    private final String TAG = AlivePolling.class.getSimpleName();

    private static final int ALIVE_DEFAULT_TIME = 60000 * 3;

    private static final int STATUS_FLAG_INIT = 0;
    private static final int STATUS_FLAG_RUN = 1;
    private static final int STATUS_FLAG_STOP_NORMAL = 2;
    private static final int STATUS_FLAG_STOP_EXCEPTION = 3;

    Context mContext;

    private AtomicInteger mAtomicInteger = new AtomicInteger(0);

    Thread mThread;
    int mThreadFlag;

    public AlivePolling(Context context) {
        this.mContext = context;

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

    @Override
    public void run() {
        mThreadFlag = STATUS_FLAG_RUN;

        Log.e(TAG, "thread run.");

        while (mAtomicInteger.get() == 0) {
            try {
                Thread.sleep(100);

                if (!Utils.isNetworkConnected(mContext)) {
                    Thread.sleep(1000 * 60);
                    continue;
                }

//                添加活跃汇报接口处理 需要验证
                JiLog.error(TAG,"alive run");
                AliveApi.getInstance().req();

                Thread.sleep(ALIVE_DEFAULT_TIME);

            } catch (InterruptedException e) {
                mThreadFlag = STATUS_FLAG_STOP_EXCEPTION;
                Log.e(TAG, "Thread InterruptException:" + e);
            } catch (Exception e) {
                Log.e(TAG, "Thread Exception:" + e);
            }
        }

        if (mThreadFlag != STATUS_FLAG_STOP_NORMAL) {
            mThreadFlag = STATUS_FLAG_STOP_EXCEPTION;
        }
        Log.e(TAG, "Thread stop,flag:" + mThreadFlag);
    }
}

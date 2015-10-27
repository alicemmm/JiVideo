package xvideo.ji.com.jivideo.service;


import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

import xvideo.ji.com.jivideo.data.ScoreDataInfo;
import xvideo.ji.com.jivideo.request.TaskMonitorApi;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class TaskMonitorPolling implements Runnable {
    private static final String TAG = TaskMonitorPolling.class.getSimpleName();

    private static final int MONITOR_INTERVAL = 1000 * 10;  // 10s
    private static final int RE_MONITOR_MAX_COUNT = 10;

    private static final int STATUS_FLAG_INIT = 0;
    private static final int STATUS_FLAG_RUN = 1;
    private static final int STATUS_FLAG_STOP_NORMAL = 2;
    private static final int STATUS_FLAG_STOP_EXCEPTION = 3;

    private Context mContext;

    private String mPkgName;

    private int mTimeMillis;

    private int mDurationRuntime;  // s

    private int mReMonitorCount;

    private ScoreDataInfo mScoreDataInfo;

    private AtomicInteger mAtomicInteger = new AtomicInteger(0);

    private Thread mThread;
    private int mThreadFlag;

    public TaskMonitorPolling(Context context, String pkgName, int timeMillis, ScoreDataInfo data) {
        mContext = context;

        mPkgName = pkgName;

        mTimeMillis = timeMillis;

        mScoreDataInfo = data;

        mThread = new Thread(this);
        mThread.setName(TAG);

        mThreadFlag = STATUS_FLAG_INIT;

    }

    public void start() {
        if (mThread != null && mThreadFlag == STATUS_FLAG_INIT) {
            mThread.start();
        }
    }

    public void forceStop() {
        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
        }
    }

    public void stop() {
        mThreadFlag = STATUS_FLAG_STOP_NORMAL;
        mAtomicInteger.incrementAndGet();
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

        JiLog.error(TAG, "thread run.");

        while (mAtomicInteger.get() == 0) {
            try {
                Thread.sleep(100); //for safe
                if (Utils.isAppForegroundRunning(mContext, mPkgName)) {
                    mDurationRuntime += MONITOR_INTERVAL;
                    JiLog.error(TAG, "DurationRuntime=" + mDurationRuntime + ", pkgName=" + mPkgName);
                } else {
                    mDurationRuntime = 0;
                    mReMonitorCount++;
                    JiLog.error(TAG, "Not running. monitor count=" + mReMonitorCount + ", pkgName=" + mPkgName);
                }

                if (mDurationRuntime >= mTimeMillis) {
                    JiLog.key(TAG, "Completed, stop monitor. pkgName=" + mPkgName);

                    //complete
                    TaskMonitorApi.getInstance().completed(mScoreDataInfo);

                    stop();
                } else {
                    if (mReMonitorCount >= RE_MONITOR_MAX_COUNT) {
                        JiLog.key(TAG, "ReMonitorCount >= " + RE_MONITOR_MAX_COUNT +
                                " force stop monitor, pkgName=" + mPkgName);

                        forceStop();
                    }
                }

            } catch (InterruptedException e) {
                mThreadFlag = STATUS_FLAG_STOP_NORMAL;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mThreadFlag != STATUS_FLAG_STOP_NORMAL) {
            mThreadFlag = STATUS_FLAG_STOP_EXCEPTION;
        }

        //remove
        TaskMonitorApi.getInstance().remove(mPkgName);
        JiLog.key(TAG, "thread stop. flag: " + mThreadFlag);
    }
}

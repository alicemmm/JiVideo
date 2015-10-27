package xvideo.ji.com.jivideo.request;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;

import xvideo.ji.com.jivideo.MyApplication;
import xvideo.ji.com.jivideo.data.ScoreDataInfo;
import xvideo.ji.com.jivideo.manager.PointOperateApi;
import xvideo.ji.com.jivideo.service.TaskMonitorPolling;
import xvideo.ji.com.jivideo.utils.JiLog;

public class TaskMonitorApi {
    private static final String TAG = TaskMonitorApi.class.getSimpleName();

    private static TaskMonitorApi mInstance = null;

    private static Context mContext;

    private static HashMap<String, TaskMonitorPolling> mTaskMonitorHM;

    private TaskMonitorApi() {
        mContext = MyApplication.getAppContext();
        mTaskMonitorHM = new HashMap<>();
    }

    public synchronized static TaskMonitorApi getInstance() {
        if (mInstance == null) {
            mInstance = new TaskMonitorApi();
        }

        return mInstance;
    }

    public synchronized void req(String pkgName, int timeMillis, ScoreDataInfo data) {
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }

        if (mTaskMonitorHM == null) {
            JiLog.error(TAG, "req | mTaskMonitorHM is null.");
            return;
        }

        TaskMonitorPolling taskMonitorPolling = mTaskMonitorHM.get(pkgName);
        if (taskMonitorPolling != null && !taskMonitorPolling.isStopped()) {
            JiLog.error(TAG, "req | is monitoring, igonre this req. pkgName=" + pkgName);
            return;
        }

        mTaskMonitorHM.remove(pkgName);

        taskMonitorPolling = new TaskMonitorPolling(mContext, pkgName, timeMillis, data);
        taskMonitorPolling.start();
        mTaskMonitorHM.put(pkgName, taskMonitorPolling);
    }

    public synchronized void remove(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }

        JiLog.error(TAG, "remove :pkgName=" + pkgName);

        if (mTaskMonitorHM == null) {
            return;
        }

        mTaskMonitorHM.remove(pkgName);
    }

    public synchronized void completed(ScoreDataInfo data) {
        if (data == null) {
            return;
        }

        PointOperateApi.getInstance().req(data);
    }
}

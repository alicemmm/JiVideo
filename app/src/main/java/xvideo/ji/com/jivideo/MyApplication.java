package xvideo.ji.com.jivideo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.Stack;

import xvideo.ji.com.jivideo.network.VolleyRequestManager;
import xvideo.ji.com.jivideo.utils.JiLog;

/**
 * Created by Domon on 15-9-21.
 */
public class MyApplication extends Application {
    public static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication mInstance = null;

    private static Context mContext;

    private Stack<Activity> mActivityStack;

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        mContext = getApplicationContext();

        init();
    }

    private void init() {
        VolleyRequestManager.init(mContext);
    }

    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }

        mActivityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activity == null) {
            return;
        }

        mActivityStack.remove(activity);

        activity.finish();
        activity = null;
    }

    public Activity currentActivity() {
        if (mActivityStack == null) {
            JiLog.error(TAG, "mActivityStack is null");
            return null;
        }

        return mActivityStack.lastElement();
    }

    public void finishLastActivity() {
        if (mActivityStack == null) {
            JiLog.error(TAG, "mActivityStack is null");
            return;
        }

        Activity activity = mActivityStack.lastElement();
        removeActivity(activity);

    }

    public void finishAllActivity() {
        if (mActivityStack == null) {
            JiLog.error(TAG, "mActivityStack is null");
            return;
        }

        for (int i = 0; i < mActivityStack.size(); i++) {
            if (mActivityStack.get(i) != null) {
                if (!mActivityStack.get(i).isFinishing()) {
                    mActivityStack.get(i).finish();
                }
            }
        }

        mActivityStack.clear();
    }
}

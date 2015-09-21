package xvideo.ji.com.jivideo.utils;

import android.util.Log;

import java.text.SimpleDateFormat;

/**
 * Created by Domon on 15-9-21.
 */
public final class JiLog {

    public static final String TAG = "JiLog";

    public static final int MAX_SIZE = 1 * 512 * 1024;

    public static final String DATE_FORMAT = "MM-dd HH:mm:ss";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);

    public static final int LEVEL_TRACE = 0;
    public static final int LEVEL_WARNING = 1;
    public static final int LEVEL_ERROR = 2;
    public static final int LEVEL_KEY = 3;
    public static final int LEVEL_NO_LOG = 4;

    private static int LOG_LEVEL = LEVEL_TRACE;

    public static String getExceptionStactTrace(Exception e) {
        if (null == e)
            return null;

        String ret = e.toString();
        StackTraceElement[] stack = e.getStackTrace();
        for (int i = 0; stack != null && i < stack.length; ++i) {
            ret += "\n" + stack[i].toString();
        }
        return ret;
    }

    public static void printExceptionStackTrace(Exception e) {
        if (e == null)
            return;

        key("Exception", "Exception: " + e.toString());
        StackTraceElement[] stack = e.getStackTrace();
        for (int i = 0; stack != null && i < stack.length; ++i) {
            key("Exception", stack[i].toString());
        }
    }

    public static void key(String tag, String content) {
        if (LOG_LEVEL > LEVEL_KEY) {
            return;
        }

        Log.e(tag, Thread.currentThread().getId() + "  " + content);
    }

    public static void error(String tag, String content) {
        if (LOG_LEVEL > LEVEL_ERROR) {
            return;
        }

        Log.e(tag, Thread.currentThread().getId() + "  " + content);
    }

    public static void warn(String tag, String content) {
        if (LOG_LEVEL > LEVEL_WARNING) {
            return;
        }

        Log.w(tag, Thread.currentThread().getId() + "  " + content);
    }

    public static void trace(String tag, String content) {
        if (LOG_LEVEL > LEVEL_TRACE) {
            return;
        }

        Log.i(tag, Thread.currentThread().getId() + "  " + content);
    }

    public static int getPriority() {
        return LOG_LEVEL;
    }

    public static void setPriority(int priority) {
        LOG_LEVEL = priority;
    }

    public static boolean openLog(int level) {
        if (level >= LEVEL_NO_LOG || level < LEVEL_TRACE) {
            return false;
        }

        setPriority(level);

        return true;
    }

    public static void closeLog() {
        setPriority(LEVEL_ERROR);
    }

    public static int testLog() {
        return -9;
    }
}

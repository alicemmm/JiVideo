package xvideo.ji.com.jivideo.utils;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import xvideo.ji.com.jivideo.MyApplication;
import xvideo.ji.com.jivideo.activity.MainActivity;
import xvideo.ji.com.jivideo.config.Consts;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static String formatSize(long totalBytes) {
        if (totalBytes >= 1000000) {
            return ((String.format("%.1f", (float) totalBytes / 1000000)) + "MB");
        }
        if (totalBytes >= 1000) {
            return ((String.format("%.1f", (float) totalBytes / 1000)) + "KB");
        } else {
            return (totalBytes + "Bytes");
        }
    }

    public static String getApkPath() {
        String externalStoragePath = getExternalStoragePath();
        if (externalStoragePath == null || externalStoragePath.isEmpty()) {
            return null;
        }

        String result = externalStoragePath + File.separator + Consts.APP_ROOT_DIRECTORY
                + File.separator + Consts.APP_APK_DIRECTORY;

        checkDirectory(result);

        return result;
    }

    public static String getExternalStoragePath() {
        if (isExternalStorageWriteable()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            JiLog.warn(TAG, "external storage is not writeable!");
            return null;
        }
    }

    public static boolean isExternalStorageWriteable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static void checkDirectory(String dir) {
        if (dir == null || dir.isEmpty()) {
            return;
        }

        if (!isExternalStorageWriteable()) {
            return;
        }

        File f = new File(dir);
        if (!f.exists()) {
            String[] pathSeg = dir.split(File.separator);
            String path = "";
            for (String temp : pathSeg) {
                if (TextUtils.isEmpty(temp)) {
                    path += File.separator;
                    continue;
                } else {
                    path += temp + File.separator;
                }
                File tempPath = new File(path);
                if (tempPath.exists() && !tempPath.isDirectory()) {
                    tempPath.delete();
                }
                tempPath.mkdirs();
            }
        } else {
            if (!f.isDirectory()) {
                f.delete();
                f.mkdirs();
            }
        }
    }

    public static boolean isAppInstalled(Context context, String pkgName) {
        if (context == null || TextUtils.isEmpty(pkgName)) {
            return false;
        }

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkgName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        file.delete();
    }

    public static boolean checkAppPackage(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return false;
        }

        boolean result = true;
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            deleteFile(filePath);
            result = false;
        }

        if (info == null) {
            result = false;
        }

        return result;
    }

    public static void installApk(Context context, String localFile) {
        if (TextUtils.isEmpty(localFile)) {
            JiLog.error(TAG, "installApk | mLocalFile is empty.");
            return;
        }

        if (!checkAppPackage(context, localFile)) {
            //package is bad
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(localFile)), "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "install failed", Toast.LENGTH_SHORT).show();
        }
    }


    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            context = MyApplication.getAppContext();
        }

        boolean isOK = false;

        try {
            boolean isWifiOK;
            boolean isMobileOK;

            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connManager == null) {
                return false;
            }

            NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (state == null) {
                return false;
            }
            if (NetworkInfo.State.CONNECTED == state) {
                isWifiOK = true;
            } else {
                isWifiOK = false;
            }

            state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (state == null) {
                return false;
            }
            if (NetworkInfo.State.CONNECTED == state) {
                isMobileOK = true;
            } else {
                isMobileOK = false;
            }

            if (isMobileOK || isWifiOK) {
                isOK = true;
            }
        } catch (Exception e) {
            isOK = false;
        }

        return isOK;
    }

    public static boolean isFileExist(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        File file = new File(fileName);
        return file.exists();
    }

    public static boolean isAppForegroundRunning(Context context, String pkgName) {
        if (context == null || TextUtils.isEmpty(pkgName)) {
            return false;
        }

        boolean result = false;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : list) {
                if (info == null) {
                    continue;
                }
                if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    String pkg = Arrays.toString(info.pkgList);
                    if (TextUtils.isEmpty(pkg)) {
                        continue;
                    }
                    if (("[" + pkgName + "]").equals(Arrays.toString(info.pkgList))) {
                        result = true;
                        break;
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info == null) {
                    continue;
                }
                if (pkgName.equals(info.topActivity.getPackageName())) {// info.baseActivity.getPackageName()
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    public static boolean isAppRunning(Context context, String pkgName) {
        if (context == null || TextUtils.isEmpty(pkgName)) {
            return false;
        }

        boolean result = false;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runInfoList = am.getRunningAppProcesses();
        if (runInfoList == null || runInfoList.isEmpty()) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo runInfo : runInfoList) {
            if (runInfo == null) {
                continue;
            }

            String[] pkgNameList = runInfo.pkgList;
            if (pkgNameList == null || pkgNameList.length == 0) {
                continue;
            }

            for (int i = 0; i < pkgNameList.length; ++i) {
                String runPkgName = pkgNameList[i];
                if (pkgName.equals(pkgNameList[i])) {
                    result = true;
                    break;
                }
            }

            if (result) {
                break;
            }
        }

        return result;
    }

    public static int launcherApp(Context context, String pkgName) {
        if (context == null || TextUtils.isEmpty(pkgName)) {
            return -1;
        }

        int result = 0;
        do {
            try {
                PackageInfo packageinfo = null;
                try {
                    packageinfo = context.getPackageManager().getPackageInfo(pkgName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    JiLog.printExceptionStackTrace(e);
                    result = -1;
                    break;
                }

                if (packageinfo == null) {
                    result = -2;
                    break;
                }

                Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                resolveIntent.setPackage(packageinfo.packageName);

                List<ResolveInfo> resolveinfoList = context.getPackageManager()
                        .queryIntentActivities(resolveIntent, 0);
                if (resolveinfoList == null) {
                    result = -3;
                    break;
                }

                ResolveInfo resolveinfo = resolveinfoList.iterator().next();
                if (resolveinfo == null) {
                    result = -4;
                    break;
                }

                String packageName = resolveinfo.activityInfo.packageName;
                String className = resolveinfo.activityInfo.name;
                ComponentName cn = new ComponentName(packageName, className);

                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    createDeskShortCut(pkgName, cn, context);
                    context.startActivity(intent);
                } catch (Exception e) {
                    JiLog.printExceptionStackTrace(e);
                    result = -5;
                    break;
                }
            } catch (Exception e) {
                JiLog.printExceptionStackTrace(e);
                result = -1;
                break;
            }
        } while (false);

        return result;
    }

    public static void createDeskShortCut(String pkgName, ComponentName cn, Context context) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            PackageInfo info = pm.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (info != null) {
                applicationInfo = info.applicationInfo;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (applicationInfo == null) {
            return;
        }

        String appName = pm.getApplicationLabel(applicationInfo).toString();
        Drawable drawable = applicationInfo.loadIcon(pm);
        BitmapDrawable bd = (BitmapDrawable) drawable;

        Intent shourCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shourCutIntent.putExtra("duplicate", false);
        shourCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        shourCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bd.getBitmap());

        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        if (cn != null) {
            intent.setComponent(cn);
        } else {
            intent.setClass(context, MainActivity.class);
        }

        shourCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shourCutIntent);
        JiLog.error(TAG, "create desk short cut successful");
    }
}

package xvideo.ji.com.jivideo.utils;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import xvideo.ji.com.jivideo.MyApplication;
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

}

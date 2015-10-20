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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import xvideo.ji.com.jivideo.MyApplication;
import xvideo.ji.com.jivideo.config.Consts;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();


    public static String getDevId() {
        try {
            return ((TelephonyManager) MyApplication.getAppContext().getSystemService(
                    Context.TELEPHONY_SERVICE)).getDeviceId(); // imei
        } catch (Exception e) {
            return "";
        }
    }

    public static int getAppVersionCode() {
        int result = -1;
        try {
            PackageManager pm = MyApplication.getAppContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(MyApplication.getAppContext().getPackageName(), 0);
            result = pi.versionCode;
        } catch (Exception e) {
            JiLog.printExceptionStackTrace(e);
        }

        return result;
    }

    public static String getAppVersionName() {
        String result = null;
        try {
            PackageManager pm = MyApplication.getAppContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(MyApplication.getAppContext().getPackageName(), 0);
            result = pi.versionName;
        } catch (Exception e) {
            JiLog.printExceptionStackTrace(e);
        }

        return result;
    }

    /*
     * MCC+MNC
     *
     * MCC：Mobile Country Code，移动国家码，共3位. MNC:Mobile NetworkCode，移动网络码，共2位.
     * 中国国际移动码为460. 移动的代码为00和02，联通的代码为01，电信的代码为03
     */
    public static String getNetworkOperator() {
        try {
            return ((TelephonyManager) MyApplication.getAppContext().getSystemService(
                    Context.TELEPHONY_SERVICE)).getNetworkOperator(); // MCC+MNC
        } catch (Exception e) {
            return "";
        }
    }

    /*
     * 获取网络类型
     *
     * 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
     */
    public static Consts.NetworkType getCurrentNetworkType() {
        try {
            ConnectivityManager connManager = (ConnectivityManager) MyApplication.getAppContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (NetworkInfo.State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()) {
                return Consts.NetworkType.NETWORK_TYPE_WIFI;
            }

            if (NetworkInfo.State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState()) {
                int subtype = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getSubtype();
//            String name = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//                    .getSubtypeName();
//            Log.e(TAG, "name:" + name + "=" + "subtype:" + subtype);
                return getMobileNetworkType(subtype);
            } else {
                return Consts.NetworkType.NETWORK_TYPE_UNKNOWN;
            }
        } catch (Exception e) {
            return Consts.NetworkType.NETWORK_TYPE_UNKNOWN;
        }

    }

    private static Consts.NetworkType getMobileNetworkType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:// 1
            case TelephonyManager.NETWORK_TYPE_EDGE:// 2
            case TelephonyManager.NETWORK_TYPE_CDMA:// 4
            case TelephonyManager.NETWORK_TYPE_1xRTT:// 7
            case TelephonyManager.NETWORK_TYPE_IDEN:// 11
                return Consts.NetworkType.NETWORK_TYPE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:// 3
            case TelephonyManager.NETWORK_TYPE_EVDO_0:// 5
            case TelephonyManager.NETWORK_TYPE_EVDO_A:// 6
            case TelephonyManager.NETWORK_TYPE_HSDPA:// 8
            case TelephonyManager.NETWORK_TYPE_HSUPA:// 9
            case TelephonyManager.NETWORK_TYPE_HSPA:// 10
            case TelephonyManager.NETWORK_TYPE_EVDO_B:// 12
            case 14:// TelephonyManager.NETWORK_TYPE_EHRPD:
            case 15:// TelephonyManager.NETWORK_TYPE_HSPAP:
                return Consts.NetworkType.NETWORK_TYPE_3G;
            case 13:// TelephonyManager.NETWORK_TYPE_LTE:
                return Consts.NetworkType.NETWORK_TYPE_4G;
            default:
                return Consts.NetworkType.NETWORK_TYPE_UNKNOWN;
        }
    }

    public static int getScreenWidth() {
        try {
            return ((WindowManager) MyApplication.getAppContext().getSystemService(
                    Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth(); // pixel
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getScreenHeight() {
        try {
            return ((WindowManager) MyApplication.getAppContext().getSystemService(
                    Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();// pixel
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    public static String getPhoneManuf() {
        return Build.MANUFACTURER;
    }

    public static int getOSType() {
        return Consts.OS_TYPE;
    }

    public static String getInstalledAppList() {
        String result = ""; // alist

        ArrayList<String> appLists = getInstalledAppNames();
        if (appLists != null && appLists.size() > 0) {
            boolean isFirst = true;
            StringBuilder sbApp = new StringBuilder();
            for (String appName : appLists) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sbApp.append(";");
                }
                sbApp.append(appName);
            }
            result = sbApp.toString();
        }

        return result;
    }

    private static ArrayList<String> getInstalledAppNames() {
        ArrayList<String> result = null;

        PackageManager packageManager = MyApplication.getAppContext().getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if (packageInfos == null) {
            return null;
        }

        result = new ArrayList<String>();
        for (int i = 0; i < packageInfos.size(); i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String pkgName = packageInfo.packageName;
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                result.add(pkgName + "," + appName);
            }
        }

        return result;
    }

    /**
     * SIM的状态信息：

     * SIM_STATE_UNKNOWN          未知状态 0
     * SIM_STATE_ABSENT           没插卡 1
     * SIM_STATE_PIN_REQUIRED     锁定状态，需要用户的PIN码解锁 2
     * SIM_STATE_PUK_REQUIRED     锁定状态，需要用户的PUK码解锁 3
     * SIM_STATE_NETWORK_LOCKED   锁定状态，需要网络的PIN码解锁 4
     * SIM_STATE_READY            就绪状态 5
     * @return
     */

    public static int getSIMState() {
        try {
            TelephonyManager tm = (TelephonyManager) MyApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimState();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) MyApplication.getAppContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }



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

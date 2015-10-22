package xvideo.ji.com.jivideo.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xvideo.ji.com.jivideo.MyApplication;
import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Preferences;

public class BaseInfoData {
    private static final String TAG = BaseInfoData.class.getSimpleName();

    public static Map<String, String> getBaseInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("channelID", getChannelId() + "");
        map.put("subChannelID", getSubChannelId() + "");
        map.put("appID", getAppVersionCode() + "");
        map.put("imei", getDevId());
        map.put("phoneNumber", getPhoneNumber());
        map.put("os", getOSType() + "");
        map.put("net", getCurrentNetworkType() + "");
        map.put("brand", getPhoneModel());
        map.put("simState", getSIMState() + "");
        map.put("width", getScreenWidth() + "");
        map.put("height", getScreenHeight() + "");
        map.put("appListInstall", getInstalledAppList());
        map.put("ipAddress", TextUtils.isEmpty(getIpAddress()) ? getLocalIpAddress() : getIpAddress());
        return map;
    }

    public static Map<String, String> getAliveInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("channelID", getChannelId() + "");
        map.put("subChannelID", getSubChannelId() + "");
        map.put("appID", getAppVersionCode() + "");
        map.put("imei", getDevId());
        return map;
    }

    public static String getUserId() {
        return Preferences.getStringValue("profiles_id");
    }

    public static void setUserId(String value) {
        Preferences.setStringValue("profiles_id", value);
    }

    public static void setMyPoint(int point) {
        Preferences.setIntegerValue("point", point);
    }

    public static int getMyPoint() {
        return Preferences.getIntegerValue("point");
    }


    public static int getChannelId() {
        int result = -1;
        try {
            Context context = MyApplication.getAppContext();
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            result = applicationInfo.metaData.getInt("CNXAD_JHCH");
        } catch (Exception e) {
            JiLog.key(TAG, "no key: CNXAD_JHCH");
        }

        return result;
    }

    public static int getSubChannelId() {
        int result = -1;
        try {
            Context context = MyApplication.getAppContext();
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            result = applicationInfo.metaData.getInt("CNXAD_JHSUBCH");
        } catch (Exception e) {
            JiLog.key(TAG, "no key: CNXAD_JHSUBCH");
        }

        return result;
    }

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

    public static String getPhoneNumber() {
        String result = null;
        try {
            TelephonyManager tm = (TelephonyManager) MyApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            result = tm.getLine1Number();
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
     * <p/>
     * SIM_STATE_UNKNOWN          未知状态 0
     * SIM_STATE_ABSENT           没插卡 1
     * SIM_STATE_PIN_REQUIRED     锁定状态，需要用户的PIN码解锁 2
     * SIM_STATE_PUK_REQUIRED     锁定状态，需要用户的PUK码解锁 3
     * SIM_STATE_NETWORK_LOCKED   锁定状态，需要网络的PIN码解锁 4
     * SIM_STATE_READY            就绪状态 5
     *
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
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
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

}

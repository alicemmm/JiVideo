package xvideo.ji.com.jivideo.config;


public final class Consts {

    public static final int OS_TYPE = 1;  // Android

    public static enum NetworkType {
        NETWORK_TYPE_UNKNOWN, NETWORK_TYPE_WIFI, NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_4G
    }

    //        public static final String GET_HOTVIDEO_URL = "http://api.hdyfhpoi.com/hots/api/4";

    public static final String SERVICE_DOMAIN = "http://192.168.5.99:81";
    public static final String SERVICE_DOMAIN2 = "http://192.168.0.155:8090";

    public static final String URL_CLIENT_ACTIVE = SERVICE_DOMAIN2 + "/reportClientActive.ashx";
    public static final String GET_HOTVIDEO_URL = SERVICE_DOMAIN + "/datas.ashx";
    public static final String GET_MAINVIDEO_URL = SERVICE_DOMAIN + "/getindex.ashx";
    public static final String GET_APP_LIST = SERVICE_DOMAIN + "/getAppList.ashx";
    public static final String GET_POINT_OPERATE = SERVICE_DOMAIN2 + "/operateUserPoints.ashx";

    public static final String APP_ROOT_DIRECTORY = "JiVideo";
    public static final String APP_APK_DIRECTORY = "APK";

    public static final String AD_GOOGLE_TABLE_ID = "ca-app-pub-5840358239603737/3683899001";

}

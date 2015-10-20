package xvideo.ji.com.jivideo.manager;


import android.content.Context;

import xvideo.ji.com.jivideo.MyApplication;

public class ClientInfoApi {
    private static final String TAG = ClientInfoApi.class.getSimpleName();

    private static ClientInfoApi mInstance;
    private static Context mContext;

    private ClientInfoApi() {
        mContext = MyApplication.getAppContext();
    }

    public static ClientInfoApi getInstance() {
        if (mInstance == null) {
            mInstance = new ClientInfoApi();
        }

        return mInstance;
    }

}

package xvideo.ji.com.jivideo.service;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import xvideo.ji.com.jivideo.MyApplication;
import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class CoreService extends Service {
    private static final String TAG = CoreService.class.getSimpleName();

    private InterstitialAd mInterstitialAd;

    private Handler mHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showAd();
            mHandler.postDelayed(runnable, 1000 * 10);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        JiLog.error(TAG,"oncreate service");
        init();

        mHandler.postDelayed(runnable, 1000 * 10);
    }

    private void init() {
        mInterstitialAd = new InterstitialAd(MyApplication.getAppContext());
        mInterstitialAd.setAdUnitId(Consts.AD_GOOGLE_TABLE_ID);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(Utils.getDevId())
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void showAd() {
        JiLog.error(TAG, "prepare show ad");
        if (mInterstitialAd.isLoaded()) {
            JiLog.error(TAG, "show ad");
            mInterstitialAd.show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

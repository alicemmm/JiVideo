package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.HotVideoData;
import xvideo.ji.com.jivideo.manager.HotVideoManager;
import xvideo.ji.com.jivideo.utils.JiLog;

/**
 * Created by Domon on 15-9-18.
 */
public class AboutActivity extends BaseActivity {
    private static final String TAG = AboutActivity.class.getSimpleName();
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mContext = this;

        HotVideoManager manager = new HotVideoManager(this, new HotVideoManager.onResponseListener() {
            @Override
            public void onFailure(String errMsg) {
                Toast.makeText(mContext, "xxxxx", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(HotVideoData hotVideoData) {
                Toast.makeText(mContext, "yyyy", Toast.LENGTH_LONG).show();
                JiLog.error(TAG, hotVideoData.toString());
            }
        });

        manager.req();
    }
}

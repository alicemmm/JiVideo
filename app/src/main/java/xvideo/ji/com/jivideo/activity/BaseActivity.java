package xvideo.ji.com.jivideo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import xvideo.ji.com.jivideo.MyApplication;

/**
 * Created by Domon on 15-9-21.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MyApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyApplication.getInstance().removeActivity(this);
    }
}

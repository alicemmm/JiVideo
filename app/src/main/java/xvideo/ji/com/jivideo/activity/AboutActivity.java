package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.os.Bundle;

import xvideo.ji.com.jivideo.R;

public class AboutActivity extends BaseActivity {
    private static final String TAG = AboutActivity.class.getSimpleName();
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mContext = this;

    }
}

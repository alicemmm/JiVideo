package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.os.Bundle;

import xvideo.ji.com.jivideo.R;

/**
 * Created by YinJim on 15/9/29.
 */
public class VideoDetailActivity extends BaseActivity {
    private static final String TAG = VideoDetailActivity.class.getSimpleName();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        mContext = this;
        init();
        
    }

    private void init() {

    }
}

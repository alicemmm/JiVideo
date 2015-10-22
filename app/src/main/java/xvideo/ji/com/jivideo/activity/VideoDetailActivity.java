package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.BaseInfoData;
import xvideo.ji.com.jivideo.data.HotVideoData;
import xvideo.ji.com.jivideo.data.ScoreDataInfo;
import xvideo.ji.com.jivideo.manager.PointOperateApi;

public class VideoDetailActivity extends BaseActivity {
    private static final String TAG = VideoDetailActivity.class.getSimpleName();

    private static final int HANDLE_FAILURE = 0;
    private static final int HANDLE_SUCCESS = 1;

    private Context mContext;
    private HotVideoData.HotsEntity mHotsEntity;

    private ScoreDataInfo mScoreDataInfo;

    @Bind(R.id.custom_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.detail_video_big_pic_iv)
    ImageView mBigPicIv;

    @Bind(R.id.detail_video_icon_iv)
    ImageView mIconIv;

    @Bind(R.id.detail_video_title_tv)
    TextView mTitleTv;

    @Bind(R.id.detail_video_content_tv)
    TextView mContentTv;

    @Bind(R.id.detail_video_need_point_tv)
    TextView mBigPointTv;

    @Bind(R.id.detail_video_small_point_tv)
    TextView mSmallPointTv;

    @Bind(R.id.adView)
    AdView mAdView;

    @OnClick(R.id.detail_video_more_detail_tv)
    void onClickMorePoint() {

    }

    @OnClick(R.id.detail_video_play_tv)
    void onClickPlay() {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_FAILURE:
                    doHandlerFailure(msg.obj);
                    break;
                case HANDLE_SUCCESS:
                    doHandlerSuccess(msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void doHandlerFailure(Object obj) {
        if (obj == null) {
            return;
        }
        Toast.makeText(mContext, obj.toString(), Toast.LENGTH_LONG).show();
    }

    private void doHandlerSuccess(Object obj) {
        Toast.makeText(mContext, "get one point!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        mContext = this;

        ButterKnife.bind(this);
        getData();

        initToolBar();

        init();

    }

    private void getData() {
        Intent intent = getIntent();
        mHotsEntity = intent.getParcelableExtra("data");
        if (mHotsEntity == null) {
            mHotsEntity = new HotVideoData.HotsEntity();
        }
    }

    private void initToolBar() {
        mToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title_text));
        mToolbar.setTitle(getString(R.string.my_point) + " " + mHotsEntity.getScore());
        mToolbar.setPadding(50, 0, 0, 0);
    }

    private void init() {
        Glide.with(mContext).load(mHotsEntity.getMain_icon()).into(mBigPicIv);
        Glide.with(mContext).load(mHotsEntity.getSmall_icon()).into(mIconIv);
        mTitleTv.setText(mHotsEntity.getTitle());
        mContentTv.setText(mHotsEntity.getDescription() + "\n" + mHotsEntity.getWatch() + " " + getString(R.string.watching));
        mBigPointTv.setText(mHotsEntity.getHigh_point() + getString(R.string.points));
//        mSmallPointTv.setText(getString(R.string.click_point) + " " + mHotsEntity.getLow_point()
//                + " " + getString(R.string.points2));

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                super.onAdOpened();
                mScoreDataInfo = new ScoreDataInfo();
                mScoreDataInfo.setOperate(PointOperateApi.OPERATE_MODEFY_POINT);
                mScoreDataInfo.setTitle("ad click");
                mScoreDataInfo.setOpPoint(1);
                mScoreDataInfo.setUserId(BaseInfoData.getUserId());

                asyncPointRecordReq();
            }
        });
    }

    private void asyncPointRecordReq() {
        PointOperateApi.getInstance().req(mScoreDataInfo).setModefyPointListener(
                new PointOperateApi.onModefyPointListener() {
            @Override
            public void result(ScoreDataInfo dataInfo) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

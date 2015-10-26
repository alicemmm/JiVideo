package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.BaseInfoData;
import xvideo.ji.com.jivideo.data.HotVideoData;
import xvideo.ji.com.jivideo.data.PointListData;
import xvideo.ji.com.jivideo.data.ScoreDataInfo;
import xvideo.ji.com.jivideo.manager.PointOperateApi;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class VideoDetailActivity extends BaseActivity {
    private static final String TAG = VideoDetailActivity.class.getSimpleName();

    private static final int HANDLER_POINT_RESULT = 0;

    private static final int HANDLE_POINT_FAILURE = 1;
    private static final int HANDLE_POINT_SUCCESS = 2;


    private Context mContext;
    private HotVideoData.HotsEntity mHotsEntity;

    private ScoreDataInfo mScoreDataInfo;

    private PointOperateApi mPointOperateApi;

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
        if (isBuyVideoIds(mHotsEntity.getId())) {
            Intent intent = new Intent(mContext, WebActivity.class);
            intent.putExtra("url", mHotsEntity.getUrl());
            startActivity(intent);
        } else {
            mScoreDataInfo.setTitle(mHotsEntity.getTitle());
            //todo test score
//      mScoreDataInfo.setOpPoint(-mHotsEntity.getHigh_point());
            mScoreDataInfo.setOpPoint(-1);
            mScoreDataInfo.setUserId(BaseInfoData.getUserId());
            mScoreDataInfo.setOperate(PointOperateApi.OPERATE_MODEFY_POINT);
            //expense score id
            mScoreDataInfo.setExpenseScoreId(mHotsEntity.getId());
            asyncPointRecordReq();
        }
    }

    private boolean isBuyVideoIds(int isId) {
        boolean result = false;
        String id = BaseInfoData.getBuyMovesIds();
        String[] ids = id.split(",");
        for (int i = 0; i < ids.length; ++i) {
            if (ids[i].equals(isId + "")) {
                result = true;
            }
        }

        return result;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_POINT_RESULT:
                    doHandlerPointResult(msg.obj);
                    break;
                case HANDLE_POINT_FAILURE:
                    doHandlerPointFailure(msg.obj);
                    break;
                case HANDLE_POINT_SUCCESS:
                    doHandlerPointSuccess(msg);
                    break;
                default:
                    break;
            }
        }
    };

    private void doHandlerPointFailure(Object obj) {
        if (obj == null) {
            return;
        }
        Toast.makeText(mContext, obj.toString(), Toast.LENGTH_LONG).show();
    }

    private void doHandlerPointSuccess(Message msg) {
        if (msg == null) {
            return;
        }

        int myPoint = msg.arg1;
        String buyVideoIds = (String) msg.obj;
        JiLog.error(TAG, "totalpoint=" + myPoint);
        JiLog.error(TAG, "buyVideoIds=" + buyVideoIds);

        BaseInfoData.setMyPoint(myPoint);
        BaseInfoData.setBuyMovesIds(buyVideoIds);

        mToolbar.setTitle(getString(R.string.my_point) + " " + BaseInfoData.getMyPoint());
    }

    private void doHandlerPointResult(Object obj) {
        if (obj == null) {
            return;
        }

        ScoreDataInfo dataInfo = (ScoreDataInfo) obj;
        boolean isSuccess = dataInfo.isUploadSuccess();
        if (isSuccess) {
            if (mScoreDataInfo.getOpPoint() > 0) {
                Toast.makeText(mContext, "get one point!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", mHotsEntity.getUrl());
                startActivity(intent);

                Toast.makeText(mContext, "Consume " + mHotsEntity.getHigh_point() + " points", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "get point failure", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        reqGetMyPoint();
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
        mToolbar.setTitle(getString(R.string.my_point) + " " + BaseInfoData.getMyPoint());
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

        mScoreDataInfo = new ScoreDataInfo();
        mScoreDataInfo.setOperate(PointOperateApi.OPERATE_MODEFY_POINT);
        mScoreDataInfo.setTitle("default method");
        mScoreDataInfo.setOpPoint(0);
        mScoreDataInfo.setUserId(BaseInfoData.getUserId());

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                super.onAdOpened();
                mScoreDataInfo.setTitle("ad click");
                mScoreDataInfo.setOpPoint(1);

                asyncPointRecordReq();
            }
        });
    }

    private void asyncPointRecordReq() {
        PointOperateApi.getInstance().req(mScoreDataInfo).setModefyPointListener(
                new PointOperateApi.onModefyPointListener() {
                    @Override
                    public void result(ScoreDataInfo dataInfo) {
                        Message msg = new Message();
                        msg.what = HANDLER_POINT_RESULT;
                        msg.obj = dataInfo;
                        mHandler.sendMessage(msg);
                    }
                });
    }

    private void reqGetMyPoint() {
        if (!Utils.isNetworkConnected(mContext)) {
            return;
        }

        if (TextUtils.isEmpty(BaseInfoData.getUserId())) {
            return;
        }

        ScoreDataInfo scoreDataInfo = new ScoreDataInfo();
        scoreDataInfo.setUserId(BaseInfoData.getUserId());
        scoreDataInfo.setOperate(PointOperateApi.OPERATE_GET_POINT);

        if (mPointOperateApi == null) {
            mPointOperateApi = new PointOperateApi(mContext, scoreDataInfo, new PointOperateApi.onResponseListener() {
                @Override
                public void onFailure(String errMsg) {
                    Message msg = new Message();
                    msg.obj = errMsg;
                    msg.what = HANDLE_POINT_FAILURE;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onSuccess(ArrayList<PointListData> datas, int totalPoints, String buyVideoIds) {
                    Message msg = new Message();
                    msg.arg1 = totalPoints;
                    msg.obj = buyVideoIds;
                    msg.what = HANDLE_POINT_SUCCESS;
                    mHandler.sendMessage(msg);
                }
            });
        }

        mPointOperateApi.req();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPointOperateApi != null) {
            mPointOperateApi.cancel();
        }
    }
}

package xvideo.ji.com.jivideo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.activity.VideoDetailActivity;
import xvideo.ji.com.jivideo.data.HotVideoData;
import xvideo.ji.com.jivideo.manager.MainVideoManager;
import xvideo.ji.com.jivideo.view.CustomItemFrameLayout;
import xvideo.ji.com.jivideo.view.PicSlideView;

public class MainFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = MainFragment.class.getSimpleName();

    private static final int HANDLE_FAILURE = 0;
    private static final int HANDLE_SUCCESS = 1;

    private CustomItemFrameLayout mBottomItem1Item;
    private CustomItemFrameLayout mBottomItem2Item;
    private CustomItemFrameLayout mBottomItem3Item;
    private CustomItemFrameLayout mTopItem1Item;
    private CustomItemFrameLayout mTopItem2Item;

    private Context mContext;

    private MainVideoManager mManager;

    private List<HotVideoData.HotsEntity> mPicSldeDatas;
    private List<HotVideoData.HotsEntity> mPicOtherDatas;

    private ArrayList<CustomItemFrameLayout> customItemFrameLayouts;

    @Bind(R.id.slide_main_psv)
    PicSlideView mPicSlideView;

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
        if (obj == null) {
            return;
        }

        HotVideoData videoData = (HotVideoData) obj;

        mPicSldeDatas = new ArrayList<>();
        mPicOtherDatas = new ArrayList<>();

        int size = videoData.getHots().size();
        for (int i = 0; i < size; ++i) {
            if (videoData.getHots().get(i).getIndexType() == 1) {
                mPicSldeDatas.add(videoData.getHots().get(i));
            } else {
                mPicOtherDatas.add(videoData.getHots().get(i));
            }
        }

        mPicSlideView.setDataAndInit(mPicSldeDatas);

        if (mPicOtherDatas.size() < 5) {
            return;
        }

        int picOtherSize = mPicOtherDatas.size();
        for (int i = 0; i < picOtherSize; ++i) {
            Glide.with(mContext).load(mPicOtherDatas.get(i).getSmall_icon()).into(customItemFrameLayouts.get(i).getItemImageView());
            customItemFrameLayouts.get(i).setItemInfo(mPicOtherDatas.get(i).getTitle());
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getActivity();

        findViewById(view);

        init();

        return view;
    }

    private void findViewById(View view) {
        mTopItem1Item = (CustomItemFrameLayout) view.findViewById(R.id.top_item1_fl);
        mTopItem2Item = (CustomItemFrameLayout) view.findViewById(R.id.top_item2_fl);
        mBottomItem1Item = (CustomItemFrameLayout) view.findViewById(R.id.bottom_item1_fl);
        mBottomItem2Item = (CustomItemFrameLayout) view.findViewById(R.id.bottom_item2_fl);
        mBottomItem3Item = (CustomItemFrameLayout) view.findViewById(R.id.bottom_item3_fl);
        mPicSlideView = (PicSlideView) view.findViewById(R.id.slide_main_psv);
    }

    private void init() {
        //TODO bitmap
        Resources resources = this.getResources();

        customItemFrameLayouts = new ArrayList<>();
        customItemFrameLayouts.add(mTopItem1Item);
        customItemFrameLayouts.add(mTopItem2Item);
        customItemFrameLayouts.add(mBottomItem1Item);
        customItemFrameLayouts.add(mBottomItem2Item);
        customItemFrameLayouts.add(mBottomItem3Item);

        customItemFrameLayouts.get(0).setOnClickListener(this);
        customItemFrameLayouts.get(1).setOnClickListener(this);
        customItemFrameLayouts.get(2).setOnClickListener(this);
        customItemFrameLayouts.get(3).setOnClickListener(this);
        customItemFrameLayouts.get(4).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        asyncMainVideoReq();
    }

    private void asyncMainVideoReq() {
        if (mManager == null) {
            mManager = new MainVideoManager(mContext, new MainVideoManager.onResponseListener() {
                @Override
                public void onFailure(String errMsg) {
                    Message msg = new Message();
                    msg.what = HANDLE_FAILURE;
                    msg.obj = errMsg;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onSuccess(HotVideoData hotVideoData) {
                    Message msg = new Message();
                    msg.what = HANDLE_SUCCESS;
                    msg.obj = hotVideoData;
                    mHandler.sendMessage(msg);
                }
            });
        }

        mManager.req();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        try {
            switch (id) {
                case R.id.top_item1_fl:
                    onClickDetailVideo(mPicOtherDatas.get(0));
                    break;
                case R.id.top_item2_fl:
                    onClickDetailVideo(mPicOtherDatas.get(1));
                    break;
                case R.id.bottom_item1_fl:
                    onClickDetailVideo(mPicOtherDatas.get(2));
                    break;
                case R.id.bottom_item2_fl:
                    onClickDetailVideo(mPicOtherDatas.get(3));
                    break;
                case R.id.bottom_item3_fl:
                    onClickDetailVideo(mPicOtherDatas.get(4));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClickDetailVideo(HotVideoData.HotsEntity data) {
        Intent intent = new Intent(mContext, VideoDetailActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.cancel();
        }
    }
}

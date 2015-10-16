package xvideo.ji.com.jivideo.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.activity.VideoDetailActivity;
import xvideo.ji.com.jivideo.data.HotVideoData;

public class PicSlideView extends FrameLayout {
    private static final String TAG = PicSlideView.class.getSimpleName();

    private static final int IMAGE_COUNT = 3;
    private static final int TIME_INTERVAL = 5;

    private static final boolean isAutoPlay = true;

    private Context mContext;

    private List<HotVideoData.HotsEntity> mWebDatas;
    private List<View> mImageViewsList;
    private List<View> mDotViewsList;

    private LinearLayout mDotGroup;

    private ViewPager mViewPager;
    private int mCurrentId = 0;

    private ScheduledExecutorService mScheduledExecutorService;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.setCurrentItem(mCurrentId);
        }
    };

    public PicSlideView(Context context) {
        this(context, null);
        mContext = context;
    }

    public PicSlideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public PicSlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setDataAndInit(List<HotVideoData.HotsEntity> webDatas) {
        this.mWebDatas = webDatas;

        initData();
        initUI(mContext);
        if (isAutoPlay) {
            startPlay();
        }
    }

    private void initData() {
        if (mWebDatas == null) {
            mWebDatas = new ArrayList<>();
        }

        mImageViewsList = new ArrayList<View>();
        mDotViewsList = new ArrayList<View>();
    }

    private void initUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_picsilde, this, true);
        mDotGroup = (LinearLayout) findViewById(R.id.dot_group);

        for (int i = 0; i < mWebDatas.size(); i++) {
            View viewItem = LayoutInflater.from(context).inflate(R.layout.view_slide_customitem, null);
            ImageView iconIv = (ImageView) viewItem.findViewById(R.id.item_pic_iv);
            TextView titleTv = (TextView) viewItem.findViewById(R.id.item_info_tv);
            TextView watching = (TextView) viewItem.findViewById(R.id.item_watch_tv);

            Glide.with(context)
                    .load(mWebDatas.get(i).getSmall_icon())
                    .into(iconIv);

            titleTv.setText(mWebDatas.get(i).getTitle());

            watching.setText(mWebDatas.get(i).getWatch() + mContext.getResources().getString(R.string.watching));

            mImageViewsList.add(viewItem);

            View dot = mDotGroup.getChildAt(i);
            dot.setVisibility(VISIBLE);
            mDotViewsList.add(dot);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setFocusable(true);

        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ((ViewPager) container).addView(mImageViewsList.get(position));

            //TODO pic jump
            mImageViewsList.get(position).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoDetailActivity.class);
                    intent.putExtra("data", mWebDatas.get(position));
                    mContext.startActivity(intent);
                }
            });

            return mImageViewsList.get(position);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getCount() {
            return mImageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        boolean isAutoPlay = false;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentId = position;

            for (int i = 0; i < mDotViewsList.size(); i++) {
                if (i == position) {
                    ((View) mDotViewsList.get(i)).setBackgroundResource(R.mipmap.dot_selected);
                } else {
                    ((View) mDotViewsList.get(i)).setBackgroundResource(R.mipmap.dot_unselected);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING://use hand
                    isAutoPlay = false;
                    break;
                case ViewPager.SCROLL_STATE_SETTLING://change image
                    isAutoPlay = true;
                    break;
                case ViewPager.SCROLL_STATE_IDLE://end or free
                    if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        mViewPager.setCurrentItem(0);
                    } else if (mViewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void startPlay() {
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, TIME_INTERVAL, TimeUnit.SECONDS);
    }

    public void stopPlay() {
        mScheduledExecutorService.shutdown();
    }

    private class SlideShowTask implements Runnable {
        @Override
        public void run() {
            synchronized (mViewPager) {
                mCurrentId = (mCurrentId + 1) % mImageViewsList.size();
                mHandler.obtainMessage().sendToTarget();
            }
        }
    }

}

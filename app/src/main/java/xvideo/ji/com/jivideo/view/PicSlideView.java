package xvideo.ji.com.jivideo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import xvideo.ji.com.jivideo.R;

public class PicSlideView extends FrameLayout {
    private static final String TAG = PicSlideView.class.getSimpleName();

    private static final int IMAGE_COUNT = 3;
    private static final int TIME_INTERVAL = 5;

    private static final boolean isAutoPlay = true;

    private Context mContext;

    private ArrayList<String> mWebImagesIds;
    private List<ImageView> mImageViewsList;
    private List<View> mDotViewsList;

    private LinearLayout mDotGroup;

    private ViewPager mViewPager;
    private int mCurrentId = 0;

    private ScheduledExecutorService mScheduledExecutorService;

    private Handler mHanlder = new Handler() {
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

        initData();
        initUI(context);
        if (isAutoPlay) {
            startPlay();
        }
    }

    private void initData() {
        mWebImagesIds = new ArrayList<>();
        mWebImagesIds.add("http://img.lakalaec.com/ad/57ab6dc2-43f2-4087-81e2-b5ab5681642d.jpg");
        mWebImagesIds.add("http://img.lakalaec.com/ad/cb56a1a6-6c33-41e4-9c3c-363f4ec6b728.jpg");

        mImageViewsList = new ArrayList<ImageView>();
        mDotViewsList = new ArrayList<View>();
    }

    private void initUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_picsilde, this, true);
        mDotGroup = (LinearLayout) findViewById(R.id.dot_group);
//        BitmapUtils bitmapUtils = new BitmapUtils(context);
//
        for (int i = 0; i < mWebImagesIds.size(); i++) {
            ImageView view = new ImageView(context);
            Glide.with(context)
                    .load(mWebImagesIds.get(i))
                    .into(view);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageViewsList.add(view);

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
                    Toast.makeText(mContext,position+"",Toast.LENGTH_LONG).show();
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
                mHanlder.obtainMessage().sendToTarget();
            }
        }
    }

    private void destoryBitmaps() {
        for (int i = 0; i < IMAGE_COUNT; i++) {
            ImageView imageView = mImageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                //解除drawable对view的引用
                drawable.setCallback(null);
            }
        }
    }
}

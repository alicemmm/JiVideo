package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.fragment.MainFragment;
import xvideo.ji.com.jivideo.fragment.MoreFragment;
import xvideo.ji.com.jivideo.fragment.SoftFragment;
import xvideo.ji.com.jivideo.fragment.VideoFragment;
import xvideo.ji.com.jivideo.service.CoreService;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int TIME_GOOGLE_AD_SHOW = 10;

    @Bind(R.id.custom_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.custom_drawerlayout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.leftmenu_lv)
    ListView mLeftMenuLv;

    @Bind(R.id.main_bottom1_ll)
    LinearLayout mMainBottom1Ll;

    @Bind(R.id.main_bottom2_ll)
    LinearLayout mMainBottom2Ll;

    @Bind(R.id.main_bottom3_ll)
    LinearLayout mMainBottom3Ll;

    @Bind(R.id.main_bottom4_ll)
    LinearLayout mMainBottom4Ll;

    private LinearLayout[] mMainBottomLls;
    private Fragment[] mFragments;

    private ActionBarDrawerToggle mDrawerToggle;
    private VideoFragment mVideoFragment;
    private MainFragment mMainFragment;
    private SoftFragment mSoftFragment;
    private MoreFragment mMoreFragment;
    private String[] strs;
    private ArrayAdapter mArrayAdapter;
    private Context mContext;
    private int btnIndex;
    private int currentBtnIndex = 0;

    private InterstitialAd mInterstitialAd;

    private ScheduledExecutorService mScheduledExecutorService;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showAd();
        }
    };

    private class TimeShowAd implements Runnable {
        @Override
        public void run() {
            mHandler.obtainMessage().sendToTarget();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        ButterKnife.bind(this);

        initToolbar();

        initMenu();

        init();

        initAd();

        startService(new Intent(mContext, CoreService.class));

//        startShowAd();
    }

    private void initAd() {
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(Consts.AD_GOOGLE_TABLE_ID);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
                Toast.makeText(mContext, "ad close", Toast.LENGTH_SHORT).show();
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

    public void startShowAd() {
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new TimeShowAd(), 1, TIME_GOOGLE_AD_SHOW, TimeUnit.SECONDS);
    }

    public void stopShowAd() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdown();
        }
    }

    private void initMenu() {
        strs = getResources().getStringArray(R.array.titles_list);
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, strs);
        mLeftMenuLv.setAdapter(mArrayAdapter);

        //TODO need to add menu
        mLeftMenuLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        startActivity(new Intent(mContext, PointsRecordsActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(mContext, ExpenseRecordActivity.class));
                        break;
                    case 2:
                        checkUpdate();
                        break;
                    case 3:
                        startActivity(new Intent(mContext, AboutActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initToolbar() {
        mToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title_text));
        mToolbar.setTitle(getString(R.string.app_name));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //创建返回键，实现打开关闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void init() {
        mVideoFragment = new VideoFragment();
        mMainFragment = new MainFragment();
        mSoftFragment = new SoftFragment();
        mMoreFragment = new MoreFragment();

        mFragments = new Fragment[]{mMainFragment, mVideoFragment, mSoftFragment, mMoreFragment};

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mVideoFragment)
                .add(R.id.fragment_container, mMoreFragment)
                .add(R.id.fragment_container, mMainFragment)
                .add(R.id.fragment_container, mSoftFragment)
                .hide(mVideoFragment)
                .hide(mSoftFragment)
                .hide(mMoreFragment)
                .commit();

        mMainBottomLls = new LinearLayout[4];
        mMainBottomLls[0] = mMainBottom1Ll;
        mMainBottomLls[1] = mMainBottom2Ll;
        mMainBottomLls[2] = mMainBottom3Ll;
        mMainBottomLls[3] = mMainBottom4Ll;

        mMainBottomLls[0].setSelected(true);
        mMainBottomLls[0].setOnClickListener(this);
        mMainBottomLls[1].setOnClickListener(this);
        mMainBottomLls[2].setOnClickListener(this);
        mMainBottomLls[3].setOnClickListener(this);
    }

    private void checkUpdate() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopShowAd();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_bottom1_ll:
                btnIndex = 0;
                break;
            case R.id.main_bottom2_ll:
                btnIndex = 1;
                break;
            case R.id.main_bottom3_ll:
                btnIndex = 2;
                break;
            case R.id.main_bottom4_ll:
                btnIndex = 3;
                showAd();
                break;
            default:
                break;
        }

        if (currentBtnIndex != btnIndex) {
            android.support.v4.app.FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentBtnIndex]);

            if (!mFragments[btnIndex].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[btnIndex]);
            }
            trx.show(mFragments[btnIndex]);
            trx.commitAllowingStateLoss();
        }

        mMainBottomLls[currentBtnIndex].setSelected(false);
        mMainBottomLls[btnIndex].setSelected(true);
        currentBtnIndex = btnIndex;
    }
}

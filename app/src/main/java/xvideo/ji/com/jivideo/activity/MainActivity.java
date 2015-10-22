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
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.data.BaseInfoData;
import xvideo.ji.com.jivideo.data.PointListData;
import xvideo.ji.com.jivideo.data.ScoreDataInfo;
import xvideo.ji.com.jivideo.fragment.MainFragment;
import xvideo.ji.com.jivideo.fragment.MoreFragment;
import xvideo.ji.com.jivideo.fragment.SoftFragment;
import xvideo.ji.com.jivideo.fragment.VideoFragment;
import xvideo.ji.com.jivideo.manager.ClientInfoManager;
import xvideo.ji.com.jivideo.manager.PointOperateApi;
import xvideo.ji.com.jivideo.service.CoreService;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int HANDLE_FAILURE = 0;
    private static final int HANDLE_SUCCESS = 1;
    private static final int HANDLE_POINT_FAILURE = 2;
    private static final int HANDLE_POINT_SUCCESS = 3;

    private static final int HANDLER_SCORE_RESULT = 4;

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
                case HANDLE_POINT_FAILURE:
                    doHandlerPointFailure(msg.obj);
                    break;
                case HANDLE_POINT_SUCCESS:
                    doHandlerPointSuccess(msg.obj);
                    break;
                case HANDLER_SCORE_RESULT:
                    doHandlerScoreResult(msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void doHandlerScoreResult(Object obj) {
        JiLog.error(TAG,"handler result");

        if (obj == null) {
            return;
        }

        ScoreDataInfo dataInfo = (ScoreDataInfo) obj;
        boolean isSuccess = dataInfo.isUploadSuccess();
        if (isSuccess) {
            Toast.makeText(mContext, R.string.get5points, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mContext, "get point failure", Toast.LENGTH_SHORT).show();
        }
    }

    private void doHandlerPointFailure(Object obj) {
        if (obj == null) {
            return;
        }
        Toast.makeText(mContext, obj.toString(), Toast.LENGTH_LONG).show();
    }

    private void doHandlerPointSuccess(Object obj) {
        if (obj == null) {
            return;
        }

        int myPoint = (int) obj;

        BaseInfoData.setMyPoint(myPoint);
    }

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

        String userId = (String) obj;

        if (!TextUtils.isEmpty(userId) && TextUtils.isEmpty(BaseInfoData.getUserId())) {
            BaseInfoData.setUserId(userId);

            if (BaseInfoData.getMyPoint() == -1) {
                reqGetMyPoint();
            }
        }
    }

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

    private ClientInfoManager mManager;

    private PointOperateApi mPointOperateApi;
    private ScoreDataInfo mScoreDataInfo;


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

        if (TextUtils.isEmpty(BaseInfoData.getUserId())) {
            reqGetUserId();
        } else {
            reqGetMyPoint();
        }

    }

    private void initAd() {
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(Consts.AD_GOOGLE_TABLE_ID);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                JiLog.error(TAG,"open ad");
                reqModefyScore();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(BaseInfoData.getDevId())
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void showAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
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
                mToolbar.setTitle(getString(R.string.my_point) + BaseInfoData.getMyPoint());
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mToolbar.setTitle(getString(R.string.app_name));
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

    private void reqGetUserId() {
        if (!Utils.isNetworkConnected(mContext)) {
            return;
        }

        if (mManager == null) {
            mManager = new ClientInfoManager(mContext, new ClientInfoManager.OnClientInfoListener() {
                @Override
                public void onFailure(String errMsg) {
                    Message msg = new Message();
                    msg.obj = errMsg;
                    msg.what = HANDLE_FAILURE;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onSuccess(String userId) {
                    Message msg = new Message();
                    msg.obj = userId;
                    msg.what = HANDLE_SUCCESS;
                    mHandler.sendMessage(msg);
                }
            });
        }

        mManager.req();
    }

    private void reqGetMyPoint() {
        if (!Utils.isNetworkConnected(mContext)) {
            return;
        }

        mScoreDataInfo = new ScoreDataInfo();
        mScoreDataInfo.setUserId(BaseInfoData.getUserId());
        mScoreDataInfo.setOperate(PointOperateApi.OPERATE_GET_POINT);

        if (mPointOperateApi == null) {
            mPointOperateApi = new PointOperateApi(mContext, mScoreDataInfo, new PointOperateApi.onResponseListener() {
                @Override
                public void onFailure(String errMsg) {
                    Message msg = new Message();
                    msg.obj = errMsg;
                    msg.what = HANDLE_POINT_FAILURE;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onSuccess(ArrayList<PointListData> datas, int totalPoints) {
                    Message msg = new Message();
                    msg.obj = totalPoints;
                    msg.what = HANDLE_POINT_SUCCESS;
                    mHandler.sendMessage(msg);
                }
            });
        }

        mPointOperateApi.req();
    }

    private void reqModefyScore() {
        ScoreDataInfo dataInfo = new ScoreDataInfo();
        dataInfo.setOperate(PointOperateApi.OPERATE_MODEFY_POINT);
        dataInfo.setUserId(BaseInfoData.getUserId());
        dataInfo.setOpPoint(5);
        PointOperateApi.getInstance().req(dataInfo).setModefyPointListener(
                new PointOperateApi.onModefyPointListener() {
                    @Override
                    public void result(ScoreDataInfo dataInfo) {
                        JiLog.error(TAG,"result");
                        Message msg = new Message();
                        msg.obj = dataInfo;
                        msg.what = HANDLER_SCORE_RESULT;
                        mHandler.sendMessage(msg);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.cancel();
        }

        if (mPointOperateApi != null) {
            mPointOperateApi.cancel();
        }
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

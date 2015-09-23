package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.fragment.ListFragment;
import xvideo.ji.com.jivideo.fragment.MainFragment;
import xvideo.ji.com.jivideo.fragment.VideoFragment;

public class MainActivity extends ActionBarActivity {
    @Bind(R.id.custom_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.custom_drawerlayout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.leftmenu_lv)
    ListView mLeftMenuLv;

    private ActionBarDrawerToggle mDrawerToggle;
    private VideoFragment mVideoFragment;
    private MainFragment mMainFragment;
    private ListFragment mListFragment;
    private String[] strs = {"1", "2", "3", "4"};
    private ArrayAdapter mArrayAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        ButterKnife.bind(this);

        initToolbar();

        initMenu();

        init();
    }

    private void initMenu() {
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, strs);
        mLeftMenuLv.setAdapter(mArrayAdapter);
    }

    private void initToolbar() {
        mToolbar.setTitle("JiVideo");

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
        mListFragment = new ListFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mVideoFragment)
                .add(R.id.fragment_container, mListFragment)
                .add(R.id.fragment_container, mMainFragment)
                .hide(mMainFragment)
                .hide(mListFragment)
                .commit();

        //TODO need to add menu
        mLeftMenuLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 3) {
                    startActivity(new Intent(mContext, AboutActivity.class));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

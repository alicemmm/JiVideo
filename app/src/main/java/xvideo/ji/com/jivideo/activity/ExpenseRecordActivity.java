package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import xvideo.ji.com.jivideo.R;

public class ExpenseRecordActivity extends ActionBarActivity {
    private static final String TAG = ExpenseRecordActivity.class.getSimpleName();

    private Context mContext;

    @Bind(R.id.custom_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_expense_record);

        mContext = this;
        ButterKnife.bind(this);

        initToolBar();
    }

    private void initToolBar(){
        mToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title_text));
        mToolbar.setTitle("Points Records");

        setSupportActionBar(mToolbar);
    }
}

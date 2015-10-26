package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.BaseInfoData;
import xvideo.ji.com.jivideo.data.PointListData;
import xvideo.ji.com.jivideo.data.ScoreDataInfo;
import xvideo.ji.com.jivideo.manager.PointOperateApi;

public class ExpenseRecordActivity extends ActionBarActivity {
    private static final String TAG = ExpenseRecordActivity.class.getSimpleName();

    private static final int HANDLE_FAILURE = 0;
    private static final int HANDLE_SUCCESS = 1;

    private Context mContext;

    private PointOperateApi mManager;

    private ArrayList<PointListData> mDatas;

    private ScoreDataInfo mScoreDataInfo;

    @Bind(R.id.custom_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.expense_record_lv)
    ListView mListView;

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

        mDatas = (ArrayList<PointListData>) obj;

        mListView.setAdapter(new PointExpenseAdapter(mContext, mDatas));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_expense_record);

        mContext = this;
        ButterKnife.bind(this);

        initToolBar();

        init();

        asyncPointRecordReq();
    }

    private void initToolBar() {
        mToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title_text));
        mToolbar.setTitle(getResources().getString(R.string.point_expense));

        setSupportActionBar(mToolbar);
    }

    private void init() {
        mScoreDataInfo = new ScoreDataInfo();
        mScoreDataInfo.setOperate(PointOperateApi.OPERATE_RECORD_POINT);
        mScoreDataInfo.setUserId(BaseInfoData.getUserId());
        mScoreDataInfo.setPointType(PointOperateApi.POINT_TYPE_EXPENSE);
    }

    private void asyncPointRecordReq() {
        if (mManager == null) {
            mManager = new PointOperateApi(mContext, mScoreDataInfo, new PointOperateApi.onResponseListener() {
                @Override
                public void onFailure(String errMsg) {
                    Message msg = new Message();
                    msg.what = HANDLE_FAILURE;
                    msg.obj = errMsg;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onSuccess(ArrayList<PointListData> datas, int totalPoints,String buyVideoIds) {
                    Message msg = new Message();
                    msg.what = HANDLE_SUCCESS;
                    msg.obj = datas;
                    mHandler.sendMessage(msg);
                }
            });
        }

        mManager.req();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.cancel();
        }
    }

    private class PointExpenseAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<PointListData> datas;

        private class ViewHolder {
            TextView titleTv;
            TextView pointTv;
            TextView timeTv;
        }

        public PointExpenseAdapter(Context context, ArrayList<PointListData> datas) {
            this.context = context;
            this.datas = datas;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int i) {
            return datas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return datas.get(i).hashCode();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.activity_point_record_item, null);
                holder.titleTv = (TextView) view.findViewById(R.id.record_title_item_tv);
                holder.pointTv = (TextView) view.findViewById(R.id.record_point_item_tv);
                holder.timeTv = (TextView) view.findViewById(R.id.record_time_item_tv);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.titleTv.setText(datas.get(i).getTitle());
            holder.pointTv.setText("Point" + datas.get(i).getPoint());
            holder.timeTv.setText(datas.get(i).getTime());

            return view;
        }
    }
}

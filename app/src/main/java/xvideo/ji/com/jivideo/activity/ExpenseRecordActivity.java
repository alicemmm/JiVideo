package xvideo.ji.com.jivideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.PointListData;

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

    private void initToolBar() {
        mToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title_text));
        mToolbar.setTitle("Points Records");

        setSupportActionBar(mToolbar);
    }

    private void init() {

    }

    private class PointRecordAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<PointListData> datas;

        private class ViewHolder {
            TextView titleTv;
            TextView pointTv;
            TextView timeTv;
        }

        public PointRecordAdapter(Context context, ArrayList<PointListData> datas) {
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
            holder.pointTv.setText("Point-" + datas.get(i).getPoint());
            holder.timeTv.setText(datas.get(i).getTime());

            return view;
        }
    }
}

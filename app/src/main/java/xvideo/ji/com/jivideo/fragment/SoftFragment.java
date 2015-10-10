package xvideo.ji.com.jivideo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.SoftData;

public class SoftFragment extends Fragment {
    private static final String TAG = SoftFragment.class.getSimpleName();

    private Context mContext;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soft, container, false);
        mContext = getActivity();
        mListView = (ListView) view.findViewById(R.id.soft_fragment_lv);
        init();
        return view;
    }

    private void init() {
//        mListView.setAdapter(new SoftListAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

    }


    private class SoftListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context context;
        private ArrayList<SoftData> datas;

        public SoftListAdapter(Context context, ArrayList<SoftData> datas) {
            this.context = context;
            this.datas = datas;
            inflater = LayoutInflater.from(mContext);
        }

        private class ViewHolder {
            ImageView iconIv;
            TextView titleTv;
            TextView introduceTv;
            TextView rewardTv;
            ImageView downloadIv;
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
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.fragment_soft_item, null);
                viewHolder.iconIv = (ImageView) view.findViewById(R.id.soft_item_icon_iv);
                viewHolder.titleTv = (TextView) view.findViewById(R.id.soft_item_title_tv);
                viewHolder.introduceTv = (TextView) view.findViewById(R.id.soft_item_introduce_tv);
                viewHolder.rewardTv = (TextView) view.findViewById(R.id.soft_item_reward_tv);
                viewHolder.downloadIv = (ImageView) view.findViewById(R.id.soft_item_download_iv);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.titleTv.setText(datas.get(i).getTitle());
            viewHolder.introduceTv.setText(datas.get(i).getIntroduce());
            viewHolder.rewardTv.setText("Credits" + datas.get(i).getPoint() + "install and open");
            Glide.with(context).load(datas.get(i).getIcon()).into(viewHolder.iconIv);

            return view;
        }
    }

}

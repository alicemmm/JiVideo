package xvideo.ji.com.jivideo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.activity.VideoDetailActivity;
import xvideo.ji.com.jivideo.data.HotVideoData;
import xvideo.ji.com.jivideo.manager.HotVideoManager;

public class VideoFragment extends Fragment {
    private final static String TAG = VideoFragment.class.getSimpleName();

    private static final int HANDLE_FAILURE = 0;
    private static final int HANDLE_SUCCESS = 1;

    private GridView mGridView;
    private Context mContext;
    private HotVideoData mHotVideoData;

    private HotVideoManager mManager;

    private MyAdapter mMyAdapter;

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

        mHotVideoData = (HotVideoData) obj;
        mMyAdapter = new MyAdapter(mContext, mHotVideoData);
        mGridView.setAdapter(mMyAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mContext = getActivity();

        mGridView = (GridView) view.findViewById(R.id.video_gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, VideoDetailActivity.class);
                intent.putExtra("data", mHotVideoData.getHots().get(i));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        asyncHotVideoReq();
    }

    private void asyncHotVideoReq() {
        if (mManager == null) {
            mManager = new HotVideoManager(mContext, new HotVideoManager.onResponseListener() {
                @Override
                public void onFailure(String errMsg) {
                    Message message = new Message();
                    message.what = HANDLE_FAILURE;
                    message.obj = errMsg;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onSuccess(HotVideoData hotVideoData) {
                    Message message = new Message();
                    message.what = HANDLE_SUCCESS;
                    message.obj = hotVideoData;
                    mHandler.sendMessage(message);
                }
            });
        }

        mManager.req();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.cancel();
        }
    }

    private class MyAdapter extends BaseAdapter {
        HotVideoData hotVideoData;
        Context context;
        LayoutInflater inflater;

        public MyAdapter(Context context, HotVideoData hotVideoData) {
            this.context = context;
            this.hotVideoData = hotVideoData;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return hotVideoData.getHots().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        class ViewHolder {
            ImageView itemPic;
            TextView itemInfo;
            ImageView itemDp;
            TextView itemTitle;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();

            if (view == null) {
                view = inflater.inflate(R.layout.view_customitem, null);
                holder.itemPic = (ImageView) view.findViewById(R.id.item_pic_iv);
                holder.itemInfo = (TextView) view.findViewById(R.id.item_info_tv);
                holder.itemDp = (ImageView) view.findViewById(R.id.item_dp_iv);
                holder.itemTitle = (TextView) view.findViewById(R.id.item_title_tv);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.itemDp.setVisibility(View.GONE);
            holder.itemInfo.setText(hotVideoData.getHots().get(i).getWatch() + getString(R.string.watching));
            holder.itemTitle.setText(hotVideoData.getHots().get(i).getTitle());
            Glide.with(mContext)
                    .load(hotVideoData.getHots().get(i).getSmall_icon())
                    .into(holder.itemPic);

            return view;
        }

    }
}

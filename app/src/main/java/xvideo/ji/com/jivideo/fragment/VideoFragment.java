package xvideo.ji.com.jivideo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.HotVideoData;
import xvideo.ji.com.jivideo.manager.HotVideoManager;

/**
 * Created by Domon on 15-9-18.
 */
public class VideoFragment extends Fragment {
    private final static String TAG = VideoFragment.class.getSimpleName();

    private static final int HANDLE_FAILURE = 0;
    private static final int HANDLE_SUCCESS = 1;

    private GridView mGridView;
    private ArrayList<HashMap<String, Object>> mArrayList;
    private Context mContext;
    private HotVideoData mHotVideData;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_FAILURE:
                    doHandlerFailure();
                    break;
                case HANDLE_SUCCESS:
                    doHandlerSuccess(msg.obj);
                    break;
                default:
                    break;
            }
        }
    };


    private void doHandlerFailure() {

    }

    private void doHandlerSuccess(Object obj) {
        if (obj == null) {
            return;
        }

        mHotVideData = (HotVideoData) obj;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mContext = getActivity();

        HotVideoManager manager = new HotVideoManager(mContext, new HotVideoManager.onResponseListener() {
            @Override
            public void onFailure(String errMsg) {
                Message message = new Message();
                message.what = HANDLE_FAILURE;
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

        manager.req();

        mGridView = (GridView) view.findViewById(R.id.video_gridview);

        mArrayList = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < 20; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.mipmap.ic_launcher);
            map.put("ItemText", "Info" + i);
            mArrayList.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), mArrayList, R.layout.item_video,
                new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.griditem_pic_iv, R.id.griditem_info_tv});

        mGridView.setAdapter(simpleAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "onClick" + i, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}

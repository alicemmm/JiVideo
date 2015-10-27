package xvideo.ji.com.jivideo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.BaseInfoData;
import xvideo.ji.com.jivideo.data.ScoreDataInfo;
import xvideo.ji.com.jivideo.data.SoftData;
import xvideo.ji.com.jivideo.download.DownloadManager;
import xvideo.ji.com.jivideo.download.DownloadService;
import xvideo.ji.com.jivideo.manager.PointOperateApi;
import xvideo.ji.com.jivideo.manager.SoftListManager;
import xvideo.ji.com.jivideo.request.TaskMonitorApi;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class SoftFragment extends Fragment {
    private static final String TAG = SoftFragment.class.getSimpleName();

    private static final int HANDLE_FAILURE = 0;
    private static final int HANDLE_SUCCESS = 1;

    private static final int DOWNLOAD_STATE_PREPARE = 0;
    private static final int DOWNLOAD_STATE_START = 1;
    private static final int DOWNLOAD_STATE_LOADING = 2;
    private static final int DOWNLOAD_STATE_COMPLETE = 3;
    private static final int DOWNLOAD_STATE_FAILURE = 4;

    @Bind(R.id.soft_fragment_lv)
    ListView mListView;

    @OnItemClick(R.id.soft_fragment_lv)
    void onItemClickListView(int position) {
        mTempIndex = position;
        SoftData tempDownLoadData = mDatas.get(position);
        String localFile = Utils.getApkPath() + File.separator + tempDownLoadData.getTitle()
                + "_" + tempDownLoadData.getId() + ".apk";

        int ret = Utils.launcherApp(mContext, tempDownLoadData.getPkgName());
        if (ret != 0) {
            //launch failed
            if (Utils.isFileExist(localFile) && Utils.checkAppPackage(mContext, localFile)) {
                Utils.installApk(mContext, localFile);
            } else {
                switch (tempDownLoadData.getDownloadState()) {
                    case DOWNLOAD_STATE_PREPARE:
                        startDownload(tempDownLoadData);
                        break;
                    case DOWNLOAD_STATE_START:
                        Toast.makeText(mContext, "start download!", Toast.LENGTH_SHORT).show();
                        break;
                    case DOWNLOAD_STATE_LOADING:
                        Toast.makeText(mContext, "downloading...", Toast.LENGTH_SHORT).show();
                        break;
                    case DOWNLOAD_STATE_COMPLETE:
                        startDownload(tempDownLoadData);
                        break;
                    case DOWNLOAD_STATE_FAILURE:
                        startDownload(tempDownLoadData);
                        break;
                    default:
                        break;
                }
            }
        } else {
            //monitor
            ScoreDataInfo dataInfo = new ScoreDataInfo();
            dataInfo.setUserId(BaseInfoData.getUserId());
            dataInfo.setExpenseScoreId(tempDownLoadData.getId());
            dataInfo.setTitle(tempDownLoadData.getTitle());
            dataInfo.setOpPoint(tempDownLoadData.getPoint());
            dataInfo.setOperate(PointOperateApi.OPERATE_MODEFY_POINT);

            TaskMonitorApi.getInstance().req(tempDownLoadData.getPkgName(), 20, dataInfo);
        }
    }

    private Context mContext;

    private ArrayList<SoftData> mDatas;
    private int mTempIndex;

    private String mLocalFile;

    private SoftListManager mSoftListManager;
    private DownloadManager mDownloadManager;

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

        mDatas = (ArrayList<SoftData>) obj;

        mListView.setAdapter(new SoftListAdapter(mContext, mDatas));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soft, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        init();

        asyncSoftListReq();

        return view;
    }

    private void init() {
        mDownloadManager = DownloadService.getDownloadManager(mContext);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void asyncSoftListReq() {
        if (!Utils.isNetworkConnected(mContext)) {
            return;
        }

        if (TextUtils.isEmpty(BaseInfoData.getUserId())) {
            return;
        }

        if (mSoftListManager == null) {
            mSoftListManager = new SoftListManager(mContext, new SoftListManager.onResponseListener() {
                @Override
                public void onFailure(String errMsg) {
                    Message msg = new Message();
                    msg.what = HANDLE_FAILURE;
                    msg.obj = errMsg;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onSuccess(ArrayList<SoftData> softDatas) {
                    Message msg = new Message();
                    msg.what = HANDLE_SUCCESS;
                    msg.obj = softDatas;
                    mHandler.sendMessage(msg);
                }
            });
        }

        mSoftListManager.req();
    }

    private void startDownload(SoftData data) {
        if (!Utils.isExternalStorageWriteable()) {
            Toast.makeText(mContext, "No memory card!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Utils.isNetworkConnected(mContext)) {
            Toast.makeText(mContext, "Network is not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        mLocalFile = Utils.getApkPath() + File.separator + data.getTitle() + "_" + data.getId() + ".apk";

        if (Utils.isFileExist(mLocalFile)) {
            Utils.deleteFile(mLocalFile);
        }

        try {
            mDownloadManager.addNewDownload(data.getDownloadUrl(), data.getTitle(), mLocalFile, data.getId(),
                    true, false, true, new DownloadRequestCallBack());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSoftListManager != null) {
            mSoftListManager.cancel();
        }
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
            viewHolder.rewardTv.setText("Credits " + datas.get(i).getPoint() + " install and open");
            Glide.with(context).load(datas.get(i).getIcon()).into(viewHolder.iconIv);

            return view;
        }
    }

    private class DownloadRequestCallBack extends RequestCallBack<File> {

        @SuppressWarnings("unchecked")
        private void refreshListItem() {
        }

        @Override
        public void onStart() {
            mDatas.get(mTempIndex).setDownloadState(DOWNLOAD_STATE_START);
            JiLog.error(TAG, "OnStart");
            Toast.makeText(mContext, "start download", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            mDatas.get(mTempIndex).setDownloadState(DOWNLOAD_STATE_LOADING);
            JiLog.error(TAG, "total=current=" + total + ":" + current);
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            mDatas.get(mTempIndex).setDownloadState(DOWNLOAD_STATE_COMPLETE);
            Utils.installApk(mContext, mLocalFile);
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            mDatas.get(mTempIndex).setDownloadState(DOWNLOAD_STATE_FAILURE);
            JiLog.error(TAG, "receive ACTION_DOWNLOAD_ERR. errCode=" + error);
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled() {
            mDatas.get(mTempIndex).setDownloadState(DOWNLOAD_STATE_FAILURE);
            JiLog.error(TAG, "OnCancelled");
        }
    }

}

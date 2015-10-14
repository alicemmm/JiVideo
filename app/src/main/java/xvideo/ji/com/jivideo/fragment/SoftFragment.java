package xvideo.ji.com.jivideo.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import xvideo.ji.com.jivideo.R;
import xvideo.ji.com.jivideo.data.SoftData;
import xvideo.ji.com.jivideo.download.DownloadManager;
import xvideo.ji.com.jivideo.download.DownloadService;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class SoftFragment extends Fragment {
    private static final String TAG = SoftFragment.class.getSimpleName();

    private Context mContext;
    private ListView mListView;

    private ArrayList<SoftData> datas;

    private String mLocalFile;

    DownloadManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soft, container, false);
        mContext = getActivity();
        mListView = (ListView) view.findViewById(R.id.soft_fragment_lv);
        init();
        return view;
    }

    private void init() {
        datas = new ArrayList<>();
        manager = DownloadService.getDownloadManager(mContext);

        for (int i = 0; i < 2; ++i) {
            SoftData data = new SoftData();
            data.setTitle("title" + i);
            data.setIcon("http://source.jisuoping.com/image/20150924170007721.png");
            data.setIntroduce("this is introduce");
            data.setPoint(2);
            data.setDownloadUrl("http://jsp.dx1200.com/apk/2015/new-taohuayuan-12079.apk");
            datas.add(data);
        }


        mListView.setAdapter(new SoftListAdapter(mContext, datas));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialogToDownloadApk(datas.get(i));
            }
        });

    }


    private void dialogToDownloadApk(final SoftData data) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog1, null);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        TextView contentTv = (TextView) view.findViewById(R.id.dialog_desc_tv);
        ImageView closeIv = (ImageView) view.findViewById(R.id.dialog_close_iv);
        Button cancelBtn = (Button) view.findViewById(R.id.dialog_positive_btn);
        Button okBtn = (Button) view.findViewById(R.id.dialog_update_negative_btn);
        titleTv.setText("Download?");
        contentTv.setText("Download the installation and open the application to get " + data.getPoint() + " points, download it?");
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getActivity().isFinishing()) {
                    dialog.dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getActivity().isFinishing()) {
                    dialog.dismiss();
                }
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload(data);
                if (!getActivity().isFinishing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    private void startDownload(SoftData data) {
        mLocalFile = Utils.getApkPath() + File.separator + data.getTitle() + ".apk";

        if (!Utils.isExternalStorageWriteable()) {
            Toast.makeText(mContext, "No memory card!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Utils.isNetworkConnected(mContext)) {
            Toast.makeText(mContext, "Network is not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utils.isFileExist(mLocalFile)) {
            Utils.deleteFile(mLocalFile);
        }

        try {
            manager.addNewDownload(data.getDownloadUrl(), data.getTitle(), mLocalFile, 1, true, false, true, new DownloadRequestCallBack());
        } catch (Exception e) {
            e.printStackTrace();
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
            JiLog.error(TAG, "OnStart");
            Toast.makeText(mContext, "start download", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            JiLog.error(TAG, "total=current=" + total + ":" + current);
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            Utils.installApk(mContext, mLocalFile);
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            JiLog.error(TAG, "receive ACTION_DOWNLOAD_ERR. errCode=" + error);
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled() {
            JiLog.error(TAG, "OnCancelled");
        }
    }

}

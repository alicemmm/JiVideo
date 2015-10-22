package xvideo.ji.com.jivideo.request;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.Map;

import xvideo.ji.com.jivideo.MyApplication;
import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.data.BaseInfoData;
import xvideo.ji.com.jivideo.network.VolleyRequestManager;
import xvideo.ji.com.jivideo.service.CoreService;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class AliveApi {
    public static final String TAG = AliveApi.class.getSimpleName();

    private static AliveApi mInstance = null;
    private static Context mContext;
    private StringRequest mRequest;

    private static final int REQ_ASYNC = 0;
    private static final int REQ_SYNC = 1;

    private AliveApi() {
        mContext = MyApplication.getAppContext();
    }

    public synchronized static AliveApi getInstance() {
        if (mInstance == null) {
            mInstance = new AliveApi();
        }
        return mInstance;
    }

    private void analyzeAliveRsp(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        try {
            String rsp = URLDecoder.decode(s, "utf-8");

            if (TextUtils.isEmpty(rsp)) {
                return;
            }

            JiLog.error(TAG, "rsp=" + rsp);

            JSONObject object = new JSONObject(rsp);

            int state = object.getInt("state");

            if (state == 0) {
                Intent intent = new Intent(mContext, CoreService.class);
                intent.setAction(CoreService.ACTION_SHOW_AD);
                mContext.startService(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void req() {
        if (!Utils.isNetworkConnected(mContext)) {
            return;
        }

        RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        mRequest = new StringRequest(Request.Method.POST, Consts.URL_CLIENT_ACTIVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String s) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                analyzeAliveRsp(s);
                            }
                        }).start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return BaseInfoData.getAliveInfo();
            }
        };

        requestQueue.add(mRequest);
    }
}

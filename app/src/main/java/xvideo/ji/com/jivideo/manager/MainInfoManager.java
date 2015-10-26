package xvideo.ji.com.jivideo.manager;

import android.content.Context;
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

import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.data.BaseInfoData;
import xvideo.ji.com.jivideo.network.VolleyRequestManager;
import xvideo.ji.com.jivideo.utils.JiLog;
import xvideo.ji.com.jivideo.utils.Utils;

public class MainInfoManager {
    private static final String TAG = MainInfoManager.class.getSimpleName();

    public interface onResponseListener {
        void onFailure(String errMsg);

        void onSuccess(String userId);
    }

    private StringRequest mRequest;

    private Context mContext;

    private onResponseListener mListener;

    public MainInfoManager(Context mContext, onResponseListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public MainInfoManager(Context mContext) {
        this.mContext = mContext;
    }

    public void setListener(onResponseListener mListener) {
        this.mListener = mListener;
    }

    public void req() {
        if (!Utils.isNetworkConnected(mContext)) {
            return;
        }

        final RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        mRequest = new StringRequest(Request.Method.POST, Consts.URL_CLIENT_ACTIVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                analyseRsp(response);
                            }
                        }).start();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                JiLog.printExceptionStackTrace(error);
                doFailure(null);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return BaseInfoData.getBaseInfo();
            }
        };

        requestQueue.add(mRequest);
    }

    private void analyseRsp(String param) {
        if (TextUtils.isEmpty(param)) {
            return;
        }

        try {
            String rsp = URLDecoder.decode(param, "utf-8");

            if (TextUtils.isEmpty(rsp)) {
                return;
            }

            JiLog.error(TAG, "rsp=" + rsp);

            JSONObject object = new JSONObject(rsp);

            int state = object.getInt("state");

            String userId = object.getString("user_id");

            JiLog.error(TAG, "userid=" + userId);

            if (state == 0) {
                doSuccess(userId);
            } else {
                doFailure(state + "");
            }

        } catch (Exception e) {
            doFailure(e.toString());
            e.printStackTrace();
        }
    }

    private void doFailure(String errMsg) {
        if (mListener != null) {
            mListener.onFailure(errMsg);
        }
    }

    private void doSuccess(String userId) {
        if (mListener != null) {
            mListener.onSuccess(userId);
        }
    }

    public void cancel() {
        if (mRequest != null) {
            mRequest.cancel();
        }
    }
}

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

public class ClientInfoManager {
    private static final String TAG = ClientInfoManager.class.getSimpleName();

    private Context mContext;
    private OnClientInfoListener mListener;

    private StringRequest mRequest;

    public interface OnClientInfoListener {
        void onFailure(String errMsg);

        void onSuccess(String userId);
    }


    public ClientInfoManager(Context context) {
        mContext = context;
    }

    public ClientInfoManager(Context context, OnClientInfoListener listener) {
        this(context);
        mListener = listener;
    }


    private void analyseRsp(String param) {
        if (!TextUtils.isEmpty(param)) {
            return;
        }
        try {
            String rsp = URLDecoder.decode(param, "utf-8");
            if (!TextUtils.isEmpty(rsp)) {
                return;
            }

            JSONObject object = new JSONObject(rsp);
            int state = object.getInt("state");
            if (state == 0) {
                String userId = object.getString("user_id");
                doSuccess(userId);
            } else {
                doFailure("error");
            }

        } catch (Exception e) {
            doFailure("error");
            e.printStackTrace();
        }
    }

    public void req() {
        if (!Utils.isNetworkConnected(mContext)) {
            return;
        }

        final RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();
        if (mRequest == null) {
            mRequest = new StringRequest(Request.Method.POST, Consts.URL_REPORT_CLIENT_INFO,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            new Thread() {
                                @Override
                                public void run() {
                                    analyseRsp(response);
                                }
                            }.start();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    JiLog.printExceptionStackTrace(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return BaseInfoData.getBaseInfo();
                }
            };
        }

        requestQueue.add(mRequest);
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

package xvideo.ji.com.jivideo.manager;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.network.VolleyRequestManager;
import xvideo.ji.com.jivideo.utils.JiLog;

public class ModifyPointManager {
    private static final String TAG = ModifyPointManager.class.getSimpleName();

    public interface onResponseListener {
        void onFailure(String errMsg);

        void onSuccess(String result);
    }

    private StringRequest mRequest;

    private Context mContext;
    private int mPoint;

    private onResponseListener mListener;


    public ModifyPointManager(Context mContext, onResponseListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public ModifyPointManager(Context mContext) {
        this.mContext = mContext;
    }

    public void setListener(onResponseListener mListener) {
        this.mListener = mListener;
    }

    public void setReqPoint(int point) {
        mPoint = point;
    }

    public void req() {
        final RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        mRequest = new StringRequest(Request.Method.POST, Consts.GET_APP_LIST,
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
                Map<String, String> map = new HashMap<>();
                map.put("point", mPoint + "");
                return map;
            }
        };

        requestQueue.add(mRequest);
    }

    private void analyseRsp(String param) {
        try {
            String rsp = URLDecoder.decode(param, "utf-8");

            if (TextUtils.isEmpty(rsp)) {
                doFailure("param is empty");
                return;
            }

            JiLog.error(TAG, "rsp=" + rsp);

            JSONObject jsonObject = new JSONObject(rsp);

            String result = "";

            doSuccess(result);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doFailure(String errMsg) {
        if (mListener != null) {
            mListener.onFailure(errMsg);
        }
    }

    private void doSuccess(String result) {
        if (mListener != null) {
            mListener.onSuccess(result);
        }
    }

    public void cancel() {
        if (mRequest != null) {
            mRequest.cancel();
        }
    }

}

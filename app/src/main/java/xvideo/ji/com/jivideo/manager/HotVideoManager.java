package xvideo.ji.com.jivideo.manager;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.network.VolleyRequestManager;
import xvideo.ji.com.jivideo.utils.JiLog;

/**
 * Created by Domon on 15-9-21.
 */
public class HotVideoManager {
    private static final String TAG = HotVideoManager.class.getSimpleName();

    public interface onResponseListener {
        void onFailure(String errMsg);

        void onSuccess();
    }

    private StringRequest mRequest;

    private Context mContext;

    private onResponseListener mListener;

    public HotVideoManager(Context mContext, onResponseListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public HotVideoManager(Context mContext) {
        this.mContext = mContext;
    }

    public void setListener(onResponseListener mListener) {
        this.mListener = mListener;
    }

    public void req() {
        final RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        mRequest = new StringRequest(Request.Method.POST, Consts.GET_HOTVIDEO_URL,
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
        });

        requestQueue.add(mRequest);
    }

    private void analyseRsp(String param) {
        try {
            String rsp = URLDecoder.decode(param, "utf-8");
            JiLog.error(TAG, rsp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void doFailure(String errMsg) {
        if (mListener != null) {
            mListener.onFailure(errMsg);
        }
    }

    private void doSuccess() {
        if (mListener != null) {
            mListener.onSuccess();
        }
    }

    public void cancel() {
        if (mRequest != null) {
            mRequest.cancel();
        }
    }
}

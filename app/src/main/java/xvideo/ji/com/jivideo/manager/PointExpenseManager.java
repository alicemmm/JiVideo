package xvideo.ji.com.jivideo.manager;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.data.PointListData;
import xvideo.ji.com.jivideo.network.VolleyRequestManager;
import xvideo.ji.com.jivideo.utils.JiLog;

public class PointExpenseManager {
    private static final String TAG = PointExpenseManager.class.getSimpleName();

    public interface onResponseListener {
        void onFailure(String errMsg);

        void onSuccess(ArrayList<PointListData> datas);
    }

    private StringRequest mRequest;

    private Context mContext;

    private onResponseListener mListener;

    public PointExpenseManager(Context mContext, onResponseListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public PointExpenseManager(Context mContext) {
        this.mContext = mContext;
    }

    public void setListener(onResponseListener mListener) {
        this.mListener = mListener;
    }

    public void req() {
        final RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        mRequest = new StringRequest(Request.Method.POST, Consts.GET_POINT_EXPENSE,
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

            if (TextUtils.isEmpty(rsp)) {
                doFailure("param is empty");
                return;
            }

            JiLog.error(TAG, "rsp=" + rsp);

            ArrayList<PointListData> datas = null;

            JSONObject jsonObject = new JSONObject(rsp);
            JSONArray jsonArray = jsonObject.getJSONArray("hots");
            int length = jsonArray.length();

            if (length > 0) {
                datas = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    PointListData data = new PointListData();
                    data.setTitle(object.getString("title"));
                    data.setPoint(object.getInt("point"));
                    data.setTime(object.getString("time"));
                    datas.add(data);
                }
            }

            doSuccess(datas);

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

    private void doSuccess(ArrayList<PointListData> datas) {
        if (mListener != null) {
            mListener.onSuccess(datas);
        }
    }

    public void cancel() {
        if (mRequest != null) {
            mRequest.cancel();
        }
    }
}

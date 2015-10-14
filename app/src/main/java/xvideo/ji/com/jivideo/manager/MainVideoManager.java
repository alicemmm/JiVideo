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
import xvideo.ji.com.jivideo.data.HotVideoData;
import xvideo.ji.com.jivideo.network.VolleyRequestManager;
import xvideo.ji.com.jivideo.utils.JiLog;

public class MainVideoManager {
    private static final String TAG = MainVideoManager.class.getSimpleName();

    public interface onResponseListener {
        void onFailure(String errMsg);

        void onSuccess(HotVideoData hotVideoData);
    }

    private StringRequest mRequest;

    private Context mContext;

    private onResponseListener mListener;

    public MainVideoManager(Context mContext, onResponseListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public MainVideoManager(Context mContext) {
        this.mContext = mContext;
    }

    public void setListener(onResponseListener mListener) {
        this.mListener = mListener;
    }

    public void req() {
        final RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        mRequest = new StringRequest(Request.Method.POST, Consts.GET_MAINVIDEO_URL,
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

            HotVideoData hotVideoData = new HotVideoData();
            ArrayList<HotVideoData.HotsEntity> hotsDataList = null;

            JSONObject jsonObject = new JSONObject(rsp);
            JSONArray jsonArray = jsonObject.getJSONArray("hots");
            int length = jsonArray.length();

            if (length > 0) {
                hotsDataList = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    HotVideoData.HotsEntity hotsData = new HotVideoData.HotsEntity();

                    hotsData.setArea(object.optString("area"));
                    hotsData.setBt_url(object.optString("bt_url"));
                    hotsData.setCountry(object.optString("country"));
                    hotsData.setDescription(object.optString("description"));
                    hotsData.setHigh_point(object.optInt("high_point"));
                    hotsData.setId(object.optInt("id"));
                    hotsData.setLow_point(object.optInt("low_point"));
                    hotsData.setMain_icon(object.optString("main_icon"));
                    hotsData.setScore(object.optInt("score"));
                    hotsData.setSmall_icon(object.optString("small_icon"));
                    hotsData.setTitle(object.optString("title"));
                    hotsData.setUrl(object.optString("url"));
                    hotsData.setVideo(object.optString("video"));
                    hotsData.setVideo2(object.optString("video2"));
                    hotsData.setWatch(object.optInt("watch"));
                    hotsData.setIndexType(object.optInt("indextype"));
                    hotsDataList.add(hotsData);
                }
                hotVideoData.setHots(hotsDataList);
            }

            doSuccess(hotVideoData);

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

    private void doSuccess(HotVideoData hotVideoData) {
        if (mListener != null) {
            mListener.onSuccess(hotVideoData);
        }
    }

    public void cancel() {
        if (mRequest != null) {
            mRequest.cancel();
        }
    }
}

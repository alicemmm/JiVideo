package xvideo.ji.com.jivideo.manager;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

import xvideo.ji.com.jivideo.MyApplication;
import xvideo.ji.com.jivideo.config.Consts;
import xvideo.ji.com.jivideo.data.PointListData;
import xvideo.ji.com.jivideo.data.ScoreDataInfo;
import xvideo.ji.com.jivideo.network.VolleyRequestManager;
import xvideo.ji.com.jivideo.utils.JiLog;

public class PointOperateApi {
    private static final String TAG = PointOperateApi.class.getSimpleName();

    public static final int OPERATE_GET_POINT = 0;
    public static final int OPERATE_MODEFY_POINT = 1;
    public static final int OPERATE_RECORD_POINT = 2;

    public static final int POINT_TYPE_EARN = 1;
    public static final int POINT_TYPE_EXPENSE = 2;

    public interface onResponseListener {
        void onFailure(String errMsg);

        void onSuccess(ArrayList<PointListData> datas, int totalPoints);
    }

    public interface onModefyPointListener {
        void result(ScoreDataInfo dataInfo);
    }

    private StringRequest mRequest;

    private static Context mContext;
    private static PointOperateApi mInstance;
    private ScoreDataInfo mScoreDataInfo;

    private onResponseListener mListener;
    private onModefyPointListener mModefyPointListener;

    public PointOperateApi(Context context, ScoreDataInfo dataInfo,
                           onResponseListener mListener) {
        this(context, dataInfo);
        this.mListener = mListener;
    }

    public PointOperateApi(Context context, ScoreDataInfo dataInfo) {
        mContext = context;
        mScoreDataInfo = dataInfo;
    }

    private PointOperateApi() {
        mContext = MyApplication.getAppContext();
    }

    public synchronized static PointOperateApi getInstance() {
        if (mInstance == null) {
            mInstance = new PointOperateApi();
        }

        return mInstance;
    }

    public void setListener(onResponseListener mListener) {
        this.mListener = mListener;
    }

    public void setModefyPointListener(onModefyPointListener listener) {
        this.mModefyPointListener = listener;
    }

    public PointOperateApi req(final ScoreDataInfo dataInfo) {
        final RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        mRequest = new StringRequest(Request.Method.POST, Consts.GET_POINT_OPERATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int res = analyseModefyRsp(response);
                                if (res == 0) {
                                    dataInfo.setIsUploadSuccess(true);
                                } else {
                                    dataInfo.setIsUploadSuccess(false);
                                }

                                doModefyResult(dataInfo);
                            }
                        }).start();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                JiLog.error(TAG, "onErrorResponse" + error);
                dataInfo.setIsUploadSuccess(false);
                doModefyResult(dataInfo);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("op", dataInfo.getOperate() + "");
                map.put("user_id", dataInfo.getUserId() + "");
                map.put("op_points", dataInfo.getOpPoint() + "");
                return map;
            }
        };

        requestQueue.add(mRequest);

        return getInstance();
    }

    private int analyseModefyRsp(String param) {
        int result = -1;
        if (TextUtils.isEmpty(param)) {
            return -1;
        }

        try {
            String rsp = URLDecoder.decode(param, "utf-8");
            JSONObject object = new JSONObject(rsp);
            result = object.getInt("state");
            JiLog.error(TAG, "get score state=" + result);

        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }

        return result;
    }

    public void req() {
        final RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        mRequest = new StringRequest(Request.Method.POST, Consts.GET_POINT_OPERATE,
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
                map.put("op", mScoreDataInfo.getOperate() + "");
                map.put("user_id", mScoreDataInfo.getUserId() + "");
                switch (mScoreDataInfo.getOperate()) {
                    case 0:
                        break;
                    case 1:
                        map.put("op_points", mScoreDataInfo.getOpPoint() + "");
                        map.put("op_title", mScoreDataInfo.getTitle());
                        break;
                    case 2:
                        map.put("point_type", mScoreDataInfo.getPointType() + "");
                        break;
                    default:
                        break;
                }

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
            int state = jsonObject.getInt("state");
            if (state != 0) {
                doFailure(state + "");
            } else {
                int totalPoint = 0;
                ArrayList<PointListData> datas = null;

                if (mScoreDataInfo.getOperate() == OPERATE_GET_POINT) {
                    totalPoint = jsonObject.getInt("total_points");
                }

                if (mScoreDataInfo.getOperate() == OPERATE_RECORD_POINT) {
                    JSONArray jsonArray = jsonObject.getJSONArray("record");
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
                }

                doSuccess(datas, totalPoint);
            }

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

    private void doSuccess(ArrayList<PointListData> datas, int totalPoints) {
        if (mListener != null) {
            mListener.onSuccess(datas, totalPoints);
        }
    }

    private void doModefyResult(ScoreDataInfo dataInfo) {
        if (mModefyPointListener != null) {
            mModefyPointListener.result(dataInfo);
        }
    }

    public void cancel() {
        if (mRequest != null) {
            mRequest.cancel();
        }
    }

}

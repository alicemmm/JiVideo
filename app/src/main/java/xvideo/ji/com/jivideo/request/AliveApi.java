package xvideo.ji.com.jivideo.request;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lidroid.xutils.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

import xvideo.ji.com.jivideo.MyApplication;
import xvideo.ji.com.jivideo.network.VolleyRequestManager;
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

    private String buildParam() {
        return null;
    }

    private void analyzeAliveRsp(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

    }

    public void req() {
        if (!Utils.isNetworkConnected(mContext)) {
            return;
        }

        RequestQueue requestQueue = VolleyRequestManager.getRequestQueue();

        //TODO need api url
        mRequest = new StringRequest(Request.Method.POST, "url",
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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String data1 = buildParam();
                RequestParams params = new RequestParams();

                Map<String, String> map = new HashMap<String, String>();
                map.put("data", data1);

                return map;
            }
        };

        requestQueue.add(mRequest);
    }
}

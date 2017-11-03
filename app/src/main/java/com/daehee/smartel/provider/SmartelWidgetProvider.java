package com.daehee.smartel.provider;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daehee.smartel.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daehee on 2017. 9. 12..
 */

/**
 * AppWidgetProvider: 위젯을 만들 때 반드시 사용되어야 하는 클래스이며 extends 하여 사용한다.
 * 내부적으로 BroadcastReceiver를 상속받고 있다
 */
public class SmartelWidgetProvider extends AppWidgetProvider {

    final String TEXT_PHONE = "01025513286";
    final String TEXT_NAME = "ECA095EB8C80ED9DAC";
    final String TEXT_PW = "wjdeogml1!";
    String LEFT_CALL = "";
    String LEFT_SMS = "";
    String LEFT_DATA = "";
    String TEXT_COOKIE = "";

    /**
     * 위젯이 처음 생성될 때 불려지는 메소드 (같은 위젯을 여러개 부를 때에는 맨 처음 만든 위젯을 부를 때만 호출)
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Log.d("MyLog", "Widget: " + "onEnabled");
    }

    /**
     * 위젯 xml에서 설정한 updatePeriodMillis에 따라 주기적으로 호출되는 메소드이며
     * 초기 위젯 생성시의 initialize 작업도 수행, 보통 handler를 넣어준다
     * <p>
     * ### onUpdate 구현시 주의사항: 다수의 widget에 대한 업데이트 관리를 위해
     * 마지막 파라미터인 appWidgetIds의 개수 만큼 돌면서 위젯의 업데이트를 구현해야 한다
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;
        Log.d("MyLog", "Widget: " + "onUpdate -> N: " + N);

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = buildViews(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            setData(views);
        }


    } // end of onUpdate

    /**
     * onDeleted: 위젯이 홈 화면에서 삭제될 때 호출
     *
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        Log.d("MyLog", "Widget: " + "onDeleted");
    }

    /**
     * 마지막 위젯이 홈 화면에서 삭제될 때 호출, onEnabled의 반대개념
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        Log.d("MyLog", "Widget: " + "onDisabled");
    }

    /**
     * BroadcastReceiver의 그 함수와 동일하며 다른 콜백보다 먼저 불린다.
     * 아래의 manifest에서 지정한 action의 실행이 가능하다. 물론 커스텀 액션도 등록 가능
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d("MyLog", "Widget: " + "onReceive");

        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Log.d("MyLog", "Widget: " + "ACTION_APPWIDGET_UPDATE");
            Bundle extras = intent.getExtras();
            //Bundle 은 Key-Value 쌍으로 이루어진 일종의 해쉬맵 자료구조
            //한 Activity에서 Intent 에 putExtras로 Bundle 데이터를 넘겨주고,
            //다른 Activity에서 getExtras로 데이터를 참조하는 방식.
            /*if (extras != null) {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if ((appWidgetIds != null) && (appWidgetIds.length > 0)) {
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }*/

//            int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, getClass()));
            if ((appWidgetIds != null) && (appWidgetIds.length > 0)) {
                this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
            }
        }//업데이트인 경우
        else if (action.equals("Click1")) {
            Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
        }
    }

    private PendingIntent buildURIIntent(Context context) {
//        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://puzzleleaf.tistory.com"));
        Intent intent = new Intent();
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        return pi;
    }

    private PendingIntent buildRefreshIntent(Context context) {
//        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://puzzleleaf.tistory.com"));
        Intent intent = new Intent();
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    //Click1 이라는 Action을 onReceive로 보낸다.
    private PendingIntent buildToastIntent(Context context) {
        Intent in = new Intent("Click1");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    // RemoteView 를 빌드하고 리턴하는 함수
    private RemoteViews buildViews(Context context, int appWidgetId) {
        Log.d("MyLog", "Widget: " + "buildViews");
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
//        views.setOnClickPendingIntent(R.id.ivRefresh,buildURIIntent(context));
        views.setViewVisibility(R.id.ivRefresh, View.GONE);
        views.setViewVisibility(R.id.content, View.GONE);
        views.setViewVisibility(R.id.progressBar, View.VISIBLE);
        views.setViewVisibility(R.id.tvLoading, View.VISIBLE);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(appWidgetId, views);

        views.setOnClickPendingIntent(R.id.ivRefresh, buildRefreshIntent(context));

        volleyLogin(context, views, appWidgetId);

        return views;
    }

    public void setData(RemoteViews remoteViews) {

    }

    public void volleyLogin(final Context context, final RemoteViews views, final int appWidgetId) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final MyRequest stringRequest = new MyRequest(Request.Method.POST, "http://smartelmobile.com/user/member/login_proc.asp", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                String utf8Str = "";

                try {
                    utf8Str = new String(response.getBytes("ISO-8859-1"), "KSC5601");
                    Log.d("MyLog", "Widget: login: " + utf8Str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (utf8Str != null && !"".equals(utf8Str)) {

                    if (utf8Str.contains("href")) {
                        Log.d("MyLog", "Widget: 로그인 성공");
                        volleyCheckUsement(context, views, appWidgetId);
                    } else {
                        Log.d("MyLog", "Widget: 로그인 실패");
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyLog", "Widget: error : " + error);
                final VolleyError err = error;

                Log.d("MyLog", "Widget: " + err.toString() + "");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("goUrl", "/user/mypage/m_service_info.asp");
                params.put("top_search", "");
                params.put("hp_no", TEXT_PHONE);
                params.put("user_nm", TEXT_NAME);
                params.put("pwd", TEXT_PW);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                20000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    } // end of volleyLogin

    public void volleyCheckUsement(final Context context, final RemoteViews views, final int appWidgetId) {

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://smartelmobile.com/user/mypage/m_realTimePay_info.asp", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                Log.d("MyLog", "response: " + response);
                String utf8Str = "";
                try {
                    utf8Str = new String(response.getBytes("ISO-8859-1"), "KSC5601");
//                    Log.d("MyLog", "response encoded: " + utf8Str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (utf8Str != null && !"".equals(utf8Str)) {
//                    String splitArr[] = utf8Str.split("\"|\'");
//                    for (int i = 0; i < splitArr.length; i++) {
//                        Log.d("MyLog", i + ": " + splitArr[i]);
//                    }

//                    Log.d("MyLog", "요금조회 성공: " +  utf8Str);
                    Log.d("MyLog", "Widget: 요금조회 성공");

                    Document doc = Jsoup.parse(utf8Str);
                    Element table = doc.select("table.rn_table7.mt10").get(0);

                    Element trTotal = table.select("tr").get(2);
                    Elements totalTds = trTotal.select("td");

                    Element trLeft = table.select("tr").get(4);
                    Elements leftTds = trLeft.select("td");
                    LEFT_CALL = leftTds.get(1).html();
                    LEFT_SMS = leftTds.get(3).html();
                    LEFT_DATA = leftTds.get(4).html();

                    views.setViewVisibility(R.id.ivRefresh, View.VISIBLE);
                    views.setViewVisibility(R.id.content, View.VISIBLE);
                    views.setViewVisibility(R.id.progressBar, View.GONE);
                    views.setViewVisibility(R.id.tvLoading, View.GONE);
                    AppWidgetManager manager = AppWidgetManager.getInstance(context);

                    views.setTextViewText(R.id.tvLeftCall, LEFT_CALL);
                    views.setTextViewText(R.id.tvLeftSms, LEFT_SMS);
                    views.setTextViewText(R.id.tvLeftData, LEFT_DATA);

                    manager.updateAppWidget(appWidgetId, views);

                } // end of res String null

            } // end of onResponse

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyLog", "Widget: error : " + error);
                final VolleyError err = error;

                Log.d("MyLog", "Widget: " + err.toString() + "");

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("Cookie", TEXT_COOKIE);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                20000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    } // end of volleyCheckPaySystem

    class MyRequest extends StringRequest{
        private String cookie = "";

        public MyRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public MyRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            Map<String, String> map = response.headers;

//            Set set = map.keySet();
//            Iterator iter = set.iterator();
//
//            while (iter.hasNext()) {
//                String key = (String) iter.next();
//                String value = map.get(key);
//                Log.d("MyLog", "key: " + key + ", value = " + value);
//            }

            String strCookie = map.get("Set-Cookie");
            String strArr[] = strCookie.split(";");
            TEXT_COOKIE = strArr[0];
            Log.d("MyLog", "Widget: TEXT_COOKIE: " + TEXT_COOKIE);

            return super.parseNetworkResponse(response);
        }

        public String getCookie() {
            return cookie;
        }
        public void setCookie(String cookie) {
            this.cookie = cookie;

        }
    }

} // end of class

package com.daehee.smartel.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daehee.smartel.view.DoughnutView;
import com.daehee.smartel.util.LayoutUtils;
import com.daehee.smartel.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String URL_LOGIN = "http://smartelmobile.com/user/member/login.asp?goUrl=/user/mypage/m_service_info.asp";
    final String URL_PAY = "http://smartelmobile.com/user/mypage/m_realTimePay_info.asp";

    final String TEXT_PHONE = "01025513286";
    final String TEXT_NAME = "ECA095EB8C80ED9DAC";
    final String TEXT_PW = ""; // 반드시 필요 !

    String TEXT_COOKIE = "";

    WebView webView;

    String call = "";
    String sms = "";
    String data = "";
    String product = "";

    boolean STATE_LOGIN = false;
    boolean STATE_PAY = false;
    boolean STATE_DONE = false;

    TextView txtv;
    DoughnutView dnView;
    DoughnutView dnView02;
    DoughnutView dnView03;

    TextView tvName;
    TextView tvPhone;
    TextView tvProduct;
    TextView tvStartDate;
    TextView tvType;
    TextView tvStatus;
    TextView tvUseCall;
    TextView tvUseSms;
    TextView tvUseData;

    String TOTAL_CALL = "";
    String TOTAL_SMS  = "";
    String TOTAL_DATA = "";

    String USED_CALL = "";
    String USED_SMS  = "";
    String USED_DATA = "";

    String LEFT_CALL = "";
    String LEFT_SMS  = "";
    String LEFT_DATA = "";

    String USER_PHONE_NUM = "";
    String USER_PAY_SYSTEM = "";

    String USER_STATUS = "";
    String USER_START_DATE = "";
    String USER_TYPE = "";

    String USER_NAME = "";

    ProgressDialog pd;


    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String DEVICE_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        MobileAds.initialize(getApplicationContext(), getString(R.string.ads_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d("MyLog", "onAdClosed");
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d("MyLog", "onAdFailedToLoad");
                super.onAdFailedToLoad(i);
//                webView.loadUrl(URL_LOGIN); // 해당 주소로 사이트 연결
            }

            @Override
            public void onAdLeftApplication() {
                Log.d("MyLog", "onAdLeftApplication");
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                Log.d("MyLog", "onAdOpened");
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                Log.d("MyLog", "onAdLoaded");
//                webView.loadUrl(URL_LOGIN); // 해당 주소로 사이트 연결
                super.onAdLoaded();
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(DEVICE_ID)
                .build();
        mAdView.loadAd(adRequest);

        int DP_24 = (int) LayoutUtils.pxFromDp(getApplicationContext(), 24);
        int DP_36 = (int) LayoutUtils.pxFromDp(getApplicationContext(), 36);

        pd = new ProgressDialog(MainActivity.this);
        pd.setCancelable(false);
        pd.setMessage("로그인 중입니다");

        dnView = (DoughnutView) findViewById(R.id.dnView);
        dnView02 = (DoughnutView) findViewById(R.id.dnView02);
        dnView03 = (DoughnutView) findViewById(R.id.dnView03);

        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvProduct = (TextView) findViewById(R.id.tvProduct);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvType = (TextView) findViewById(R.id.tvType);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvUseCall = (TextView) findViewById(R.id.tvUseCall);
        tvUseSms = (TextView) findViewById(R.id.tvUseSms);
        tvUseData = (TextView) findViewById(R.id.tvUseData);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        txtv = (TextView) pd.findViewById(android.R.id.message);
        ProgressBar pb = new ProgressBar(getApplicationContext());

        volleyLogin();
    } // end of onCreate

    public void dismissBar() {
//        bar.dismiss();
        pd.dismiss();
    }

    public void refreshView() {

        tvName.setText(USER_NAME + " 님");
        tvPhone.setText("(" + USER_PHONE_NUM + ")");
        tvProduct.setText(USER_PAY_SYSTEM);

        tvStatus.setText(USER_STATUS);
        tvStartDate.setText(USER_START_DATE);
        tvType.setText(USER_TYPE);

        int PER_CALL = 360;
        int PER_SMS = 360;
        int PER_DATA = 360;

        if ("무제한".equals(TOTAL_CALL)) {
            tvUseCall.setText("기본제공");
        } else {
            tvUseCall.setText(USED_CALL + " / " + TOTAL_CALL);
            PER_CALL = (Integer.parseInt( (USED_CALL.substring(0, USED_CALL.length()-1)).replace(",", "") ) * 360
                    / Integer.parseInt( (TOTAL_CALL.substring(0, TOTAL_CALL.length()-1)).replace(",", "")) );
        }

        if ("무제한".equals(TOTAL_SMS)) {
            tvUseSms.setText("기본제공");
        } else {
            tvUseSms.setText(USED_SMS + " / " + TOTAL_SMS);
            PER_SMS = (Integer.parseInt( (USED_SMS.substring(0, USED_SMS.length()-1)).replace(",", "") ) * 360
                    / Integer.parseInt( (TOTAL_SMS.substring(0, TOTAL_SMS.length()-1)).replace(",", "")) );
        }

        tvUseData.setText(USED_DATA + " / " + TOTAL_DATA);
        PER_DATA = (Integer.parseInt( (USED_DATA.substring(0, USED_DATA.length()-2)).replace(",", "") ) * 360
                / Integer.parseInt( (TOTAL_DATA.substring(0, TOTAL_DATA.length()-2)).replace(",", "")) );


        /* start anim */
        if ("무제한".equals(TOTAL_CALL)) {
            dnView.animateArc(0.0f, 360.0f, 1500);
            dnView.setFree(true);
        } else {
            dnView.animateArc(0.0f, (float) PER_CALL, 1500);
            dnView.setFree(false);
        }

        if ("무제한".equals(TOTAL_SMS)) {
            dnView02.animateArc(0.0f, 360.0f, 1500);
            dnView02.setFree(true);
        } else {
            dnView02.animateArc(0.0f, (float) PER_SMS, 1500);
            dnView02.setFree(true);
        }
        dnView03.animateArc(0.0f, PER_DATA, 1500);
    }

    public void volleyLogin() {
        pd.show();

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final MyRequest stringRequest = new MyRequest(Request.Method.POST, "http://smartelmobile.com/user/member/login_proc.asp", new Response.Listener<String>() {

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

                    if (utf8Str.contains("href")) {
                        Log.d("MyLog", "로그인 성공");
                        volleyCheckPaySystem();
                    } else {
                        Log.d("MyLog", "로그인 실패");
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyLog", "error : " + error);
                final VolleyError err = error;

                Log.d("MyLog", err.toString()+"");

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
        requestQueue.add(stringRequest);
    } // end of volleyLogin

    public void volleyCheckPaySystem() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                pd.setMessage("가입정보를 확인중입니다");
            }
        });

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://smartelmobile.com/user/mypage/m_service_info.asp", new Response.Listener<String>() {

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

//                    Log.d("MyLog", "가입정보 조회 성공: " +  utf8Str);
                    Log.d("MyLog", "가입정보 조회 성공");

                    /* 가입정보 관련 parsing */
                    Document doc = Jsoup.parse(utf8Str);
                    Element table = doc.select("table.rn_table2").get(0);

                    Element trStatus = table.select("tr").get(0);
                    Element tdStatus = trStatus.select("td").get(0);
                    USER_STATUS = tdStatus.html();

                    Element trType = table.select("tr").get(1);
                    Element tdType = trType.select("td").get(0);
                    USER_TYPE = tdType.html();

                    Element trStartDate = table.select("tr").get(0);
                    Element tdStartDate = trStartDate.select("td").get(0);
                    USER_START_DATE = tdStartDate.html();


                    /* 이용 상품 관련 parsing */
                    doc = Jsoup.parse(utf8Str);
                    table = doc.select("table.rn_table3").get(0);

                    Element trPhoneNum = table.select("tr").get(0);
                    Element tdPhoneNum = trPhoneNum.select("td").get(0);
                    USER_PHONE_NUM = tdPhoneNum.html();

                    Element trPaySystem = table.select("tr").get(1);
                    Element tdPaySystem = trPaySystem.select("td").get(0);
                    USER_PAY_SYSTEM = tdPaySystem.html();

                    Log.d("MyLog", "[번호]: " + USER_PHONE_NUM + ", [요금제]: " + USER_PAY_SYSTEM);

                    volleyCheckUsement();
                } // end of res String null
            } // end of onResponse

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyLog", "error : " + error);
                final VolleyError err = error;

                Log.d("MyLog", err.toString()+"");

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

    public void volleyCheckUsement() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                pd.setMessage("실시간요금을 조회 중입니다\n다소 시간이 소요될 수 있습니다 (약 7초)");
            }
        });

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                    Log.d("MyLog", "요금조회 성공");

                    Document doc = Jsoup.parse(utf8Str);
                    Element table = doc.select("table.rn_table7.mt10").get(0);

                    Element trTotal = table.select("tr").get(2);
                    Elements totalTds = trTotal.select("td");
                    TOTAL_CALL = totalTds.get(1).html();
                    TOTAL_SMS = totalTds.get(3).html();
                    TOTAL_DATA = totalTds.get(4).html();

                    Element trUsed = table.select("tr").get(3);
                    Elements usedTds = trUsed.select("td");
                    USED_CALL = usedTds.get(1).html();
                    USED_SMS = usedTds.get(3).html();
                    USED_DATA = usedTds.get(4).html();

                    Element trLeft = table.select("tr").get(4);
                    Elements leftTds = trLeft.select("td");
                    LEFT_CALL = leftTds.get(1).html();
                    LEFT_SMS = leftTds.get(3).html();
                    LEFT_DATA = leftTds.get(4).html();

                    table = doc.select("table.rn_table2.mt14").get(0);
                    Element trName = table.select("tr").get(1);
                    Element tdName = trName.select("td").get(0);
                    USER_NAME = tdName.html();

                    Log.d("MyLog", "[통화]    제공: " + TOTAL_CALL + ", 사용: " +  USED_CALL + ", 잔여: " +  LEFT_CALL);
                    Log.d("MyLog", "[문자]    제공: " + TOTAL_SMS + ", 사용: " +  USED_SMS + ", 잔여: " +  LEFT_SMS);
                    Log.d("MyLog", "[데이터]\t제공: " + TOTAL_DATA + ", 사용: " +  USED_DATA + ", 잔여: " +  LEFT_DATA);

                } // end of res String null
                pd.dismiss(); // At the end of serial request, dismiss the dialog.

                refreshView();

            } // end of onResponse

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyLog", "error : " + error);
                final VolleyError err = error;

                Log.d("MyLog", err.toString()+"");

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
            Log.d("MyLog", "TEXT_COOKIE: " + TEXT_COOKIE);

            return super.parseNetworkResponse(response);
        }

        public String getCookie() {
            return cookie;
        }
        public void setCookie(String cookie) {
            this.cookie = cookie;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                break;
            case R.id.action_setting:
                break;
            case R.id.action_info:
                break;
        }

        return true;
    }
} // end of class

package br.eco.wash4me.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.eco.wash4me.activity.base.W4MApplication;
import br.eco.wash4me.entity.Account;
import br.eco.wash4me.entity.Order;
import br.eco.wash4me.entity.Supplier;
import br.eco.wash4me.entity.User;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.utils.Constants.*;

public class DataAccess {
    private static DataAccess dataAccess;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    public static DataAccess getDataAccess() {
        if(dataAccess == null) {
            dataAccess = new DataAccess();
        }

        return dataAccess;
    }

    private DataAccess() {
        VolleyLog.setTag("w4m.app.volley");
    }

    public void doLogin(Context context, Account account, final Callback<User> callback, final Callback<Void> errorCallback) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", account.getUsername());
        parameters.put("password", account.getPassword());

        GsonRequest<User> request = buildRequest(context, buildURL(AUTHENTICATE), Request.Method.POST,
                User.class, parameters, new Response.Listener<User>() {
                    @Override
                    public void onResponse(User user) {
                        callback.execute(user);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorCallback.execute(null);
                    }
                });

        queueRequest(context, request);
    }

    public void getOrders(Context context, final Callback<List<Order>> callback) {
        GsonRequest<Order[]> request = buildRequest(context, buildURL(ORDERS), Request.Method.GET,
                Order[].class, new Response.Listener<Order[]>() {
                    @Override
                    public void onResponse(Order[] orders) {
                        callback.execute(Arrays.asList(orders));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.execute(null);
                    }
                });

        queueRequest(context, request);
    }

    public void getSuppliers(Context context, final Callback<List<Supplier>> callback) {
        GsonRequest<Supplier[]> request = buildRequest(context, buildURL(SUPPLIERS), Request.Method.GET,
                Supplier[].class, new Response.Listener<Supplier[]>() {
                    @Override
                    public void onResponse(Supplier[] suppliers) {
                        callback.execute(Arrays.asList(suppliers));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.execute(null);
                    }
                });

        queueRequest(context, request);
    }

    private String buildURL(String service) {
        return String.format("%s/%s", W4MApplication.getInstance().getWsUrl(), service);
    }

    private <T> GsonRequest<T> buildRequest(final Context context, final String url, int method, Class<T> clazz,
                                            Response.Listener<T> listener, final Response.ErrorListener errorListener) {
        return buildRequest(context, url, method, clazz, new HashMap<String, Object>(), listener, errorListener);
    }

    private <T> GsonRequest<T> buildRequest(final Context context, final String url, int method, Class<T> clazz, Map<String, Object> parameters,
                                            Response.Listener<T> listener, final Response.ErrorListener errorListener) {
        return new GsonRequest<T>(url, method, clazz, buildHeaders(), buildRequestBody(parameters), listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logErrorResponse(context, url, error);

                errorListener.onErrorResponse(error);
            }
        });
    }

    private Map<String, String> buildHeaders() {
        return new HashMap<>();
    }

    private String buildRequestBody(Object content) {
        String output = null;
        if ((content instanceof String) ||
                (content instanceof JSONObject) ||
                (content instanceof JSONArray)) {
            output = content.toString();
        } else if (content instanceof Map) {
            Uri.Builder builder = new Uri.Builder();
            HashMap hashMap = (HashMap) content;
            if (hashMap != null) {
                Iterator entries = hashMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                    entries.remove();
                }
                output = builder.build().getEncodedQuery();
            }
        }

        return output;
    }

    private void logErrorResponse(Context context, String url, VolleyError error) {
        Long millis = new GregorianCalendar().getTimeInMillis() - getApplication().getLoginDateDebug(context);

        log(String.format("[onErrorResponse] STI %sms URL %s",
                millis.toString(),
                url));
        log(String.format("[onErrorResponse] ERR %s %s",
                error.getClass().toString(),
                error.getMessage()));
    }

    private void queueRequest(Context context, GsonRequest jsonRequest) {
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(8000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        logQueue(context, jsonRequest, false);

        getRequestQueue(context).add(jsonRequest);
    }

    private void queueRequestNonIdempontent(Context context, GsonRequest jsonRequest) {
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        logQueue(context, jsonRequest, true);

        getRequestQueue(context).add(jsonRequest);
    }

    private String logQueue(Context context, GsonRequest request, Boolean idempotent) {
        Integer qtdRequests = getApplication().getQtdRequestsDebug(context);

        if(getApplication().isLogged()) {
            qtdRequests += 1;
            getApplication().setDebugInformation(context, qtdRequests, null);
            Long millis = new GregorianCalendar().getTimeInMillis() - getApplication().getLoginDateDebug(context);

            request.setTag(String.format("%s:%s", qtdRequests.toString(), millis.toString()));

            log(String.format("[%s] STI %sms TAG #%s URL [%s] ",
                    idempotent ? "queueRequestNonIdempontent" : "queueRequest",
                    millis.toString(),
                    qtdRequests.toString(),
                    request.getUrl()));
        }

        return qtdRequests.toString();
    }

    private <T> RequestQueue getRequestQueue(final Context context) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<GsonRequest<T>>() {
                @Override
                public void onRequestFinished(Request<GsonRequest<T>> request) {
                    Long millis = new GregorianCalendar().getTimeInMillis() - getApplication().getLoginDateDebug(context);
                    Long duration = 0l;
                    String tag = "0";

                    if(request.getTag() != null) {
                        duration = millis - Long.valueOf(request.getTag().toString().split(":")[1]);
                        tag = request.getTag().toString().split(":")[0];
                    }

                    log(String.format("[requestFinishedListener] STI %sms DUR %sms TAG #%s URL [%s]",
                            millis.toString(),
                            duration.toString(),
                            tag,
                            request.getUrl()));
                }
            });
        }

        return requestQueue;
    }

    private W4MApplication getApplication() {
        return W4MApplication.getInstance();
    }

    public static void log(String msg) {
        Log.d("w4m.app.volley", msg);
    }
}

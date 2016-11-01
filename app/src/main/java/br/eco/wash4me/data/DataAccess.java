package br.eco.wash4me.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.eco.wash4me.activity.base.W4MApplication;
import br.eco.wash4me.entity.Account;
import br.eco.wash4me.entity.Model;
import br.eco.wash4me.entity.Order;
import br.eco.wash4me.entity.Product;
import br.eco.wash4me.entity.Supplier;
import br.eco.wash4me.entity.User;
import br.eco.wash4me.utils.Callback;

import static br.eco.wash4me.utils.Constants.*;

public class DataAccess {
    private static DataAccess dataAccess;

    private RequestQueue requestQueue;

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
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", account.getUsername());
        parameters.put("password", account.getPassword());

        GsonRequest<User> request = buildRequest(context, buildBaseURL(AUTHENTICATE), Request.Method.POST,
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

    public void getFacebookLoginData(final Context context, final Callback<User> callback, final Callback<Void> errorCallback) {
        final User loggedUser = new User();

        final Bundle parametersPicture = new Bundle();
        parametersPicture.putString("fields", "picture.width(150).height(150),name,email");

        new AsyncTask<Void, Void, GraphResponse>() {
            @Override
            protected GraphResponse doInBackground(Void... voids) {
                return new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/",
                        parametersPicture, null).executeAndWait();
            }

            @Override
            protected void onPostExecute(GraphResponse graphResponse) {
                super.onPostExecute(graphResponse);

                if (graphResponse != null && graphResponse.getError() == null &&
                        graphResponse.getJSONObject() != null) {
                    try {
                        String url = graphResponse.getJSONObject().getJSONObject("picture")
                                .getJSONObject("data").getString("url");
                        loggedUser.setName(graphResponse.getJSONObject().getString("name"));
                        loggedUser.setEmail(graphResponse.getJSONObject().getString("email"));

                        ImageRequest request = buildImageRequest(context, url, new Callback<Bitmap>() {
                            @Override
                            public void execute(Bitmap bitmap) {
                                W4MApplication.getInstance().setProfilePicture(bitmap);
                                callback.execute(loggedUser);
                            }
                        });

                        queueRequest(context, request);
                    } catch (JSONException e) {
                        log("[getFacebookLoginData] ERROR " + e.getMessage());

                        errorCallback.execute(null);
                    }
                }
            }
        }.execute();
    }

    public void getOrders(Context context, final Callback<List<Order>> callback) {
        GsonRequest<Order[]> request = buildRequest(context, buildAPIURL(ORDERS), Request.Method.GET,
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

    public void getProducts(Context context, final Callback<List<Product>> callback) {
        GsonRequest<Product[]> request = buildRequest(context, buildAPIURL(PRODUCTS), Request.Method.GET,
                Product[].class, new Response.Listener<Product[]>() {
                    @Override
                    public void onResponse(Product[] products) {
                        callback.execute(Arrays.asList(products));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.execute(null);
                    }
                });

        queueRequest(context, request);
    }

    public void getCarModels(Context context, final String brand, final Callback<List<Model>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("marca", brand);

        GsonRequest<Model[]> request = buildRequest(context, "http://www.webmotors.com.br/carro/modelosativos" + buildParameters(parameters), Request.Method.GET,
                Model[].class, new Response.Listener<Model[]>() {
                    @Override
                    public void onResponse(Model[] cars) {
                        callback.execute(Arrays.asList(cars));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.execute(new ArrayList<Model>());
                    }
                });

        queueRequest(context, request);
    }

    public void getSuppliers(Context context, final Callback<List<Supplier>> callback) {
        GsonRequest<Supplier[]> request = buildRequest(context, buildAPIURL(SUPPLIERS), Request.Method.GET,
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

    private String buildAPIURL(String service) {
        return String.format("%s/%s", W4MApplication.getInstance().getWsUrl(), service);
    }

    private String buildBaseURL(String service) {
        return String.format("%s/%s", W4MApplication.getInstance().getBaseUrl(), service);
    }

    private <T> GsonRequest<T> buildRequest(final Context context, final String url, int method, Class<T> clazz,
                                            Response.Listener<T> listener, final Response.ErrorListener errorListener) {
        return buildRequest(context, url, method, clazz, new HashMap<String, String>(), listener, errorListener);
    }

    private <T> GsonRequest<T> buildRequest(final Context context, final String url, int method, Class<T> clazz, Map<String, String> parameters,
                                            Response.Listener<T> listener, final Response.ErrorListener errorListener) {
        return new GsonRequest<T>(url, method, clazz, parameters, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logErrorResponse(context, url, error);

                errorListener.onErrorResponse(error);
            }
        });
    }

    private ImageRequest buildImageRequest(final Context context, String url, final Callback<Bitmap> callback) {
        return new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        callback.execute(bitmap);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        callback.execute(null);
                    }
                });
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

    private void queueRequest(Context context, Request jsonRequest) {
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));

        logQueue(context, jsonRequest, false);

        getRequestQueue(context).add(jsonRequest);
    }

    private void queueRequestNonIdempontent(Context context, Request jsonRequest) {
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));

        logQueue(context, jsonRequest, true);

        getRequestQueue(context).add(jsonRequest);
    }

    private String logQueue(Context context, Request request, Boolean idempotent) {
        Integer qtdRequests = getApplication().getQtdRequestsDebug(context);

        if(getApplication().isLogged(context)) {
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

    private String buildParameters(Map<String, String> parameters) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        for(String param : parameters.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(param, parameters.get(param)));
        }

        String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");

        return "?" + paramString;
    }
}

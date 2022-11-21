package com.hcdc.e_announce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Post {

    final String IP_ADDRESS = "10.10.10.63";
    String BASE_URL = "http://" + IP_ADDRESS + "/";
//    final String BASE_URL = "https://345a-210-1-128-68.ap.ngrok.io/";
    final String URL = BASE_URL + "e-announce/";
    RequestQueue requestQueue;
    StringRequest stringRequest;
    Context context;

    public Post(Context context) {
        this.context = context;
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void MYSQL(
            String fileName,
            Map<String, String> params,
            final VolleyCallBack callBack
    ) {
        stringRequest = new StringRequest(
                Request.Method.POST, URL + fileName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            callBack.onSuccess(array);
                        } catch (JSONException e) {
                            Log.e("MYSQL", e.getMessage());
                            Log.e("MYSQL", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("Not Connected to Internet");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void MYSQL(
            String fileName,
            final VolleyCallBack callBack
    ) {
        stringRequest = new StringRequest(
                Request.Method.POST, URL + fileName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            callBack.onSuccess(array);
                        } catch (JSONException e) {
                            Log.e("MYSQL", e.getMessage());
                            Log.e("MYSQL", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("Not Connected to Internet");
                    }
                }
        );
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void checkIfImageExist(
            String event_id,
            final VolleyCallBack callBack
    ) {
        String fileName = "checkIfImageExist.php";
        stringRequest = new StringRequest(
                Request.Method.POST, URL + fileName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray array = new JSONArray();
                        array.put(response);
                        callBack.onSuccess(array);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("Not Connected to Internet");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("event_id", event_id);
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getImage(
            String fileName,
            Map<String, String> params,
            final VolleyCallBack callBack
    ) {
        final ProgressDialog progressDialog = ProgressDialog.show(
                context, "Loading",
                "Loading images", false, false
        );
        stringRequest = new StringRequest(
                Request.Method.POST, URL + fileName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            callBack.onSuccess(array);
                        } catch (JSONException e) {
                            Log.e("MYSQL", e.getMessage());
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("Not Connected to Internet");
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("query", query);
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getResponse(String user_id, String message, StringCallBack callback) {
        String fileName = "chatBot.php?user_id=" + user_id + "&message=" + message;
        stringRequest = new StringRequest(
                Request.Method.GET, URL + fileName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("Not Connected to Internet");
                    }
                }
        );
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // Validate if user exist
    public void validateUser(String email, String password) {
        String filename = "login.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        MYSQL(filename, params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                try {
                    JSONObject userInfo = result.getJSONObject(0);
                    login(userInfo);
                } catch (JSONException e) {
                    Log.e("validateUser", e.getMessage());
                    toast("User does not exist");
                }
            }
        });
    }

    // Logs user in after validation
    public void login(JSONObject userInfo) {
        Iterator<String> keys = userInfo.keys();
        Intent intent = new Intent(context, HomeScreenActivity.class);
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                intent.putExtra(key, userInfo.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
//        toast("Login Successful");
        context.startActivity(intent);
    }

    public void createAccount(
            String name, String email, String password,
            String number, String address
    ) {
        String fileName = "signUp.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("number", number);
        params.put("address", address);
        MYSQL(fileName, params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                try {
                    JSONObject object = result.getJSONObject(0);
                    toast(object.getString("result"));
                    validateUser(email, password);
                } catch (JSONException e) {
                    Log.e("createAccount", e.getMessage());
                }
            }
        });
    }

    // Save message to database
    public void saveMessage(String user_id, String message, String bot) {
        String fileName = "saveMessage.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("message", message);
        params.put("bot", bot);
        MYSQL(fileName, params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                try {
                    JSONObject object = result.getJSONObject(0);
//                    toast(object.getString("result"));
                } catch (JSONException e) {
                    toast("User account does not exist");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap stringToBitmap(String encodedString){
        try{
            byte [] encodeByte = Base64.getDecoder().decode(encodedString);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String bitmapToString(Bitmap bitmap) {
        String strBase64 = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] bitmapBytes = baos.toByteArray();
        strBase64 = Base64.getEncoder().encodeToString(bitmapBytes);
        return strBase64;
    }

}

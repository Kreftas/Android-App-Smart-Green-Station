package Utility;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class DatabaseVolley {
    private RequestQueue requestQueue;

    public DatabaseVolley(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void fetchJsonObject(String url, CustomRequest customRequest) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, customRequest::handleResponse, Throwable::printStackTrace);
        requestQueue.add(request);
    }

    public void fetchJsonArray(String url, CustomRequest customRequest) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, customRequest::handleResponse, Throwable::printStackTrace);
        requestQueue.add(request);
    }


}

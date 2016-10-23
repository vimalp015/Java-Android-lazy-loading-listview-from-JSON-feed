package in.lamiv.android.listviewfromjsonfeed.helpers;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by vimal on 10/23/2016.
 * Recommended way to use AsyncHttpClient
 * AsyncHttpClient is developed by Twitter. It is free, well tested and reliable
 */

public class RestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        if(relativeUrl != null && !relativeUrl.trim().equals(""))
            return GlobalVars.JSON_FEED_URL + relativeUrl;
        else
            return GlobalVars.JSON_FEED_URL;
    }
}

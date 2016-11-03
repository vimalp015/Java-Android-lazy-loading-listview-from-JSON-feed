package in.lamiv.android.listviewfromjsonfeed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cz.msebera.android.httpclient.Header;
import in.lamiv.android.listviewfromjsonfeed.helpers.GlobalVars;
import in.lamiv.android.listviewfromjsonfeed.helpers.JSONFeed;
import in.lamiv.android.listviewfromjsonfeed.helpers.RestClient;
import in.lamiv.android.listviewfromjsonfeed.listloader.LazyLoadAdapter;


public class MainActivity extends AppCompatActivity {

    ListView list;
    LazyLoadAdapter adapter;
    LoadItemsCallback callbackDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(GlobalVars.LIST_LOADING_MESSAGE);
        callbackDelegate = new LoadItemsCallback() {
            @Override
            public void onDownloadFinishedCallback(JSONObject jsonObject) {
                onDownloadFinished(jsonObject);
            }
        };
        LoadItemsClass.loadItems(callbackDelegate);

        Button b = (Button) findViewById(R.id.buttonRefresh);
        b.setOnClickListener(listener);
    }

    //callback interface with onDownloadFinished option,
    // you can further add onFailure and other scenario
    interface LoadItemsCallback {
        void onDownloadFinishedCallback(JSONObject jsonObject);
    }

    //callback (delegate) function that will be executed on
    // downloading of JSON data from server
    private void onDownloadFinished(JSONObject jsonObject){
        Gson gson = new GsonBuilder().create();
        JSONFeed jsonFeed = gson.fromJson(jsonObject.toString(), JSONFeed.class);
        this.setTitle(jsonFeed.getTitle());
        adapter = new LazyLoadAdapter(this.getApplicationContext(), jsonFeed);
        list = (ListView) this.findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    //Static class in Java will not hold a reference to parent class
    //hence loosely coupled and thereby avoiding accidental memory leak
    private static class LoadItemsClass {

        public static void loadItems(LoadItemsCallback _loadItemsCallback) {

            //always pass weakreference to threads that might outlive your activity
            final WeakReference<LoadItemsCallback> loadItemsCallbackWeakReference =
                    new WeakReference<LoadItemsCallback>(_loadItemsCallback);

            RestClient.get(null, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    LoadItemsCallback loadItemsCallback = loadItemsCallbackWeakReference.get();
                    if (loadItemsCallback != null) {
                        loadItemsCallback.onDownloadFinishedCallback(response);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        list.setAdapter(null);
        super.onDestroy();
    }

    //Listener for Refresh Button click
    public OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            MainActivity.this.setTitle(GlobalVars.LIST_LOADING_MESSAGE);
            adapter.imageLoader.clearCache();
            list.setAdapter(null);
            LoadItemsClass.loadItems(callbackDelegate);
        }
    };
}

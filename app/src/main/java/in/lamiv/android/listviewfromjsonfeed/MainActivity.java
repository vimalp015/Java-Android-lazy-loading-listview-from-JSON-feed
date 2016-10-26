package in.lamiv.android.listviewfromjsonfeed;

import android.app.Activity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.setTitle(GlobalVars.LIST_LOADING_MESSAGE);

        loadItems(MainActivity.this);
        Button b = (Button) findViewById(R.id.buttonRefresh);
        b.setOnClickListener(listener);
    }

    public void loadItems(Activity _activity) {
        final WeakReference<Activity> activityWeakReference = new WeakReference<Activity>(_activity);
        RestClient.get(null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Activity activity = activityWeakReference.get();
                if(activity != null) {
                    Gson gson = new GsonBuilder().create();
                    JSONFeed jsonFeed = gson.fromJson(response.toString(), JSONFeed.class);
                    activity.setTitle(jsonFeed.getTitle());
                    adapter = new LazyLoadAdapter(activity, jsonFeed);
                    list = (ListView) findViewById(R.id.list);
                    list.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
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
            loadItems(MainActivity.this);
        }
    };
}

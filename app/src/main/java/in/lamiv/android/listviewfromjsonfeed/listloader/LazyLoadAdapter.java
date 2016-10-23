package in.lamiv.android.listviewfromjsonfeed.listloader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import in.lamiv.android.listviewfromjsonfeed.R;
import in.lamiv.android.listviewfromjsonfeed.helpers.JSONFeed;

/**
 * Created by vimal on 10/23/2016.
 * Adapter extention to load the JSON feed to listview and queue image loading
 * to ImageLoader class
 */

public class LazyLoadAdapter extends BaseAdapter implements View.OnClickListener {

    private final WeakReference<Activity> activity;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private static JSONFeed jsonFeed;

    public LazyLoadAdapter(Activity _activity, JSONFeed _jsonFeed) {
        activity = new WeakReference<Activity>(_activity);

        //Remove rows with all null values
        Iterator<JSONFeed.Row> rowIterator = _jsonFeed.getRows().iterator();
        while(rowIterator.hasNext()) {
            JSONFeed.Row row = rowIterator.next();
            if((row.getTitle() == null || row.getTitle().equals(""))
                    && (row.getDescription() == null || row.getDescription().equals(""))
                    && (row.getImageHref() == null || row.getImageHref().equals(""))) {
                rowIterator.remove();
            }
        }

        this.jsonFeed = _jsonFeed;
        Activity activityRef = activity.get();
        if(activityRef != null) {
            inflater = (LayoutInflater) activityRef.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ImageLoader(activityRef.getApplicationContext());
        }
    }

    @Override
    public int getCount() {
        return jsonFeed.getRows().size();
    }

    @Override
    public Object getItem(int pos) {
        return pos;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    public static class ViewHolder {
        public TextView topicHeading;
        public TextView topicDescription;
        public ImageView topicImage;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView == null) {

            vi = inflater.inflate(R.layout.listview_row, null);

            holder = new ViewHolder();
            holder.topicHeading = (TextView) vi.findViewById(R.id.topic_heading);
            holder.topicDescription = (TextView) vi.findViewById(R.id.topic_description);
            holder.topicImage = (ImageView) vi.findViewById(R.id.topic_image);

            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.topicHeading.setText(jsonFeed.getRows().get(pos).getTitle());
        holder.topicDescription.setText(jsonFeed.getRows().get(pos).getDescription());
        imageLoader.displayImage(jsonFeed.getRows().get(pos).getImageHref(),
                holder.topicImage, holder.topicDescription);

        return vi;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

}
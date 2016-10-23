package in.lamiv.android.listviewfromjsonfeed.listloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;
import in.lamiv.android.listviewfromjsonfeed.helpers.TextMarginSpan;
import in.lamiv.android.listviewfromjsonfeed.helpers.Utils;

/**
 * Created by vimal on 10/23/2016.
 */

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();

    FileCache fileCache;

    private static final int DEFAULT_NO_OF_THREADS = 5;

    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());

    ExecutorService executorService;

    Handler handler = new Handler();

    public ImageLoader(Context context) {

        fileCache = new FileCache(context);

        executorService = Executors.newFixedThreadPool(DEFAULT_NO_OF_THREADS);
    }

    public void displayImage(String url, ImageView imageView, TextView textView) {

        imageViews.put(imageView, url);

        Bitmap bitmap = memoryCache.get(url);

        if(bitmap != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
            if(!textView.getText().toString().equals("")) {
                SpannableString ss = new SpannableString(textView.getText().toString());
                int width = imageView.getLayoutParams().width + 40; //add 40px as padding space for 6 rows
                ss.setSpan(new TextMarginSpan(6, width), 1, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(ss);
            }
        } else {
            queuePhoto(url, imageView, textView);

            imageView.setVisibility(View.GONE);
            textView.setText(textView.getText().toString());
        }
    }

    private void queuePhoto(String url, ImageView imageView, TextView textView) {

        PhotoToLoad p = new PhotoToLoad(url, imageView, textView);

        executorService.submit(new PhotosLoader(p));
    }

    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public TextView textView;
        public PhotoToLoad(String u, ImageView i, TextView t) {
            url = u;
            imageView = i;
            textView = t;
        }
    }

    class PhotosLoader implements Runnable {

        PhotoToLoad photoToLoad;

        public PhotosLoader(PhotoToLoad ptl) {
            this.photoToLoad = ptl;
        }

        @Override
        public void run() {
            try {

                if(imageViewReused(photoToLoad))
                    return;

                File f = fileCache.getFile(photoToLoad.url);
                Bitmap b = decodeFile(f);
                if(b != null) {
                    processImage(photoToLoad, b);
                }
                else {
                    getImageFromURL(photoToLoad);
                }
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void processImage(PhotoToLoad photoToLoad, Bitmap bmp) {

        memoryCache.put(photoToLoad.url, bmp);

        if(imageViewReused(photoToLoad))
            return;

        BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);

        handler.post(bd);
    }

    private void getImageFromURL(PhotoToLoad _photoToLoad) {
        final PhotoToLoad photoToLoad = _photoToLoad;
        SyncHttpClient Client = new SyncHttpClient();
        Client.get(photoToLoad.url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    File _f = fileCache.getFile(photoToLoad.url);
                    InputStream is = new ByteArrayInputStream(responseBody);

                    if (!_f.exists()) {
                        _f.createNewFile();
                    }

                    OutputStream os = new FileOutputStream(_f);
                    Utils.copyStream(is, os);
                    os.close();
                    Bitmap bitmap = decodeFile(_f);
                    processImage(photoToLoad, bitmap);

                } catch(Throwable e) {
                    e.printStackTrace();

                    if(e instanceof OutOfMemoryError)
                        memoryCache.clear();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
    }

    private Bitmap decodeFile(File f) {

        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            final int REQUIRED_SIZE = 85;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }

                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    boolean imageViewReused(PhotoToLoad ptl) {

        String tag = imageViews.get(ptl.imageView);

        if(tag == null || !tag.equals(ptl.url))
            return true;
        return false;
    }

    class BitmapDisplayer implements Runnable {

        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;

            if(bitmap != null) {
                photoToLoad.imageView.setVisibility(View.VISIBLE);
                photoToLoad.imageView.setImageBitmap(bitmap);

                if(!photoToLoad.textView.getText().toString().equals("")) {
                    SpannableString ss = new SpannableString(photoToLoad.textView.getText().toString());
                    int width = photoToLoad.imageView.getLayoutParams().width + 40; //add 40px as padding space for 6 rows
                    ss.setSpan(new TextMarginSpan(6, width), 1, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    photoToLoad.textView.setText(ss);
                }

            } else {
                photoToLoad.imageView.setVisibility(View.GONE);
                photoToLoad.textView.setText(photoToLoad.textView.getText().toString());
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
}

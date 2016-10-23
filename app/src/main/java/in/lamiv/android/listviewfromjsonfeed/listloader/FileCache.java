package in.lamiv.android.listviewfromjsonfeed.listloader;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import in.lamiv.android.listviewfromjsonfeed.helpers.GlobalVars;

/**
 * Created by vimal on 10/23/2016.
 * Caches our file to the phone memory
 */

public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    GlobalVars.IMAGE_DIRECTORY);
        } else {
            cacheDir = context.getCacheDir();
        }

        if(!cacheDir.exists()) {
            Environment.getExternalStorageDirectory().setWritable(true);
            cacheDir.mkdirs();
        }
    }

    public File getFile(String url) {

        String filename = String.valueOf(url.hashCode());

        return new File(cacheDir, filename);
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if(files == null) {
            return;
        }

        for(File f : files) {
            f.delete();
        }
    }
}

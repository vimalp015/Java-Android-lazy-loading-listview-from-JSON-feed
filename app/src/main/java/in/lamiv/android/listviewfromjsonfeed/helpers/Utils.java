package in.lamiv.android.listviewfromjsonfeed.helpers;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vimal on 10/23/2016.
 * Will contain all the util methods like copyStream, date formatting etc. used in this project
 */

public class Utils {

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;

        try {

            byte[] bytes = new byte[buffer_size];

            for(;;) {

                int count = is.read(bytes, 0, buffer_size);
                if(count == -1) {
                    break;
                }

                os.write(bytes, 0, count);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

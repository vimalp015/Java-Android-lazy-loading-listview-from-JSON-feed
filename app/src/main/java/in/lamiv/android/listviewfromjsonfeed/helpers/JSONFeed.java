package in.lamiv.android.listviewfromjsonfeed.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vimal on 10/23/2016.
 */

public class JSONFeed {

    private String title;
    private List<Row> rows = new ArrayList<Row>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public class Row {
        private String title;
        private String description;
        private String imageHref;

        public String getImageHref() {
            return imageHref;
        }

        public void setImageHref(String imageHref) {
            this.imageHref = imageHref;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

}

package in.lamiv.android.listviewfromjsonfeed.helpers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * Created by vimal on 10/23/2016.
 * This class is required to provide float effect for the description text in items in listview
 */

public class TextMarginSpan implements LeadingMarginSpan.LeadingMarginSpan2 {
    private int margin;
    private int lines;

    public TextMarginSpan(int lines, int margin) {
        this.margin = margin;
        this.lines = lines;
    }

    /* Returns the value to which must be added to the indentation */
    @Override
    public int getLeadingMargin(boolean first) {
        if (first) {
            /*
             * This padding is applied to the number of
             * rows returned getLeadingMarginLineCount ()
             */
            return margin;
        } else {
            // Indent all other rows
            return 0;
        }
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

    }

    /*
     *
     * Returns the number of rows to which indent returned by
     * getLeadingMargin (true) is to be applied Note:
     * The indentation is applied only to the
     * N rows of the first paragraph.
     *
     */
    @Override
    public int getLeadingMarginLineCount() {
        return lines;
    }
}


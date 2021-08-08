package com.example.cryptoapp;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {
    private TextView tvContent;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent=(TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(""+e.getY());
        super.refreshContent(e,highlight);
    }

    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {
        if (mOffset==null)
        {
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return super.getOffset();
    }

}

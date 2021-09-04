package com.app.buna.sharingmarket.customview;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WrapWidthTextView
        extends androidx.appcompat.widget.AppCompatTextView {
    public WrapWidthTextView(@NonNull Context context) {
        super(context);
    }

    public WrapWidthTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapWidthTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // constructors here

    @Override protected void onMeasure (final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure (widthMeasureSpec, heightMeasureSpec);

        Layout layout = getLayout ();
        if (layout != null) {
            int width = (int) Math.ceil (getMaxLineWidth (layout))
                    + getCompoundPaddingLeft () + getCompoundPaddingRight ();
            int height = getMeasuredHeight ();
            setMeasuredDimension (width, height);
        }
    }

    private float getMaxLineWidth (Layout layout) {
        float max_width = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth (i) > max_width) {
                max_width = layout.getLineWidth (i);
            }
        }
        return max_width;
    }
}
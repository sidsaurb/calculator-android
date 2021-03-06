package com.example.siddhant.calculator_cs654.helperClasses;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Created by Siddhant on 12/14/2015.
 */
public class TypefaceSpan extends MetricAffectingSpan {
    /** An <code>LruCache</code> for previously loaded typefaces. */
//    private static LruCache<String, Typeface> sTypefaceCache =
//            new LruCache<String, Typeface>(12);

    private Typeface mTypeface;

    /**
     * Load the {@link Typeface} and apply to a {@link Spannable}.
     */
    public TypefaceSpan(Typeface typeface) {
        mTypeface = typeface;

//        if (mTypeface == null) {
//            mTypeface = Typeface.createFromAsset(context.getApplicationContext()
//                    .getAssets(), String.format("%s", typefaceName));
//
//            // Cache the loaded Typeface
//            sTypefaceCache.put(typefaceName, mTypeface);
//        }
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);

        // Note: This flag is required for proper typeface rendering
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);

        // Note: This flag is required for proper typeface rendering
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}

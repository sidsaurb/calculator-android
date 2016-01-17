package com.example.siddhant.calculator_cs654.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.siddhant.calculator_cs654.R;
import com.example.siddhant.calculator_cs654.databaseClasses.DatabaseClass;

import java.util.ArrayList;

/**
 * Created by Siddhant Saurabh on 1/10/2016.
 */
public class HistoryAdapter extends ArrayAdapter<DatabaseClass> {
    private final Context context;
    Typeface typeface;

    public HistoryAdapter(Context context, ArrayList<DatabaseClass> chains) {
        super(context, R.layout.history_row, chains);
        this.context = context;
        typeface = Typeface.createFromAsset(context.getAssets(),
                "proxima.otf");
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final DatabaseClass singleHistory = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.history_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.queryTextView = (TextView) convertView.findViewById(R.id.queryTextView);
            viewHolder.responseTextView = (TextView) convertView.findViewById(R.id.responseTextView);
//            viewHolder.scrollView = (HorizontalScrollView) convertView.findViewById(R.id.scrollBar);
            if (!singleHistory.animationStates) {
                singleHistory.animationStates = true;
                Animation an = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                an.setStartOffset(position * 50);
                convertView.startAnimation(an);
            }
            viewHolder.queryTextView.setTypeface(typeface);
            viewHolder.responseTextView.setTypeface(typeface);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.queryTextView.setText(singleHistory.query);
        viewHolder.responseTextView.setText("= " + singleHistory.response);
        return convertView;
    }

    class ViewHolder {
        public TextView queryTextView;
        public TextView responseTextView;
//        public HorizontalScrollView scrollView;
    }
}

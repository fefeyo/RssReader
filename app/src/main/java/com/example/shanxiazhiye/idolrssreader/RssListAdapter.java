package com.example.shanxiazhiye.idolrssreader;

import android.content.ClipData;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shanxiazhiye on 2017/07/02.
 */

public class RssListAdapter extends ArrayAdapter<Blog> {
    private LayoutInflater mInflater;
    private TextView mTitle;
    private TextView mDescr;

    public RssListAdapter(Context context, List<Blog> objects){
        super(context,0 ,objects);
        mInflater =  (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
}

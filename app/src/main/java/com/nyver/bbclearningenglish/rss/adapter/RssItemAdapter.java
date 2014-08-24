package com.nyver.bbclearningenglish.rss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.util.List;

public class RssItemAdapter extends ArrayAdapter<RssItem> {

    public RssItemAdapter(Context context, int resource, List<RssItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rss_item, parent, false);

        RssItem item = getItem(position);

        TextView titleView = (TextView) rowView.findViewById(R.id.itemTitle);
        titleView.setText(item.getTitle());

        TextView descriptionView = (TextView) rowView.findViewById(R.id.itemDescription);
        descriptionView.setText(item.getSummary());

        return rowView;
    }
}

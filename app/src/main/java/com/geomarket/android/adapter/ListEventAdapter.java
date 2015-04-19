package com.geomarket.android.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.geomarket.android.R;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Adapter for viewing events
 */
public class ListEventAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<Event> mEvents;
    private List<Event> mFilteredEvents;
    private EventFilter mFilter = new EventFilter();

    public ListEventAdapter(Context context, List<Event> events) {
        this.mContext = context;
        this.mEvents = events;
        mFilteredEvents = new ArrayList<>(mEvents);
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_event_detail_item, parent, false);
            viewHolder = new EventViewHolder();
            viewHolder.mThumbImage = (ImageView) convertView.findViewById(R.id.list_event_item_thumb);
            viewHolder.mTypeText = (TextView) convertView.findViewById(R.id.list_event_item_type);
            viewHolder.mDurationText = (TextView) convertView.findViewById(R.id.list_event_item_duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (EventViewHolder) convertView.getTag();
        }
        Event event = mEvents.get(position);
        viewHolder.mTypeText.setText(event.getText().getHeading());
        Picasso.with(mContext).load(GeoMarketServiceApiBuilder.HOST + event.getImageSmallUrl()).into(viewHolder.mThumbImage);
        if (event.getExpires() != null) {
            viewHolder.mDurationText.setText(DateUtils.getRelativeTimeSpanString(event.getExpires(), new Date().getTime(),
                    0)); //DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    /**
     * Filter events
     */
    private class EventFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String searchQuery = constraint.toString().toLowerCase();
            List<Event> filteredEvents = new ArrayList<>();

            for (Event e : mEvents) {
                if (e.shouldBeFiltered(searchQuery)) {
                    filteredEvents.add(e);
                }
            }
            results.values = filteredEvents;
            results.count = filteredEvents.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredEvents = (ArrayList<Event>) results.values;
            notifyDataSetChanged();
        }
    }

    static class EventViewHolder {
        private ImageView mThumbImage;
        private TextView mTypeText;
        private TextView mDurationText;
    }
}

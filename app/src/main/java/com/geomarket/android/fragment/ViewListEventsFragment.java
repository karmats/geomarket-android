package com.geomarket.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.geomarket.android.R;
import com.geomarket.android.adapter.ListEventAdapter;
import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the Callbacks
 * interface.
 */
public class ViewListEventsFragment extends Fragment implements AbsListView.OnItemClickListener {
    private static String EVENTS_ARG = "events_arg";

    private OnListEventClickListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListEventAdapter mAdapter;

    /**
     * The Event items
     */
    private ArrayList<Event> mEvents;

    public static ViewListEventsFragment newInstance(ArrayList<Event> events) {
        ViewListEventsFragment fragment = new ViewListEventsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EVENTS_ARG, events);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViewListEventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEvents = getArguments().getParcelableArrayList(EVENTS_ARG);
        mAdapter = new ListEventAdapter(getActivity(), mEvents);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vieweventslistfragment, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnListEventClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnListEventClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onListEventClick(mEvents.get(position));
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * Filter events
     *
     * @param filter Filter string
     */
    public void filterEvents(String filter) {
        mAdapter.getFilter().filter(filter);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListEventClickListener {

        /**
         * When user clicks on an {@link com.geomarket.android.api.Event}
         *
         * @param event The event clicked
         */
        public void onListEventClick(Event event);
    }

}

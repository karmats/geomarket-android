package com.geomarket.android.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geomarket.android.R;
import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.drive.Contents;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewEventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewEventDetailsFragment extends Fragment {

    private static final String ARG_EVENT = "event";

    private Event mEvent;

    @InjectView(R.id.view_event_detail_description)
    TextView mEventDescTextView;
    @InjectView(R.id.view_event_detail_location)
    TextView mEventLocation;
    @InjectView(R.id.view_event_detail_phone)
    TextView mEventPhone;
    @InjectView(R.id.view_event_detail_web_site)
    TextView mEventWebSite;
    @InjectView(R.id.view_event_detail_map_img)
    ImageView mEventMapImg;
    @InjectView(R.id.view_event_detail_qr_img)
    ImageView qrEventImg;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event The event to view
     * @return A new instance of fragment ViewEventDetailsFragment.
     */
    public static ViewEventDetailsFragment newInstance(Event event) {
        ViewEventDetailsFragment fragment = new ViewEventDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewEventDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEvent = getArguments().getParcelable(ARG_EVENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_event_details, container, false);
        ButterKnife.inject(this, view);
        mEventDescTextView.setText(mEvent.getText().getBody());
        mEventLocation.setText(mEvent.getCompany().getStreet() + mEvent.getCompany().getStreetNr());
        mEventPhone.setText(String.valueOf(mEvent.getCompany().getPostalCode()));
        mEventWebSite.setText(mEvent.getCompany().getName() + ".com");
        String locationString = mEvent.getLocation().getLatitude() + "," + mEvent.getLocation().getLongitude();

        // Map img
        String url = "http://maps.google.com/maps/api/staticmap?center=" + locationString + "&zoom=18&size=1200x400&sensor=false&markers=" + locationString + "&scale=2";
        Picasso.with(getActivity()).load(url).into(mEventMapImg);

        // QR coupon
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix qrCode = writer.encode("http://dibbler.se", BarcodeFormat.QR_CODE, 600, 600);
            qrEventImg.setImageBitmap(toBitmap(qrCode));
        } catch (WriterException e) {
            LogHelper.logException(e);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * Writes the given Matrix on a new Bitmap object.
     *
     * @param matrix the matrix to write.
     * @return the new {@link Bitmap}-object.
     */
    private Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

}

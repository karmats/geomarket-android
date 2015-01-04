package com.geomarket.android.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.geomarket.android.R;
import com.geomarket.android.util.LogHelper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Downloads an image form internetz
 */
public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

    private Context mContext;
    private String mUrl;
    private ImageView mImageView;

    public DownloadImageTask(Context context, String url, ImageView imageView) {
        this.mContext = context;
        this.mUrl = url;
        this.mImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            LogHelper.logException(e);
            // Return placeholder by default
            return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.placeholder);
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        mImageView.setImageBitmap(result);
    }

}
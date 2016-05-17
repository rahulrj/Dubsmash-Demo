package dubsmashdemo.android.com.dubsmashdemo.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import dubsmashdemo.android.com.dubsmashdemo.db.VideoDbHelper;
import dubsmashdemo.android.com.dubsmashdemo.interfaces.AsyncListener;
import dubsmashdemo.android.com.dubsmashdemo.model.VideoObject;

/**
 * Created by rahul.raja on 5/14/16.
 */

/**
 * {@link AsyncTask} to fetch the videos names list from DB
 */
public class DbFetcherTask extends AsyncTask<Void, Void, List<VideoObject>> {

    private static final String TAG = DbFetcherTask.class.getSimpleName();

    private final WeakReference<Context> mContext;
    private final AsyncListener mLoaderListener;

    public DbFetcherTask(Context context, AsyncListener asyncListener) {
        this.mContext = new WeakReference<>(context);
        mLoaderListener = asyncListener;
    }


    @Override
    protected List<VideoObject> doInBackground(Void... params) {
        List<VideoObject> videoObjects = new ArrayList<>();
        if (mContext.get() != null) {
            final VideoDbHelper db = new VideoDbHelper(mContext.get().getApplicationContext());
            videoObjects = db.getAllVideoDetails();
        }

        return videoObjects;
    }

    @Override
    protected void onPostExecute(List<VideoObject> videoObjects) {
        if (mLoaderListener != null) {
            if (videoObjects == null) {
                mLoaderListener.onTaskFailed();
            } else {
                mLoaderListener.onTaskSuccess(videoObjects);

            }
        } else {
            Log.d(TAG, "Listener destroyed. Cant load data");
        }
    }
}

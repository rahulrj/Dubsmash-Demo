package dubsmashdemo.android.com.dubsmashdemo.interfaces;

import dubsmashdemo.android.com.dubsmashdemo.async.DbFetcherTask;

/**
 * Created by rahul.raja on 5/14/16.
 */

/**
 * Interface to receive callbacks from {@link DbFetcherTask}
 */
public interface AsyncListener {

    void onTaskSuccess(Object data);

    void onTaskFailed();
}

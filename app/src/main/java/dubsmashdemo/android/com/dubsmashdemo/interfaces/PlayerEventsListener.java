package dubsmashdemo.android.com.dubsmashdemo.interfaces;

/**
 * Created by rahul.raja on 5/15/16.
 */


/**
 * A listener for core events.
 */
public interface PlayerEventsListener {
    void onStateChanged(boolean playWhenReady, int playbackState);

    void onError(Exception e);

    void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                            float pixelWidthHeightRatio);
}


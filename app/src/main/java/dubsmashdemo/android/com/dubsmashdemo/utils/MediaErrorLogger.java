package dubsmashdemo.android.com.dubsmashdemo.utils;

import android.media.MediaCodec;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import dubsmashdemo.android.com.dubsmashdemo.interfaces.PlayerInternalErrorListener;

/**
 * Created by rahul.raja on 5/15/16.
 */
public class MediaErrorLogger implements PlayerInternalErrorListener {

    private static String TAG = MediaErrorLogger.class.getSimpleName();

    private long sessionStartTimeMs;
    private static final NumberFormat TIME_FORMAT;

    static {
        TIME_FORMAT = NumberFormat.getInstance(Locale.US);
        TIME_FORMAT.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
    }

    public MediaErrorLogger() {
        sessionStartTimeMs = SystemClock.elapsedRealtime();

    }

    @Override
    public void onLoadError(int sourceId, IOException e) {
        printInternalError("loadError", e);
    }

    @Override
    public void onRendererInitializationError(Exception e) {
        printInternalError("rendererInitError", e);
    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        printInternalError("decoderInitializationError", e);
    }

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
        printInternalError("audioTrackInitializationError", e);
    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e) {
        printInternalError("audioTrackWriteError", e);
    }

    @Override
    public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        printInternalError("audioTrackUnderrun [" + bufferSize + ", " + bufferSizeMs + ", "
                + elapsedSinceLastFeedMs + "]", null);
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        printInternalError("cryptoError", e);
    }

    private String getSessionTimeString() {
        return getTimeString(SystemClock.elapsedRealtime() - sessionStartTimeMs);
    }

    private String getTimeString(long timeMs) {
        return TIME_FORMAT.format((timeMs) / 1000f);
    }

    private void printInternalError(String type, Exception e) {
        Log.e(TAG, "internalError [" + getSessionTimeString() + ", " + type + "]", e);
    }


}

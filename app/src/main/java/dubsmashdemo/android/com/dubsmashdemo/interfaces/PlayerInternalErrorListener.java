package dubsmashdemo.android.com.dubsmashdemo.interfaces;

/**
 * Created by rahul.raja on 5/15/16.
 */

import android.media.MediaCodec;

import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;

import java.io.IOException;

import dubsmashdemo.android.com.dubsmashdemo.player.ExoMediaPlayer;

/**
 * Interface to show internal errors of {@link ExoMediaPlayer} . These are for debugging purpose and won't be
 * visible to users
 */
public interface PlayerInternalErrorListener {
    void onRendererInitializationError(Exception e);

    void onAudioTrackInitializationError(AudioTrack.InitializationException e);

    void onAudioTrackWriteError(AudioTrack.WriteException e);

    void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs);

    void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e);

    void onCryptoError(MediaCodec.CryptoException e);

    void onLoadError(int sourceId, IOException e);
}

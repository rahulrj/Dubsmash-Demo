package dubsmashdemo.android.com.dubsmashdemo.interfaces;

/**
 * Created by rahul.raja on 5/15/16.
 */

import dubsmashdemo.android.com.dubsmashdemo.player.MediaPlayer;

/**
 * Builds renderers for the player.
 */
public interface PlayerRendererBuilder {
    /**
     * Builds renderers for playback.
     *
     * @param player The player for which renderers are being built. {@link MediaPlayer#onRenderers}
     *               should be invoked once the renderers have been built. If building fails,
     *               {@link MediaPlayer#onRenderersError} should be invoked.
     */
    void buildRenderers(MediaPlayer player);

    /**
     * Cancels the current build operation, if there is one. Else does nothing.
     * <p>
     * A canceled build operation must not invoke {@link MediaPlayer#onRenderers} or
     * {@link MediaPlayer#onRenderersError} on the player, which may have been released.
     */
    void cancel();
}

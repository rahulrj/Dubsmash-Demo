package dubsmashdemo.android.com.dubsmashdemo.interfaces;

/**
 * Created by rahul.raja on 5/15/16.
 */

import dubsmashdemo.android.com.dubsmashdemo.player.ExoMediaPlayer;

/**
 * Builds renderers for the {@link ExoMediaPlayer}.
 */
public interface PlayerRendererBuilder {
    /**
     * Builds renderers for playback.
     *
     * @param player The player for which renderers are being built. {@link ExoMediaPlayer#onRenderers}
     *               should be invoked once the renderers have been built. If building fails,
     *               {@link ExoMediaPlayer#onRenderersError} should be invoked.
     */
    void buildRenderers(ExoMediaPlayer player);

    /**
     * Cancels the current build operation, if there is one. Else does nothing.
     * <p/>
     * A canceled build operation must not invoke {@link ExoMediaPlayer#onRenderers} or
     * {@link ExoMediaPlayer#onRenderersError} on the player, which may have been released.
     */
    void cancel();
}

package dubsmashdemo.android.com.dubsmashdemo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;

import dubsmashdemo.android.com.dubsmashdemo.R;
import dubsmashdemo.android.com.dubsmashdemo.player.ExtractorRendererBuilder;
import dubsmashdemo.android.com.dubsmashdemo.player.MediaPlayer;
import dubsmashdemo.android.com.dubsmashdemo.utils.Constants;
import dubsmashdemo.android.com.dubsmashdemo.utils.Utils;

/**
 * Created by rahul.raja on 5/14/16.
 */
public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.Listener {

    private View mRootLayout;
    private AspectRatioFrameLayout mVideoFrame;
    private SurfaceView mSurfaceView;
    private MediaPlayer player;
    private Uri mMediaUri;
    private boolean playerNeedsPrepare;
    private long playerPosition;
    private boolean enableBackgroundAudio;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            String mediaPath = intent.getStringExtra(Constants.KEY_VIDEO_PATH);
            if (mediaPath != null) {
                mMediaUri = Uri.parse(mediaPath);
            }
        }
        setUpLayout();
    }

    private void setUpLayout() {
        mRootLayout = findViewById(R.id.root);
        mVideoFrame = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        onShown();

    }

    @Override
    protected void onPause() {
        super.onPause();
        onHidden();

    }

    @Override
    protected void onStop() {
        super.onStop();
        onHidden();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void onShown() {
        if (player == null) {
            preparePlayer(true);
        } else {
            player.setBackgrounded(false);
        }
    }


    private void onHidden() {
        if (!enableBackgroundAudio) {
            releasePlayer();
        } else {
            player.setBackgrounded(true);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }


    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            String userAgent = Utils.getUserAgent(this, "DubsmashDemo");
            MediaPlayer.RendererBuilder rendererBuilder = new ExtractorRendererBuilder(this, userAgent, mMediaUri);
            player = new MediaPlayer(rendererBuilder);
            player.addListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(mSurfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }


    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            player.seekTo(0);
            player.setPlayWhenReady(true);
        }

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        mVideoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
    }

    private void showSnackMessage(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG).show();

    }

}

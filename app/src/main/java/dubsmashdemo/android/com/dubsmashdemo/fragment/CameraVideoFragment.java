package dubsmashdemo.android.com.dubsmashdemo.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dubsmashdemo.android.com.dubsmashdemo.R;
import dubsmashdemo.android.com.dubsmashdemo.utils.CameraHelper;
import dubsmashdemo.android.com.dubsmashdemo.utils.Constants;

/**
 * Created by rahul.raja on 5/15/16.
 */

/**
 * This fragment uses the {@link Camera} APIs which are deprecated since Android API 21
 */
@SuppressWarnings("deprecation")
public class CameraVideoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CameraVideoFragment.class.getSimpleName();

    private File mVideoFile;
    private CountDownTimer mCountDownTimer;
    private TextView mTimerView;
    private TextureView mTextureView;
    private Button mButtonVideo;
    private String mVideoAbsolutePath;
    private boolean mIsRecordingVideo;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;


    public static CameraVideoFragment newInstance() {
        return new CameraVideoFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mVideoFile = (File) arguments.getSerializable(Constants.KEY_VIDEO_FILE_NAME);
        }
        if (mVideoFile != null) {
            mVideoAbsolutePath = mVideoFile.getAbsolutePath();
        }
        return inflater.inflate(R.layout.fragment_camera2_video, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mTextureView = (TextureView) view.findViewById(R.id.texture);
        mButtonVideo = (Button) view.findViewById(R.id.video);
        mTimerView = (TextView) view.findViewById(R.id.timer);
        mButtonVideo.setOnClickListener(this);
        new MediaPrepareTask().execute(null, null, null);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video: {
                onRecordClick();

            }
        }
    }

    /**
     * The capture button controls all user interaction. When recording, the button click
     * stops recording, releases {@link android.media.MediaRecorder} and {@link android.hardware.Camera}. When not recording,
     * it prepares the {@link android.media.MediaRecorder} and starts recording.
     **/

    public void onRecordClick() {
        if (mIsRecordingVideo) {
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                //noinspection ResultOfMethodCallIgnored
                mVideoFile.delete();
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            mIsRecordingVideo = false;
            releaseCamera();
            cancelTheTimer();
            sendDataBackToActivity();

        } else {
            mIsRecordingVideo = true;
            // inform the user that recording has started
            mMediaRecorder.start();
            setCaptureButtonText(getString(R.string.stop));
            startTheTimer();

        }
    }

    private void cancelTheTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void startTheTimer() {
        mCountDownTimer = new CountDownTimer(Constants.RECORDING_MAX_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerView.setText(String.format("%02d:%02d:%02d", 0, 0, (millisUntilFinished / 1000)));

            }

            @Override
            public void onFinish() {
                mTimerView.setText(getString(R.string.finish_string));

            }
        }.start();

    }

    private void setCaptureButtonText(String title) {
        mButtonVideo.setText(title);
    }

    @Override
    public void onPause() {
        super.onPause();
        // if we are using MediaRecorder, release it first
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean prepareVideoRecorder() {

        mCamera = CameraHelper.getDefaultCameraInstance();

        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, mTextureView.getWidth(), mTextureView.getHeight());

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);
        try {
            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
            // with {@link SurfaceView}
            mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }


        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);

        mMediaRecorder.setOutputFile(mVideoAbsolutePath);
        mMediaRecorder.setMaxDuration(Constants.RECORDING_MAX_DURATION);

        // Step 4 : set info on the recorder
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    sendDataBackToActivity();
                }
            }
        });

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    private void sendDataBackToActivity() {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_VIDEO_ABS_PATH, mVideoFile.getAbsolutePath());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }


    /**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                return true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }

        }
    }
}

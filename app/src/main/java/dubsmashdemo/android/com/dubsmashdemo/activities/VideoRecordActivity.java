package dubsmashdemo.android.com.dubsmashdemo.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

import dubsmashdemo.android.com.dubsmashdemo.R;
import dubsmashdemo.android.com.dubsmashdemo.fragment.Camera2VideoFragment;
import dubsmashdemo.android.com.dubsmashdemo.utils.Constants;

/**
 * Created by rahul.raja on 5/15/16.
 */
public class VideoRecordActivity extends Activity {


    private File mVideoFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        getDataFromIntent();
        setUpVideoRecordFragment();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mVideoFileName = (File) intent.getSerializableExtra(Constants.KEY_VIDEO_FILE_NAME);
        }
    }

    private void setUpVideoRecordFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_VIDEO_FILE_NAME, mVideoFileName);
        Fragment camera2VideoFragment = Camera2VideoFragment.newInstance();
        camera2VideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, camera2VideoFragment).commit();

    }
}

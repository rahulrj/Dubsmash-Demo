package dubsmashdemo.android.com.dubsmashdemo.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dubsmashdemo.android.com.dubsmashdemo.R;
import dubsmashdemo.android.com.dubsmashdemo.activities.VideoRecordActivity;
import dubsmashdemo.android.com.dubsmashdemo.adapter.VideoFilesAdapter;
import dubsmashdemo.android.com.dubsmashdemo.async.DbFetcherTask;
import dubsmashdemo.android.com.dubsmashdemo.db.VideoDbHelper;
import dubsmashdemo.android.com.dubsmashdemo.interfaces.LoaderListener;
import dubsmashdemo.android.com.dubsmashdemo.model.VideoObject;
import dubsmashdemo.android.com.dubsmashdemo.utils.Constants;
import dubsmashdemo.android.com.dubsmashdemo.utils.Utils;

/**
 * Created by rahul.raja on 5/13/16.
 */

/**
 * Fragment to show the list of all the videos available
 */
public class VideoListFragment extends Fragment implements LoaderListener {

    private static final String TAG = VideoListFragment.class.getSimpleName();

    private RecyclerView mVideoRecyclerView;
    private View mFragmentRootView;
    private String mVideoFileName;

    private List<VideoObject> mAllVideoFiles = new ArrayList<>();
    private VideoFilesAdapter mVideoFilesAdapter;


    public VideoListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentRootView = view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpLayout(mFragmentRootView);
        getSavedVideoFilesListFromDb();
    }

    private void setUpLayout(View view) {
        mVideoRecyclerView = (RecyclerView) view.findViewById(R.id.rv_videoList);
        mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mVideoRecyclerView.setHasFixedSize(true);
    }

    private void getSavedVideoFilesListFromDb() {
        DbFetcherTask videoFilesListFetcher = new DbFetcherTask(getActivity(), this);
        videoFilesListFetcher.execute();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_video_gallery, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_recordvideo:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Utils.hasPermissionsGranted(getActivity(), Constants.STORAGE_PERMISSIONS)) {
                        requestVideoPermissions();
                        return super.onOptionsItemSelected(item);

                    }
                }
                startRecordingVideo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startRecordingVideo() {
        if (getActivity() != null) {
            mVideoFileName = Constants.VIDEO_NAME_INIT + new SimpleDateFormat(Constants.VIDEO_DATE_FORMAT, Locale.US).format(new Date());
            File videoDir = Utils.getVideoDirectory(getActivity());
            if (videoDir == null) {
                showSnackMessage("Failed to create video directory");
                return;
            }
            File videoFile = null;
            try {
                videoFile = File.createTempFile(mVideoFileName, Constants.VIDEO_EXTENSION, videoDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (videoFile == null) {
                showSnackMessage("Failed to create video directory");
                return;
            }

            Intent intent = new Intent(getActivity(), VideoRecordActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.KEY_VIDEO_FILE_NAME, videoFile);
            intent.putExtras(bundle);
            startActivityForResult(intent, Constants.VIDEO_CAPTURE_CAMERA);


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.VIDEO_CAPTURE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(mFragmentRootView, "Video has been saved as : " + mVideoFileName, Snackbar.LENGTH_LONG).show();
                VideoObject videoObject = new VideoObject(mVideoFileName, data.getStringExtra(Constants.KEY_VIDEO_ABS_PATH));
                saveVideoPathInDb(videoObject);
                mAllVideoFiles.add(videoObject);
                if (mVideoFilesAdapter != null) {
                    updateRecyclerViewAdapter();
                } else {
                    setDataInAdapter();
                }


            }
        }
    }


    private void saveVideoPathInDb(VideoObject videoObject) {
        VideoDbHelper db = new VideoDbHelper(getActivity().getApplicationContext());
        db.addVideoDetails(videoObject);

    }

    private void updateRecyclerViewAdapter() {
        mVideoFilesAdapter.updateAdapter(mAllVideoFiles);
    }

    private void setDataInAdapter() {
        mVideoFilesAdapter = new VideoFilesAdapter(mAllVideoFiles);
        mVideoRecyclerView.setAdapter(mVideoFilesAdapter);
    }

    @Override
    public void onDataLoadSucceeded(Object data) {
        try {
            mAllVideoFiles = (List<VideoObject>) data;
            if (mAllVideoFiles != null && mAllVideoFiles.size() == 0) {
                showSnackMessage("No Video Data found");
                return;
            }
            if (mAllVideoFiles != null) {
                setDataInAdapter();
            }
        } catch (ClassCastException cle) {
            showSnackMessage("Error: Data could not be loaded");
        }
    }

    @Override
    public void onDataLoadFailed() {
        showSnackMessage("Data could not be loaded");
    }


    private void showSnackMessage(String message) {
        Snackbar.make(mFragmentRootView, message, Snackbar.LENGTH_LONG).show();

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestVideoPermissions() {
        if (Utils.shouldShowRequestPermissionRationale(this, Constants.STORAGE_PERMISSIONS)) {
            new PermissionConfirmationDialog().newInstance(getString(R.string.storage_permission_request),
                    Constants.REQUEST_STORAGE_PERMISSIONS, Constants.STORAGE_PERMISSIONS)
                    .show(getChildFragmentManager(), PermissionConfirmationDialog.FRAGMENT_DIALOG);
            return;
        }
        requestPermissions(Constants.STORAGE_PERMISSIONS, Constants.REQUEST_STORAGE_PERMISSIONS);

    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.REQUEST_STORAGE_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecordingVideo();
                }
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoRecyclerView.setAdapter(null);
    }
}

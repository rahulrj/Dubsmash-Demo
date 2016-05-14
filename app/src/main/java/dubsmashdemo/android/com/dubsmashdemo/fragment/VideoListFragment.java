package dubsmashdemo.android.com.dubsmashdemo.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import dubsmashdemo.android.com.dubsmashdemo.adapter.VideoFilesAdapter;
import dubsmashdemo.android.com.dubsmashdemo.async.DbFetcherTask;
import dubsmashdemo.android.com.dubsmashdemo.db.MySQLiteHelper;
import dubsmashdemo.android.com.dubsmashdemo.interfaces.LoaderListener;
import dubsmashdemo.android.com.dubsmashdemo.model.VideoObject;
import dubsmashdemo.android.com.dubsmashdemo.utils.Constants;
import dubsmashdemo.android.com.dubsmashdemo.utils.Util;

/**
 * Created by rahul.raja on 5/13/16.
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
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissionsGranted(Constants.VIDEO_PERMISSIONS)) {
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
            if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Constants.RECORDING_MAX_DURATION);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                mVideoFileName = Constants.VIDEO_NAME_INIT + new SimpleDateFormat(Constants.VIDEO_DATE_FORMAT, Locale.US).format(new Date());
                File videoDir = Util.getVideoDirectory(getActivity());
                File videoFile = null;
                if (videoDir != null) {
                    try {
                        videoFile = File.createTempFile(mVideoFileName, Constants.VIDEO_EXTENSION, videoDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (videoFile != null) {
                        Uri videoUri = Uri.fromFile(videoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                        startActivityForResult(intent, Constants.VIDEO_CAPTURE);
                    }
                } else {
                    showSnackMessage("Failed to create directory");
                }

            } else {
                showSnackMessage("No camera on device");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.VIDEO_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(mFragmentRootView, "Video has been saved as : " + mVideoFileName, Snackbar.LENGTH_LONG).show();
                VideoObject videoObject = new VideoObject(mVideoFileName, data.getDataString());
                saveVideoPathInDb(videoObject);
                mAllVideoFiles.add(videoObject);
                if (mVideoFilesAdapter != null) {
                    updateRecyclerViewAdapter();
                } else {
                    setDataInAdapter();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                showSnackMessage("Video recording cancelled");
            } else {
                showSnackMessage("Failed to record video");
            }
        }
    }


    private void saveVideoPathInDb(VideoObject videoObject) {
        MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
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


    @TargetApi(23)
    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (getActivity().checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    @TargetApi(23)
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(23)
    private void requestVideoPermissions() {
        if (shouldShowRequestPermissionRationale(Constants.VIDEO_PERMISSIONS)) {
            new PermissionConfirmationDialog().show(getChildFragmentManager(), PermissionConfirmationDialog.FRAGMENT_DIALOG);
            return;
        }
        requestPermissions(Constants.VIDEO_PERMISSIONS, Constants.REQUEST_VIDEO_PERMISSIONS);

    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.REQUEST_VIDEO_PERMISSIONS:
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

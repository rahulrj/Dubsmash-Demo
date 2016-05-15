package dubsmashdemo.android.com.dubsmashdemo.utils;

import android.Manifest;

/**
 * Created by rahul.raja on 5/14/16.
 */
public class Constants {

    public static final int RECORDING_MAX_DURATION = 20000;
    public static final int VIDEO_CAPTURE = 201;
    public static final int VIDEO_CAPTURE_CAMERA = 101;


    public static final String VIDEO_NAME_INIT = "VID_";
    public static final String VIDEO_DATE_FORMAT = "yyyyMMdd_hhmmss";
    public static final String VIDEO_EXTENSION = ".mp4";
    public static final String KEY_VIDEO_PATH = "video_path";
    public static final String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_STORAGE_PERMISSIONS = 1;
    public static final int REQUEST_CAMERA_PERMISSIONS = 1;
    public static final String KEY_VIDEO_FILE_NAME = "video_file";
    public static final String KEY_VIDEO_ABS_PATH = "video_path";
    public static final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,};

}

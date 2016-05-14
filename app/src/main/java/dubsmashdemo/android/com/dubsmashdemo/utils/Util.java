package dubsmashdemo.android.com.dubsmashdemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.google.android.exoplayer.ExoPlayerLibraryInfo;

import java.io.File;

import dubsmashdemo.android.com.dubsmashdemo.R;

/**
 * Created by rahul.raja on 5/14/16.
 */
public class Util {

    /**
     * Returns a user agent string based on the given application name and the library version.
     *
     * @param context         A valid context of the calling application.
     * @param applicationName String that will be prefix'ed to the generated user agent.
     * @return A user agent string generated using the applicationName and the library version.
     */
    public static String getUserAgent(Context context, String applicationName) {
        String versionName;
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "?";
        }
        return applicationName + "/" + versionName + " (Linux;Android " + Build.VERSION.RELEASE
                + ") " + "ExoPlayerLib/" + ExoPlayerLibraryInfo.VERSION;
    }

    /**
     * @returns a directory sd-card-path/Dubsmash-Demo
     */
    public static File getVideoDirectory(Context context) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = getAlbumStorageDir(getAlbumName(context));
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        return null;
                    }
                }
            }

        } else {
            return null;
        }

        return storageDir;
    }

    private static File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStorageDirectory()
                        + "/" + albumName
        );
    }

    private static String getAlbumName(Context context) {
        return context.getString(R.string.app_name);
    }
}

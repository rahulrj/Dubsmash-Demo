package dubsmashdemo.android.com.dubsmashdemo.model;

/**
 * Created by rahul.raja on 5/14/16.
 */
public class VideoObject {

    private String mVideoName;
    private String mVideoPath;

    public VideoObject(String videoName, String videoPath) {
        this.mVideoName = videoName;
        this.mVideoPath = videoPath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoPath(String videoPath) {
        this.mVideoPath = videoPath;
    }

    public String getVideoName() {
        return mVideoName;
    }

    public void setVideoName(String videoName) {
        this.mVideoName = videoName;
    }

    @Override
    public String toString() {
        return "VideoObject{" +
                "mVideoName='" + mVideoName + '\'' +
                ", mVideoPath='" + mVideoPath + '\'' +
                '}';
    }
}

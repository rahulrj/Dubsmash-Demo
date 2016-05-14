package dubsmashdemo.android.com.dubsmashdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import dubsmashdemo.android.com.dubsmashdemo.model.VideoObject;

/**
 * Created by rahul.raja on 5/14/16.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "VideoDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PHOTO_TABLE = "CREATE TABLE videos ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT," +
                "path TEXT ) ";


        // create  table
        db.execSQL(CREATE_PHOTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older photos table if existed
        db.execSQL("DROP TABLE IF EXISTS videos");
        this.onCreate(db);
    }

    private static final String TABLE_VIDEOS = "videos";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PATH = "path";


    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_PATH};

    public void addVideoDetails(VideoObject videoObject) {
        Log.d("addVideo", videoObject.toString());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, videoObject.getVideoName());
        values.put(KEY_PATH, videoObject.getVideoPath());

        db.insert(TABLE_VIDEOS, null, values); // key/value -> keys = column names/ values = column values
        db.close();
    }

    public ArrayList<VideoObject> getAllVideoDetails() {
        ArrayList<VideoObject> videos = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE_VIDEOS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String videoName = cursor.getString(1);
                String videoPath = cursor.getString(2);
                videos.add(new VideoObject(videoName, videoPath));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Log.d("getAllVideos()", videos.toString());
        return videos;
    }


}


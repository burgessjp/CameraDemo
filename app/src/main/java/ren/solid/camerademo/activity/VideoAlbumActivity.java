package ren.solid.camerademo.activity;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by _SOLID
 * Date:2016/3/28
 * Time:15:20
 */
public class VideoAlbumActivity extends AppCompatActivity {
    private final static String TAG = "VideoAlbumTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver contentResolver = getContentResolver();
        String[] projection = new String[]{MediaStore.Video.Media.TITLE, MediaStore.Video.Media.MINI_THUMB_MAGIC, MediaStore.Video.Media.DATA};
        Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();

        for (int counter = 0; counter < fileNum; counter++) {
            Log.e("tag", "---file is:" +
                    cursor.getString(cursor
                            .getColumnIndex(MediaStore.Video.Media.TITLE)));
            Log.e("tag", "---thumb is:" +
                    cursor.getString(cursor
                            .getColumnIndex(MediaStore.Video.Media.DATA)));
            cursor.moveToNext();
        }
        //ThumbnailUtils.createVideoThumbnail()
    }
}

package ren.solid.camerademo.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ren.solid.camerademo.R;
import ren.solid.camerademo.adapter.ImageSelectAdapter;

/**
 * Created by _SOLID
 * Date:2016/3/24
 * Time:10:54
 */
public class PhotoAlbumActivity extends AppCompatActivity {
    private static String TAG = "ren.solid.camerademo.activity.PhotoAlbumActivity";

    private ArrayList<String> mImagePath;
    private ImageSelectAdapter mAdapter;
    private RecyclerView mRvImageAlbum;
    private Button mBtnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);
        mImagePath = new ArrayList<>();
        initView();
        getImages();
    }

    private void initView() {
        mBtnComplete = (Button) findViewById(R.id.btn_complete);
        mRvImageAlbum =
                (RecyclerView) findViewById(R.id.rv_image_album);
        mAdapter = new ImageSelectAdapter(this, mImagePath);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRvImageAlbum.setLayoutManager(layoutManager);
        mRvImageAlbum.setAdapter(mAdapter);
        mAdapter.setOnCheckListener(new ImageSelectAdapter.OnCheckListener() {
            @Override
            public void onCheck(String text, int currentSelectCount) {
                mBtnComplete.setText(text);
            }
        });
    }

    public void getImages() {

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = PhotoAlbumActivity.this
                        .getContentResolver();

                // 只查询jpeg和png的图片,按时间降序
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");

                Log.e(TAG, mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    long date_modified = mCursor.getLong(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                    String display_name = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    String title = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.TITLE));
                    //  Log.e(TAG, path);
                    mImagePath.add(path);
                    File parentFileDir = new File(path).getParentFile();
                    //  Log.e(TAG, parentFileDir.getAbsolutePath());

                }
                mCursor.close();
                PhotoAlbumActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mBtnComplete.setText(ImageSelectAdapter.getCurrentSelect());
                    }
                });

            }

        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}

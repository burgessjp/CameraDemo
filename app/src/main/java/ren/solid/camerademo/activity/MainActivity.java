package ren.solid.camerademo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ren.solid.camerademo.R;
import ren.solid.camerademo.view.CameraSurfaceView;

public class MainActivity extends AppCompatActivity {


    private Bundle bundle = null; // 声明一个Bundle对象，用来存储数据
    private CameraSurfaceView mCameraSurfaceView;
    private ImageView mIvPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 显示界面
        setContentView(R.layout.activity_main);
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mIvPhoto = (ImageView) findViewById(R.id.iv_photo);
        mCameraSurfaceView.setOnSavePictureListener(new CameraSurfaceView.OnSavePictureListener() {
            @Override
            public void onSuccess(String filePath) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                mIvPhoto.setImageBitmap(bitmap);

            }
        });


    }

    /**
     * 按钮被点击触发的事件
     *
     * @param v
     */
    public void btnOnclick(View v) {
        mCameraSurfaceView.takePicture();
    }

    public void changeCameraFacing(View v) {
        mCameraSurfaceView.changCameraFacing();
    }

    public void openPhotoAlbum(View v) {
        startActivity(new Intent(this, PhotoAlbumActivity.class));
    }

    public void ivSwitchClick(View v) {

        startActivity(new Intent(this, VideoRecorderActivity.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA: // 按下拍照按钮

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraSurfaceView.onPause();
    }
}

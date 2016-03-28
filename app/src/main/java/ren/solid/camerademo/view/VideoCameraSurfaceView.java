package ren.solid.camerademo.view;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by _SOLID
 * Date:2016/3/23
 * Time:9:43
 * <p/>
 * <uses-permission android:name="android.permission.CAMERA" />
 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <p/>
 * 注：应用程序同时只能存在一个激活的 mCamera
 * <p/>
 */
public class VideoCameraSurfaceView extends SurfaceView {
    private static String TAG = "ren.solid.camerademo.view.CameraSurfaceView";
    private SurfaceHolder mSurfaceHolder;
    private MediaRecorder mMediaRecorder;// 录制视频的类
    private Camera mCamera;
    private Camera.Parameters parameters = null;
    private Context mContext;
    private int mCameraCount;
    private int mCurrentCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    private String mVideoSaveDir;
    private String mSavePath;
    private OnSaveListener mOnSavePictureListener;

    private int mWidth;
    private int mHeight;

    public VideoCameraSurfaceView(Context context) {
        this(context, null);
    }

    public VideoCameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoCameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    private void init() {
        mCameraCount = Camera.getNumberOfCameras();//得到摄像头数量
        mSurfaceHolder = getHolder();
        getCamera();
        mSurfaceHolder.setKeepScreenOn(true);// 屏幕常亮
        mSurfaceHolder.addCallback(new SurfaceCallback());//为SurfaceView的Holder添加一个回调函数
    }

    /***
     * 得到系统相机
     */
    private void getCamera() {
        if (mCamera == null)
            mCamera = Camera.open(mCurrentCameraFacing); // 打开后置摄像头
    }


    private final class SurfaceCallback implements SurfaceHolder.Callback {

        // 拍照状态变化时调用该方法
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.i(TAG, "surfaceChanged    " + "width:" + width + "|" + "height:" + height);
            mWidth = width;
            mHeight = height;
            if (mCamera != null) {
                mCamera.stopPreview();
                startPreview(holder);
                // setCameraParameters();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated");
            startPreview(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed");
            releaseCamera();
        }

    }

    /***
     * 切换相机摄像头
     */
    public void changCameraFacing() {
        if (mCameraCount > 1) {
            mCurrentCameraFacing = (mCurrentCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                    Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
            releaseCamera();
            startPreview(mSurfaceHolder);
        } else {
            //手机不支持前置摄像头
        }
    }

    /***
     * 设置相机参数
     */
    private void setCameraParameters() {
        mSurfaceHolder.setFixedSize(mWidth, mHeight);//照片的大小
        parameters = mCamera.getParameters(); // 获取相机参数
        parameters.setPictureFormat(ImageFormat.JPEG); // 设置图片格式

        parameters.setPreviewSize(mWidth, mHeight); // 设置预览大小
        parameters.setPictureSize(mWidth, mHeight); // 设置保存的图片尺寸

        parameters.setPreviewFpsRange(4, 10);//fps
        parameters.setJpegQuality(100); // 设置照片质量
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//自动对焦
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//连续对焦
        //mCamera.cancelAutoFocus();//如果要实现连续的自动对焦，这一句必须加上
        mCamera.setParameters(parameters);
    }

    /**
     * 开始预览
     */
    private void startPreview(SurfaceHolder surfaceHolder) {
        getCamera();
        try {
            mCamera.setPreviewDisplay(surfaceHolder); // 设置用于显示预览的SurfaceHolder对象
            mCamera.setDisplayOrientation(getPreviewDegree());
            mCamera.startPreview(); // 开始预览
            mCamera.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecord() throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null)
            mMediaRecorder.setCamera(mCamera);
        //mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频格式
        mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率：
        // mMediaRecorder.setVideoFrameRate(16);// 这个我把它去掉了，感觉没什么用
        mMediaRecorder.setVideoEncodingBitRate(10 * 1024 * 512);// 设置帧频率，然后就清晰了
        if (mCurrentCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        } else {//在用前置摄像头录制的时候需要旋转270度，不然会导致录制的视频是反向的
            mMediaRecorder.setOrientationHint(270);// 输出旋转270度，保持竖屏录制
        }
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);// 视频录制格式
        // mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);
        File dir = new File(getVideoSaveDir());
        if (!dir.exists()) dir.mkdir();

        mSavePath = getVideoSaveDir() + File.separator + generateFileName();

        mMediaRecorder.setOutputFile(mSavePath);
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startRecord() {
        if (mCamera == null) getCamera();
        try {
            initRecord();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mOnSavePictureListener != null) mOnSavePictureListener.onSuccess(mSavePath);
            MediaScannerConnection.scanFile(mContext, new String[]{
                            mSavePath},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            // Log.e(TAG, "扫描完成");
                        }
                    });
        }
    }

    /***
     * 释放相机资源
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 用于根据手机方向获得相机预览画面旋转的角度
     */
    private int getPreviewDegree() {
        // 获得手机的方向
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay()
                .getRotation();
        Log.i(TAG, "rotation:" + rotation);
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    /**
     * 设置保存路径
     *
     * @param pictureSavePath
     */
    public void setVideoSavePath(String pictureSavePath) {
        mVideoSaveDir = pictureSavePath;
    }

    /***
     * 得到保存的目录
     *
     * @return
     */
    public String getVideoSaveDir() {
        String path;
        if (mVideoSaveDir == null)
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                path = Environment.getExternalStorageDirectory()
                        + "/SolidCamera/";
            else {
                path = mContext.getCacheDir().getAbsolutePath()
                        + "/SolidCamera/";
            }
        else {
            path = mVideoSaveDir;
        }
        mVideoSaveDir = path;
        return path;
    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".mp4";
    }


    public void setOnSaveListener(OnSaveListener onSavePictureListener) {
        mOnSavePictureListener = onSavePictureListener;
    }

    public interface OnSaveListener {
        void onSuccess(String filePath);
    }

    public String getVideoSavePath() {
        return mSavePath;
    }

    public void onResume() {
        getCamera();
    }

    public void onPause() {
        releaseCamera();
    }


}

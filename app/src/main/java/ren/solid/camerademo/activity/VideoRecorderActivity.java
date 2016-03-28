package ren.solid.camerademo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ren.solid.camerademo.R;
import ren.solid.camerademo.view.VideoCameraSurfaceView;

/**
 * Created by _SOLID
 * Date:2016/3/28
 * Time:10:53
 */
public class VideoRecorderActivity extends AppCompatActivity implements Runnable {

    private final static String TAG = "VideoRecorderTAG";
    private final static int STATE_RECORDING = 1;
    private final static int STATE_NORMAL = 0;


    private VideoCameraSurfaceView mVideoCameraSurfaceView;
    private ImageView mIvRecorder;
    private TextView mTvRecorderTime;

    private int mCurrentState = STATE_NORMAL;
    private boolean mIsRecorder = false;
    private int mTotalSecond = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);
        mVideoCameraSurfaceView = (VideoCameraSurfaceView) findViewById(R.id.videocameraSurfaceView);
        mIvRecorder = (ImageView) findViewById(R.id.iv_record);
        mTvRecorderTime =
                (TextView) findViewById(R.id.tv_recorder_time);

        mVideoCameraSurfaceView.setOnSaveListener(new VideoCameraSurfaceView.OnSaveListener() {
            @Override
            public void onSuccess(String filePath) {
                Toast.makeText(getApplicationContext(), "文件已保存至:" + filePath, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void btnOnclick(View v) {
        if (mCurrentState == STATE_NORMAL) {
            mCurrentState = STATE_RECORDING;
            mIvRecorder.setImageResource(R.drawable.icon_camera_pause);
            mVideoCameraSurfaceView.startRecord();
            mIsRecorder = true;
            new Thread(this).start();
            Log.i(TAG, "start");
        } else {
            mCurrentState = STATE_NORMAL;
            mVideoCameraSurfaceView.stopRecord();
            mIvRecorder.setImageResource(R.drawable.btn_clue_video);
            mTotalSecond = 0;
            mIsRecorder = false;
            Log.i(TAG, "stop");
        }
    }

    public void btnSwitchOnclick(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void changeCameraFacing(View v) {
        mVideoCameraSurfaceView.changCameraFacing();
    }

    public void openVideos(View v) {
        startActivity(new Intent(this, VideoAlbumActivity.class));
    }

    public String getShowTime(int countTime) {

        String result = "";
        if (countTime < 10)
            result = "00:0" + countTime;
        else if (countTime < 60)
            result = "00:" + countTime;
        else {
            int minute = countTime / 60;
            int mod = countTime % 60;
            if (minute < 10) result += "0" + minute + ":";
            else {
                result += minute + ":";
            }
            if (mod < 10) result += "0" + mod;
            else {
                result += mod;
            }

        }
        return result;
    }

    @Override
    public void run() {
        while (mIsRecorder) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mTotalSecond++;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvRecorderTime.setText(getShowTime(mTotalSecond));
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoCameraSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoCameraSurfaceView.onPause();
    }
}

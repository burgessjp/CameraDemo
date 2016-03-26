package ren.solid.camerademo.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

import ren.solid.camerademo.R;
import ren.solid.camerademo.adapter.ImageSelectAdapter;
import ren.solid.camerademo.adapter.PictureViewPagerAdapter;

/**
 * Created by _SOLID
 * Date:2016/3/25
 * Time:9:29
 */
public class ViewPictureActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private CheckBox mCbSelect;
    private PictureViewPagerAdapter mAdapter;
    private int mPostion;
    private ArrayList<String> mImagePaths;
    private Button mBtnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);
        mImagePaths = getIntent().getStringArrayListExtra("imagePaths");
        mPostion = getIntent().getIntExtra("position", 1) - 1;
        mAdapter = new PictureViewPagerAdapter(this, mImagePaths);
        initView();
    }

    private void initView() {

        mBtnSelect =
                (Button) findViewById(R.id.btn_complete);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mCbSelect =
                (CheckBox) findViewById(R.id.cb_select);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPostion);
        setCheckState(mPostion);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCheckState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mCbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("mCbSelect", "onclick");
                int curPosition = mViewPager.getCurrentItem();
                changeState(curPosition);

            }
        });
        mCbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeState(mViewPager.getCurrentItem());
            }
        });
    }

    private void setCheckState(int position) {
        if (ImageSelectAdapter.mSelectImage.contains(mImagePaths.get(position))) {
            mCbSelect.setChecked(true);
        } else {
            mCbSelect.setChecked(false);
        }
        mBtnSelect.setText(ImageSelectAdapter.getCurrentSelect());
    }

    public void changeState(int position) {

        if (mCbSelect.isChecked()) {
            Log.e("mCbSelect", "Checked");
            if (!ImageSelectAdapter.checkOverMaxSelectCount(ViewPictureActivity.this)) {
                if (!ImageSelectAdapter.mSelectImage.contains(mImagePaths.get(position))) {
                    ImageSelectAdapter.mSelectImage.add(mImagePaths.get(position));
                }
            } else {
                mCbSelect.setChecked(false);
            }

        } else {
            Log.e("mCbSelect", "not Checked");
            if (ImageSelectAdapter.mSelectImage.contains(mImagePaths.get(position))) {
                ImageSelectAdapter.mSelectImage.remove(mImagePaths.get(position));
            }
        }
        mBtnSelect.setText(ImageSelectAdapter.getCurrentSelect());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

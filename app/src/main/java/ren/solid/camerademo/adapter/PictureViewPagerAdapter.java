package ren.solid.camerademo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ren.solid.camerademo.utils.ImageLoader;

/**
 * Created by _SOLID
 * Date:2016/3/25
 * Time:9:42
 */
public class PictureViewPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private final List<String> mImageList;

    public PictureViewPagerAdapter(Context context, List<String> imageList) {
        this.mContext = context;
        this.mImageList = imageList;
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        ImageLoader.getInstance().loadImage(mImageList.get(position), imageView);
        ((ViewPager) container).addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}

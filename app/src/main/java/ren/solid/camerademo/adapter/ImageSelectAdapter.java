package ren.solid.camerademo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ren.solid.camerademo.R;
import ren.solid.camerademo.activity.MainActivity;
import ren.solid.camerademo.activity.ViewPictureActivity;
import ren.solid.camerademo.utils.ImageLoader;

/**
 * Created by _SOLID
 * Date:2016/3/24
 * Time:16:29
 */
public class ImageSelectAdapter extends RecyclerView.Adapter<ImageSelectAdapter.ImageHolder> {

    public static List<String> mSelectImage = new ArrayList<>();
    private static int mMaxSelectCount = 9;

    private Context mContext;
    private ArrayList<String> mImagePaths;
    private OnCheckListener mOnCheckListener;

    public ImageSelectAdapter(Context ctx, ArrayList<String> imagePaths) {
        mContext = ctx;
        mImagePaths = imagePaths;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image_grid, parent, false);
        ImageHolder holder = new ImageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, final int position) {
        if (position == 0) {
            holder.iv_image.setImageResource(R.drawable.btn_photo_picker);
            holder.iv_select.setVisibility(View.GONE);
            holder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext, "您点击了拍照", Toast.LENGTH_SHORT).show();
                    mSelectImage.clear();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                }
            });
        } else {
            holder.iv_select.setVisibility(View.VISIBLE);
            holder.iv_select.setImageResource(R.drawable.btn_media_unchecked);
            holder.iv_image.setImageResource(R.drawable.bg_photo_done);
            final String imagePath = mImagePaths.get(position - 1);
            holder.iv_image.setColorFilter(null);

            ImageLoader.getInstance().loadImage(imagePath, holder.iv_image);
            holder.iv_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectImage.contains(imagePath)) {//当前选择的图片已被加入已选择图片列表中
                        mSelectImage.remove(imagePath);
                        holder.iv_image.setColorFilter(null);
                        holder.iv_select.setImageResource(R.drawable.btn_media_unchecked);
                    } else {//当前选择的图片还没被加入已选择图片列表中
                        if (!checkOverMaxSelectCount(mContext)) {
                            mSelectImage.add(imagePath);
                            holder.iv_image.setColorFilter(Color.parseColor("#77000000"));
                            holder.iv_select.setImageResource(R.drawable.btn_media_checked);
                        }
                    }
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(getCurrentSelect(), mSelectImage.size());
                }
            });
            holder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ViewPictureActivity.class);
                    intent.putExtra("imageList", mImagePaths);
                    intent.putStringArrayListExtra("imagePaths", mImagePaths);
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
                }
            });

            if (mSelectImage.contains(imagePath)) {
                holder.iv_image.setColorFilter(Color.parseColor("#77000000"));
                holder.iv_select.setImageResource(R.drawable.btn_media_checked);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mImagePaths.size() + 1;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView iv_image;
        public ImageView iv_select;

        public ImageHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            iv_select = (ImageView) itemView.findViewById(R.id.iv_select);
        }
    }


    public interface OnCheckListener {

        void onCheck(String text, int currentSelectCount);

    }

    /***
     * 检测是否超过了最大选择张数
     *
     * @return
     */
    public static boolean checkOverMaxSelectCount(Context ctx) {
        if (mSelectImage.size() >= mMaxSelectCount) {
            Toast.makeText(ctx.getApplicationContext(), "最多只能选" + mMaxSelectCount + "张图片哦", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到当前的选择情况
     *
     * @return
     */
    public static String getCurrentSelect() {
        return "完成(" + mSelectImage.size() + "/" + mMaxSelectCount + ")";
    }

    /***
     * 设置选择监听器
     *
     * @param onCheckListener
     */
    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.mOnCheckListener = onCheckListener;
    }
}

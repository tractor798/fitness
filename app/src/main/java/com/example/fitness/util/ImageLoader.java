package com.example.fitness.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.fitness.R;

/**
 * 图片加载工具类
 * 支持直接加载HTML中的图片链接及本地资源
 */
public class ImageLoader {

    /**
     * 加载网络图片
     * @param context 上下文
     * @param imageUrl 图片URL（支持HTML中的链接）
     * @param imageView 目标ImageView
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        if (context == null || imageView == null) return;
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.bg_exercise_thumbnail);
            return;
        }

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.bg_exercise_thumbnail)
                        .error(R.drawable.bg_exercise_thumbnail)
                        .centerCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * 加载圆形图片（如头像）
     */
    public static void loadCircleImage(Context context, String imageUrl, ImageView imageView) {
        if (context == null || imageView == null) return;

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.img_user_profile);
            return;
        }

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.img_user_profile)
                        .error(R.drawable.img_user_profile)
                        .circleCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
}
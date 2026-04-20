package com.example.fitness.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * 图片加载工具类
 * 支持直接加载HTML中的图片链接
 */
public class ImageLoader {

    /**
     * 加载网络图片
     * @param context 上下文
     * @param imageUrl 图片URL（支持HTML中的链接）
     * @param imageView 目标ImageView
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .centerCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * 加载圆形图片（如头像）
     */
    public static void loadCircleImage(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .circleCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
}

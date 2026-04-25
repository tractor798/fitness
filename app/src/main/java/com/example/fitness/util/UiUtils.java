package com.example.fitness.util;

import android.content.Context;
import android.widget.Toast;

/**
 * UI 辅助工具类
 * 提供通用的 UI 操作快捷方式
 */
public class UiUtils {

    /**
     * 显示短时间的 Toast
     */
    public static void showShortToast(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示长时间的 Toast
     */
    public static void showLongToast(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
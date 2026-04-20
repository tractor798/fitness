package com.example.fitness.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 圆形进度条View
 * 实现类似HTML中的环形进度条效果
 */
public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Paint subTextPaint;

    private RectF circleBounds;
    
    private float progress = 0; // 0-100
    private float strokeWidth = 20f;
    private int backgroundColor = 0xFFE6E8EA; // surface-container
    private int progressColor = 0xFF8B4B00; // primary
    
    // 文字相关
    private String mainText = "8,432";
    private String subText = "今日步数";
    private int mainTextColor = 0xFF2C2F30; // on-surface
    private int subTextColor = 0xFF595C5D; // on-surface-variant
    private float mainTextSize = 72f;
    private float subTextSize = 14f;

    // 动画
    private ValueAnimator animator;
    private float animatedProgress = 0;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 背景圆环
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStrokeWidth(strokeWidth);

        // 进度圆环
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(strokeWidth);

        // 主文字
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(mainTextColor);
        textPaint.setTextSize(mainTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.create("sans-serif-black", android.graphics.Typeface.BOLD));

        // 副文字
        subTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        subTextPaint.setColor(subTextColor);
        subTextPaint.setTextSize(subTextSize);
        subTextPaint.setTextAlign(Paint.Align.CENTER);
        subTextPaint.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));

        circleBounds = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        float padding = strokeWidth / 2 + 10;
        circleBounds.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // 绘制背景圆环
        canvas.drawArc(circleBounds, 0, 360, false, backgroundPaint);

        // 绘制进度圆环（从顶部开始，顺时针）
        float sweepAngle = (animatedProgress / 100f) * 360f;
        canvas.drawArc(circleBounds, -90, sweepAngle, false, progressPaint);

        // 绘制主文字
        float centerY = circleBounds.centerY();
        canvas.drawText(mainText, circleBounds.centerX(), centerY + mainTextSize / 3, textPaint);

        // 绘制副文字
        canvas.drawText(subText, circleBounds.centerX(), centerY + mainTextSize / 3 + subTextSize + 20, subTextPaint);
    }

    /**
     * 设置进度（带动画）
     */
    public void setProgressWithAnimation(float progress, long duration) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        this.progress = progress;
        animator = ValueAnimator.ofFloat(animatedProgress, progress);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animatedProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    /**
     * 设置进度（无动画）
     */
    public void setProgress(float progress) {
        this.progress = progress;
        this.animatedProgress = progress;
        invalidate();
    }

    /**
     * 设置圆环宽度
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        backgroundPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeWidth(strokeWidth);
        requestLayout();
    }

    /**
     * 设置背景颜色
     */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        backgroundPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置进度颜色
     */
    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置主文字
     */
    public void setMainText(String text) {
        this.mainText = text;
        invalidate();
    }

    /**
     * 设置副文字
     */
    public void setSubText(String text) {
        this.subText = text;
        invalidate();
    }

    /**
     * 设置主文字颜色
     */
    public void setMainTextColor(int color) {
        this.mainTextColor = color;
        textPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置副文字颜色
     */
    public void setSubTextColor(int color) {
        this.subTextColor = color;
        subTextPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置主文字大小
     */
    public void setMainTextSize(float size) {
        this.mainTextSize = size;
        textPaint.setTextSize(size);
        invalidate();
    }

    /**
     * 设置副文字大小
     */
    public void setSubTextSize(float size) {
        this.subTextSize = size;
        subTextPaint.setTextSize(size);
        invalidate();
    }
}

package com.example.fitness.ui.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
 * 周趋势柱状图View
 * 实现类似HTML中的周活动趋势图表
 */
public class WeeklyTrendChartView extends View {

    private Paint barPaint;
    private Paint activeBarPaint;
    private Paint labelPaint;
    
    private float[] data = new float[7]; // 7天的数据（0-100）
    private int activeDay = 3; // 当前激活的天（周四是索引3）
    
    private int barColor = 0xFFE6E8EA; // surface-container
    private int activeBarColor = 0xFF8B4B00; // primary

    private float barWidth = 0;
    private float barSpacing = 24f;
    private float cornerRadius = 20f;
    private float labelSize = 20f;
    
    private final String[] dayLabels = {"一", "二", "三", "四", "五", "六", "日"};
    
    // 动画
    private ValueAnimator animator;
    private float[] animatedData = new float[7];

    public WeeklyTrendChartView(Context context) {
        super(context);
        init();
    }

    public WeeklyTrendChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeeklyTrendChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 普通柱子
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(barColor);
        barPaint.setStyle(Paint.Style.FILL);

        // 激活的柱子
        activeBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activeBarPaint.setColor(activeBarColor);
        activeBarPaint.setStyle(Paint.Style.FILL);

        // 标签
        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // on-surface-variant
        int labelColor = 0xFF595C5D;
        labelPaint.setColor(labelColor);
        labelPaint.setTextSize(labelSize);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));

        // 初始化为0
        for (int i = 0; i < 7; i++) {
            data[i] = 50;
            animatedData[i] = 0;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // 计算柱子宽度
        float totalSpacing = barSpacing * 6; // 6个间隔
        barWidth = (w - totalSpacing) / 7;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float chartHeight = getHeight() - labelSize - 30; // 留出标签空间
        float startY = 0;

        for (int i = 0; i < 7; i++) {
            float left = i * (barWidth + barSpacing);
            float barHeight = (animatedData[i] / 100f) * chartHeight;
            float top = startY + (chartHeight - barHeight);
            
            @SuppressLint("DrawAllocation") RectF barRect = new RectF(left, top, left + barWidth, startY + chartHeight);
            
            // 选择颜色
            Paint paint = (i == activeDay) ? activeBarPaint : barPaint;
            
            // 绘制圆角矩形
            canvas.drawRoundRect(barRect, cornerRadius, cornerRadius, paint);
            
            // 绘制标签
            float labelY = startY + chartHeight + labelSize + 15;
            canvas.drawText(dayLabels[i], left + barWidth / 2, labelY, labelPaint);
        }
    }

    /**
     * 设置数据（带动画）
     */
    public void setDataWithAnimation(float[] data, long duration) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        this.data = data;
        
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            for (int i = 0; i < 7; i++) {
                animatedData[i] = data[i] * fraction;
            }
            invalidate();
        });
        animator.start();
    }

    /**
     * 设置数据（无动画）
     */
    public void setData(float[] data) {
        this.data = data;
        this.animatedData = data.clone();
        invalidate();
    }

    /**
     * 设置激活的天
     */
    public void setActiveDay(int day) {
        this.activeDay = day;
        invalidate();
    }

    /**
     * 设置柱子颜色
     */
    public void setBarColor(int color) {
        this.barColor = color;
        barPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置激活柱子颜色
     */
    public void setActiveBarColor(int color) {
        this.activeBarColor = color;
        activeBarPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置柱子间距
     */
    public void setBarSpacing(float spacing) {
        this.barSpacing = spacing;
        requestLayout();
    }

    /**
     * 设置圆角半径
     */
    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        invalidate();
    }

    /**
     * 设置标签大小
     */
    public void setLabelSize(float size) {
        this.labelSize = size;
        labelPaint.setTextSize(size);
        requestLayout();
    }
}

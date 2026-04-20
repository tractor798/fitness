package com.example.fitness.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 用于显示体重趋势的折线图。
 */
public class LineChartView extends View {

    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float[] data = new float[]{75.5f, 75.2f, 74.8f, 74.2f};
    private String[] labels = new String[]{"第一周", "第二周", "第三周", "本周"};

    private final float pointRadius = 8f;
    private final float labelSize = 24f;
    private float animationProgress = 1f;
    private ValueAnimator animator;

    public LineChartView(Context context) {
        super(context);
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint.setColor(0xFFB51A1A);
        linePaint.setStyle(Paint.Style.STROKE);
        float strokeWidth = 6f;
        linePaint.setStrokeWidth(strokeWidth);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint.setColor(0x33B51A1A);
        fillPaint.setStyle(Paint.Style.FILL);

        pointPaint.setColor(0xFFB51A1A);
        pointPaint.setStyle(Paint.Style.FILL);

        labelPaint.setColor(0xFF5B403D);
        labelPaint.setTextSize(labelSize);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.length == 0) {
            return;
        }

        float width = getWidth();
        float chartHeight = getHeight() - labelSize - 30f;
        float padding = 20f;

        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;
        for (float value : data) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        float range = max - min;
        if (range <= 0f) {
            range = 1f;
        }

        PointF[] points = buildPoints(width, chartHeight, padding, min, range);
        if (points.length == 1) {
            drawSinglePoint(canvas, points[0], chartHeight);
        } else {
            drawFill(canvas, points, chartHeight);
            drawLine(canvas, points);
            drawPoints(canvas, points);
        }
        drawLabels(canvas, width, chartHeight, padding);
    }

    private PointF[] buildPoints(float width, float baseline, float padding, float min, float range) {
        PointF[] points = new PointF[data.length];
        for (int i = 0; i < data.length; i++) {
            float x;
            if (data.length == 1) {
                x = width / 2f;
            } else {
                x = padding + (width - 2f * padding) * i / (data.length - 1f);
            }
            float normalizedY = (data[i] - min) / range;
            float targetY = baseline - (baseline * normalizedY);
            float y = baseline - (baseline - targetY) * animationProgress;
            points[i] = new PointF(x, y);
        }
        return points;
    }

    private void drawSinglePoint(Canvas canvas, PointF point, float chartHeight) {
        canvas.drawLine(point.x, chartHeight, point.x, point.y, linePaint);
        canvas.drawCircle(point.x, point.y, pointRadius, pointPaint);
    }

    private void drawFill(Canvas canvas, PointF[] points, float chartHeight) {
        Path fillPath = new Path();
        fillPath.moveTo(points[0].x, chartHeight);
        fillPath.lineTo(points[0].x, points[0].y);
        for (int i = 0; i < points.length - 1; i++) {
            float cx = (points[i].x + points[i + 1].x) / 2f;
            fillPath.quadTo(points[i].x, points[i].y, cx, (points[i].y + points[i + 1].y) / 2f);
        }
        fillPath.lineTo(points[points.length - 1].x, chartHeight);
        fillPath.close();
        canvas.drawPath(fillPath, fillPaint);
    }

    private void drawLine(Canvas canvas, PointF[] points) {
        Path linePath = new Path();
        linePath.moveTo(points[0].x, points[0].y);
        for (int i = 0; i < points.length - 1; i++) {
            float cx = (points[i].x + points[i + 1].x) / 2f;
            linePath.quadTo(points[i].x, points[i].y, cx, (points[i].y + points[i + 1].y) / 2f);
        }
        linePath.lineTo(points[points.length - 1].x, points[points.length - 1].y);
        canvas.drawPath(linePath, linePaint);
    }

    private void drawPoints(Canvas canvas, PointF[] points) {
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, pointRadius, pointPaint);
        }
    }

    private void drawLabels(Canvas canvas, float width, float chartHeight, float padding) {
        float labelY = chartHeight + labelSize + 15f;
        int count = Math.min(labels.length, data.length);
        for (int i = 0; i < count; i++) {
            float x;
            if (count == 1) {
                x = width / 2f;
            } else {
                x = padding + (width - 2f * padding) * i / (count - 1f);
            }
            canvas.drawText(labels[i], x, labelY, labelPaint);
        }
    }

    public void setDataWithAnimation(float[] newData, long duration) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        data = (newData == null || newData.length == 0) ? new float[]{0f} : newData;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void setData(float[] newData) {
        data = (newData == null || newData.length == 0) ? new float[]{0f} : newData;
        animationProgress = 1f;
        invalidate();
    }

    public void setLabels(String[] labels) {
        if (labels != null && labels.length > 0) {
            this.labels = labels;
        }
        invalidate();
    }
}

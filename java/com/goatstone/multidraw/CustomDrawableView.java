package com.goatstone.multidraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.goatstone.multidraw.trans.Stroke;

/**
 * Created by goat on 4/7/14.
 */
public class CustomDrawableView extends View {

    private int x;
    private int y;
    private int backgroundColor;
    private Paint linePaint;
    private Paint dotPaint;
    final private float size = 40 * MultiDraw.screenMatchRatio;
    final private int offset = ((int) (size / 2));

    public CustomDrawableView(Context context) {
        super(context);

        x = 20;
        y = 200;
        int width = 900;
        int height = 500;
        backgroundColor = Color.argb(230, 250, 250, 250);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setDither(true);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeMiter(100.0f);

        dotPaint = new Paint(linePaint);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setStrokeWidth(0);

    }

    protected void onDraw(Canvas canvas) {
        setBackgroundColor(backgroundColor);

        for (Stroke s : MultiDraw.strokes) {

            linePaint.setColor(s.color);
            linePaint.setStrokeWidth(s.brushSize * MultiDraw.screenMatchRatio);

            final int initX = s.strokePoints.get(0)[0] * MultiDraw.screenMatchRatio - offset;
            final int initY = s.strokePoints.get(0)[1] * MultiDraw.screenMatchRatio - offset;

            dotPaint.setColor(s.color);
            canvas.drawOval(new RectF(initX, initY, initX + s.brushSize, initY + s.brushSize), dotPaint);

            for (int i = 0; i < s.strokePoints.size(); i++) {
                final int x = s.strokePoints.get(i)[0] * MultiDraw.screenMatchRatio;
                final int y = s.strokePoints.get(i)[1] * MultiDraw.screenMatchRatio;
                if ((i + 1) < s.strokePoints.size()) {
                    final int nextX = s.strokePoints.get(i + 1)[0] * MultiDraw.screenMatchRatio;
                    final int nextY = s.strokePoints.get(i + 1)[1] * MultiDraw.screenMatchRatio;
                    canvas.drawLine(x, y, nextX, nextY, linePaint);
                }
            }

        }

    }
}

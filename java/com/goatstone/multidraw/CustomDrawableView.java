package com.goatstone.multidraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;

import com.goatstone.multidraw.trans.Stroke;

/**
 * Created by goat on 4/7/14.
 */
public class CustomDrawableView extends View {

    private ShapeDrawable shapeDrawable;
    private int x;
    private int y;
    private int backgroundColor;
    private Paint paint;

    public CustomDrawableView(Context context) {
        super(context);

        x = 20;
        y = 200;
        int width = 900;
        int height = 500;
        backgroundColor = Color.argb(210, 250, 250, 250);
        shapeDrawable = new ShapeDrawable(new OvalShape());
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    protected void onDraw(Canvas canvas) {

        setBackgroundColor(backgroundColor);
        final float size = 20 * MultiDraw.screenMatchRatio;
        final int offset = ((int)(size/2)) ;

        // draw local strokes
        for (Stroke s : MultiDraw.localStrokes) {

            paint.setColor(s.color);
            paint.setStrokeWidth(size  );
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setDither(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setAntiAlias(true);
            paint.setStrokeMiter(100.0f);

            final int initX = s.strokePoints.get(0)[0] * MultiDraw.screenMatchRatio - offset;
            final int initY = s.strokePoints.get(0)[1] * MultiDraw.screenMatchRatio - offset;

            shapeDrawable.getPaint().setColor(Color.argb(255,100,0,0 ));
            int sizeInc = 5;
            shapeDrawable.setBounds(initX, initY,
                    initX + (int)size +sizeInc, initY + (int)size+sizeInc);
            shapeDrawable.draw(canvas);

            for (int i = 0; i < s.strokePoints.size(); i++) {
                final int x = s.strokePoints.get(i)[0] * MultiDraw.screenMatchRatio;
                final int y = s.strokePoints.get(i)[1] * MultiDraw.screenMatchRatio;
                if ((i + 1) < s.strokePoints.size()) {
                    final int nextX = s.strokePoints.get(i + 1)[0] * MultiDraw.screenMatchRatio;
                    final int nextY = s.strokePoints.get(i + 1)[1] * MultiDraw.screenMatchRatio;
                    canvas.drawLine(x, y, nextX, nextY, paint);
                }
            }
        }

        for (Stroke s : MultiDraw.strokes) {

            paint.setColor(s.color);
            paint.setStrokeWidth(size  );
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setDither(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setAntiAlias(true);
            paint.setStrokeMiter(100.0f);

            final int initX = s.strokePoints.get(0)[0] * MultiDraw.screenMatchRatio - offset;
            final int initY = s.strokePoints.get(0)[1] * MultiDraw.screenMatchRatio - offset;

            shapeDrawable.getPaint().setColor(Color.argb(255,100,0,0 ));
            int sizeInc = 5;
            shapeDrawable.setBounds(initX, initY,
                    initX + (int)size +sizeInc, initY + (int)size+sizeInc);
            shapeDrawable.draw(canvas);

            for (int i = 0; i < s.strokePoints.size(); i++) {
                final int x = s.strokePoints.get(i)[0] * MultiDraw.screenMatchRatio;
                final int y = s.strokePoints.get(i)[1] * MultiDraw.screenMatchRatio;
                if ((i + 1) < s.strokePoints.size()) {
                    final int nextX = s.strokePoints.get(i + 1)[0] * MultiDraw.screenMatchRatio;
                    final int nextY = s.strokePoints.get(i + 1)[1] * MultiDraw.screenMatchRatio;
                    canvas.drawLine(x, y, nextX, nextY, paint);
                }
            }
        }
        // draw the point received from the backend
//        for (Stroke s : MultiDraw.strokes) {
//
//            shapeDrawable.getPaint().setColor(s.color);
//
//            for (int[] sp : s.strokePoints) {
//                final int x = sp[0] * MultiDraw.screenMatchRatio;
//                final int y = sp[1] * MultiDraw.screenMatchRatio;
//                final int size = 20 * MultiDraw.screenMatchRatio;
//                shapeDrawable.setBounds(x, y, x + size, y + size);
//                shapeDrawable.draw(canvas);
//            }
//
//        }

    }

}

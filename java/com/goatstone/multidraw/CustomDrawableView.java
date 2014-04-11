package com.goatstone.multidraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    public CustomDrawableView(Context context) {
        super(context);

        x = 20;
        y = 200;
        int width = 900;
        int height = 500;
        backgroundColor = Color.argb(210, 250, 250, 250);
        shapeDrawable = new ShapeDrawable(new OvalShape());
    }

    protected void onDraw(Canvas canvas) {

        setBackgroundColor(backgroundColor);

        // draw local points
        int maxStrokePoints = 400;
        if (MultiDraw.localStrokePoints.size() > maxStrokePoints) {
            MultiDraw.localStrokePoints =
                    MultiDraw.localStrokePoints.subList(MultiDraw.localStrokePoints.size() - maxStrokePoints, MultiDraw.localStrokePoints.size());
        }
        for (int[] sp : MultiDraw.localStrokePoints) {
            final int x = sp[0] * MultiDraw.screenMatchRatio;
            final int y = sp[1] * MultiDraw.screenMatchRatio;
            final int size = 20 * MultiDraw.screenMatchRatio;
            shapeDrawable.getPaint().setColor(Color.argb(100, 100, 100, 100));
            shapeDrawable.setBounds(x, y, x + size, y + size);
            shapeDrawable.draw(canvas);
        }

        // Are there stroke to draw?
        if (!MultiDraw.hasStrokes()) {
            return;
        }
        // draw the point received from the backend
        for (Stroke s : MultiDraw.strokes) {

            shapeDrawable.getPaint().setColor(s.color);

            for (int[] sp : s.strokePoints) {
                final int x = sp[0] * MultiDraw.screenMatchRatio;
                final int y = sp[1] * MultiDraw.screenMatchRatio;
                final int size = 20 * MultiDraw.screenMatchRatio;
                shapeDrawable.setBounds(x, y, x + size, y + size);
                shapeDrawable.draw(canvas);
            }

        }

    }

}

package com.goatstone.multidraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goat on 4/7/14.
 */
public class CustomDrawableView extends View {
    private ShapeDrawable shapeDrawable;
    private int x;
    private int y;
    private List<int[]> strokes;
    private Paint paint;
    private int brushColor;
    private int backgroundColor;

    public CustomDrawableView(Context context) {
        super(context);

        x = 20;
        y = 200;
        int width = 900;
        int height = 500;
        strokes = new ArrayList<int[]>();
        //strokes.add(new int[]{1, 2});
        strokes.add(new int[]{100, 200});
        brushColor = Color.argb(255, 0, 0, 255);
        backgroundColor = Color.argb(255, 250, 250, 250);

        paint = new Paint();
        paint.setColor(Color.argb(255, 244, 0, 0));
        paint.setTextSize(20);

        shapeDrawable = new ShapeDrawable(new OvalShape());
    }

    public List<int[]> getStrokePoints() {
        return strokes;
    }

    protected void onDraw(Canvas canvas) {
        setBackgroundColor(backgroundColor);
        shapeDrawable.getPaint().setColor(brushColor);

        for (int[] i : strokes) {
            final int x = i[0] * MultiDraw.screenMatchRatio;
            final int y = i[1] * MultiDraw.screenMatchRatio;
            final int size = 20 * MultiDraw.screenMatchRatio;

            shapeDrawable.setBounds(x, y, x + size, y + size);
            shapeDrawable.draw(canvas);
        }
    }

    public boolean addToStrokePoints(List<int[]> al) {
        for (int[] i : al) {
            strokes.add(i);
        }
        return true;
    }

    public boolean setTouchPattern(int x, int y) {
        strokes.add(new int[]{x, y});
        invalidate();
        return true;
    }

    public boolean setBrushColor(int brushColor) {
        this.brushColor = brushColor;
        return true;
    }

}

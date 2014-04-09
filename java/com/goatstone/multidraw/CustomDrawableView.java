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

    public CustomDrawableView(Context context) {
        super(context);

        x = 20;
        y = 200;
        int width = 900;
        int height = 500;
        strokes = new ArrayList<int[]>();
        //strokes.add(new int[]{1, 2});
        strokes.add(new int[]{100, 200});

        paint = new Paint();
        paint.setColor(Color.argb(255, 244, 0, 0));
        paint.setTextSize(20);

        shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(0xff74AC23);
    }

    public List<int[]> getStrokePoints() {
        return strokes;
    }

    protected void onDraw(Canvas canvas) {
        setBackgroundColor(Color.argb(150, 0, 0, 200));
        for (int[] i : strokes) {
            final int x = i[0] * MultiDraw.screenMatchRatio;
            final int y = i[1] * MultiDraw.screenMatchRatio;
            final int size = 20*MultiDraw.screenMatchRatio;

            shapeDrawable.setBounds(x, y, x + size, y + size);
            shapeDrawable.draw(canvas);
        }
    }

    public boolean addToStrokePoints(ArrayList<int[]> al) {
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

}

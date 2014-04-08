package com.goatstone.multidraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.View;

/**
 * Created by goat on 4/7/14.
 */
public class CustomDrawableView extends View {
    private ShapeDrawable shapeDrawable;
    private int x;
    private int y;

    public CustomDrawableView(Context context) {
        super(context);

        x = 0;
        y = 200;
        int width = 500;
        int height = 500;

        shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(0xff74AC23);
        shapeDrawable.setBounds(x, y, x + width, y + height);
    }
    public boolean setX(int x){
        Log.i(AppUtil.getTagName(), "cdv - - - - - ");
        this.x = x;
        invalidate();
        return true;
    }
    protected void onDraw(Canvas canvas) {
        shapeDrawable.setBounds(x, y, getMeasuredWidth(), getMeasuredHeight() );
        shapeDrawable.draw(canvas);
        canvas.drawText("hello", x, 100, shapeDrawable.getPaint());
    }
}

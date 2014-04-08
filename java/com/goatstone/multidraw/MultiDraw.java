package com.goatstone.multidraw;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.goatstone.multidraw.trans.Stroke;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goat on 4/7/14.
 */
public class MultiDraw {
    private List<Stroke> strokes;

    public MultiDraw() {
        strokes = new ArrayList<Stroke>();
    }

    public boolean addStroke(Stroke stroke){
        strokes.add(stroke);
        return true;
    }
    public boolean drawStrokes(Canvas canvas){
        for(Stroke s : strokes){
            canvas.drawText("xxx", 100 ,100, new Paint());
        }
        return true;
    }
}

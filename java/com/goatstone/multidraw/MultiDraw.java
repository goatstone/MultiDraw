package com.goatstone.multidraw;

import android.graphics.Color;

import com.goatstone.multidraw.trans.Stroke;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goat on 4/7/14.
 */
public class MultiDraw {
    public static List<Stroke> strokes = new ArrayList<Stroke>();

    //    public int screenMatchRatio = (int)Math.floor( metrics.densityDpi/160 );
    public static int screenMatchRatio = 1;
    public static int alpha = 255;
    public static int red = 100;
    public static int green = 100;
    public static int blue = 100;
    public static int brushSize = 20;

    public static int getBrushColor() {
        return Color.argb(alpha, red, green, blue);
    }
    public static int getGhostBrushColor() {
        return Color.argb(100, red, green, blue);
    }
    public static boolean clearStrokeList(){
        strokes.clear();
        return true;
    }
    public static int getBrushSize(){
        return  brushSize;
    }
}

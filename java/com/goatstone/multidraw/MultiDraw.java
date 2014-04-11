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
    public static List<Stroke> localStrokes = new ArrayList<Stroke>();

    //    public int screenMatchRatio = (int)Math.floor( metrics.densityDpi/160 );
    public static int screenMatchRatio = 1;
    public static int brushColor = Color.argb(255, 50, 50, 50);

}

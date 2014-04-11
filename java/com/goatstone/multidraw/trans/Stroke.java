package com.goatstone.multidraw.trans;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goat on 4/7/14.
 */
public class Stroke {
    public List<int[]> strokePoints = new ArrayList();
    public int color = Color.argb(255, 0, 0, 255);

    public Stroke() {
    }

}

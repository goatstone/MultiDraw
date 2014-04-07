package com.goatstone.multidraw.trans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goat on 4/7/14.
 */
public class Stroke {
    public List<int[]> strokePoints = new ArrayList();

    public Stroke(){
        strokePoints.add(new int[]{100,200});
    }

}

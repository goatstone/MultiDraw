package com.goatstone.multidraw.trans;

import android.graphics.Color;

/**
 * Created by goat on 3/23/14.
 */
public class BackgroundProps {
    private int red;
    private int green;
    private int blue;

    public BackgroundProps(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getColor() {
        return Color.argb(255, red, green, blue);
    }

    public String getHexColor() {
        return "#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
    }

    public void setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

}
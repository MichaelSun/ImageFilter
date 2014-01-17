package com.imagefilter.effect;

import com.imagefilter.IImageFilter;
import com.imagefilter.Image;
import com.imagefilter.PixelUtils;

/**
 * Created by michael on 14-1-17.
 */
public abstract class PointFilter implements IImageFilter {

    protected boolean initialized = false;

    protected int[] rTable, gTable, bTable;

    public PointFilter() {
    }

    public Image process(Image imageIn) {
        if (!initialized) {
            rTable = gTable = bTable = makeTable();
        }

        for (int index = 0; index < imageIn.colorArray.length; ++index) {
            imageIn.colorArray[index] = filterRGB(imageIn.colorArray[index]);
        }

        return imageIn;
    }

    protected float transferFunction( float v ) {
        return 0;
    }

    private int filterRGB(int rgb) {
        int a1 = rgb & 0xff000000;
        int r1 = (rgb >> 16) & 0xff;
        int g1 = (rgb >> 8) & 0xff;
        int b1 = rgb & 0xff;
        int r = rTable[r1];
        int g = gTable[g1];
        int b = bTable[b1];

        return a1 | (r << 16) | (g << 8) | b;
    }

    protected int[] makeTable() {
        int[] table = new int[256];
        for (int i = 0; i < 256; i++) {
            table[i] = PixelUtils.clamp((int) (255 * transferFunction(i / 255.0f)));
        }

        return table;
    }

}

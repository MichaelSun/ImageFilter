package com.imagefilter.effect;

import android.content.Context;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.imagefilter.IImageFilter;
import com.imagefilter.Image;

/**
 * Created by michael on 14-1-22.
 *
 * just for android > 16
 */
public class FastBlurAndroidFilter implements IImageFilter {

    private int mRadius;

    private Context mContext;

    public FastBlurAndroidFilter(Context context, int radius) {
        mRadius = radius;
        mContext = context;
    }

    @Override
    public Image process(Image imageIn) {
        if (Build.VERSION.SDK_INT > 16) {
            final RenderScript rs = RenderScript.create(mContext);
            final Allocation input = Allocation.createFromBitmap(rs, imageIn.image, Allocation.MipmapControl.MIPMAP_NONE,
                                                                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(mRadius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(imageIn.destImage);

            int[] colorArray = new int[imageIn.image.getWidth() * imageIn.image.getHeight()];
            imageIn.destImage.getPixels(colorArray, 0, imageIn.getWidth(), 0, 0, imageIn.getWidth(), imageIn.getHeight());
            int r, g, b;
            for (int y = 0; y < imageIn.getHeight(); y++) {
                for (int x = 0; x < imageIn.getWidth(); x++) {
                    int index = y * imageIn.getWidth() + x;
                    r = (colorArray[index] >> 16) & 0xff;
                    g = (colorArray[index] >> 8) & 0xff;
                    b = colorArray[index] & 0xff;
                    colorArray[index] = 0xff000000 | (b << 16) | (g << 8) | r;
                }
            }

            imageIn.setColorArray(colorArray);

            return imageIn;
        }

        return imageIn;
    }

}

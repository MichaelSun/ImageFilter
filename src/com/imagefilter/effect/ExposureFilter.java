package com.imagefilter.effect;

/**
 * Created by michael on 14-1-17.
 */
public class ExposureFilter extends PointFilter {

    private float exposure = 1.0f;

    public ExposureFilter() {
        super();
    }

    public ExposureFilter(float exposureFactor) {
        super();
        exposure = exposureFactor;
    }

    @Override
    protected float transferFunction(float f) {
        return 1 - (float) Math.exp(-f * exposure);
    }

    /**
     * Set the exposure level.
     *
     * @param exposure the exposure level
     * @min-value 0
     * @max-value 5+
     * @see #getExposure
     */
    public void setExposure(float exposure) {
        this.exposure = exposure;
        initialized = false;
    }

    /**
     * Get the exposure level.
     *
     * @return the exposure level
     * @see #setExposure
     */
    public float getExposure() {
        return exposure;
    }

    public String toString() {
        return "Colors/Exposure...";
    }

}

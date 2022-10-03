package org.apache.tika.detect;

public abstract class TrainedModel
{
    public abstract double predict(final double[] p0);
    
    public abstract float predict(final float[] p0);
}

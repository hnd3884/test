package org.tukaani.xz;

import java.io.InputStream;

public class DeltaOptions extends FilterOptions
{
    public static final int DISTANCE_MIN = 1;
    public static final int DISTANCE_MAX = 256;
    private int distance;
    
    public DeltaOptions() {
        this.distance = 1;
    }
    
    public DeltaOptions(final int distance) throws UnsupportedOptionsException {
        this.distance = 1;
        this.setDistance(distance);
    }
    
    public void setDistance(final int distance) throws UnsupportedOptionsException {
        if (distance < 1 || distance > 256) {
            throw new UnsupportedOptionsException("Delta distance must be in the range [1, 256]: " + distance);
        }
        this.distance = distance;
    }
    
    public int getDistance() {
        return this.distance;
    }
    
    @Override
    public int getEncoderMemoryUsage() {
        return DeltaOutputStream.getMemoryUsage();
    }
    
    @Override
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream, final ArrayCache arrayCache) {
        return new DeltaOutputStream(finishableOutputStream, this);
    }
    
    @Override
    public int getDecoderMemoryUsage() {
        return 1;
    }
    
    @Override
    public InputStream getInputStream(final InputStream inputStream, final ArrayCache arrayCache) {
        return new DeltaInputStream(inputStream, this.distance);
    }
    
    @Override
    FilterEncoder getFilterEncoder() {
        return new DeltaEncoder(this);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            assert false;
            throw new RuntimeException();
        }
    }
}

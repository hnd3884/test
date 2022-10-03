package org.tukaani.xz.delta;

abstract class DeltaCoder
{
    static final int DISTANCE_MIN = 1;
    static final int DISTANCE_MAX = 256;
    static final int DISTANCE_MASK = 255;
    final int distance;
    final byte[] history;
    int pos;
    
    DeltaCoder(final int distance) {
        this.history = new byte[256];
        this.pos = 0;
        if (distance < 1 || distance > 256) {
            throw new IllegalArgumentException();
        }
        this.distance = distance;
    }
}

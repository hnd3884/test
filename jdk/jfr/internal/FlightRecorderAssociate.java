package jdk.jfr.internal;

import jdk.jfr.Recording;
import java.time.Instant;

public interface FlightRecorderAssociate
{
    void nextChunk(final Object p0, final SecuritySupport.SafePath p1, final Instant p2, final Instant p3, final long p4, final Recording p5);
    
    void finishJoin();
}

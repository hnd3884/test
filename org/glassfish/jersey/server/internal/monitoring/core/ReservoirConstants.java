package org.glassfish.jersey.server.internal.monitoring.core;

public final class ReservoirConstants
{
    public static final int COLLISION_BUFFER_POWER = 8;
    public static final int COLLISION_BUFFER = 256;
    public static final int TRIM_THRESHOLD = 256;
    
    private ReservoirConstants() {
        throw new AssertionError((Object)"Instantiation not allowed.");
    }
}

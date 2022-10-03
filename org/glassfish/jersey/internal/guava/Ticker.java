package org.glassfish.jersey.internal.guava;

public abstract class Ticker
{
    private static final Ticker SYSTEM_TICKER;
    
    Ticker() {
    }
    
    public static Ticker systemTicker() {
        return Ticker.SYSTEM_TICKER;
    }
    
    public abstract long read();
    
    static {
        SYSTEM_TICKER = new Ticker() {
            @Override
            public long read() {
                return Platform.systemNanoTime();
            }
        };
    }
}

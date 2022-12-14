package com.google.api.client.util;

public interface Sleeper
{
    public static final Sleeper DEFAULT = new Sleeper() {
        @Override
        public void sleep(final long millis) throws InterruptedException {
            Thread.sleep(millis);
        }
    };
    
    void sleep(final long p0) throws InterruptedException;
}

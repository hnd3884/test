package jdk.internal.platform;

public class Container
{
    private Container() {
    }
    
    public static Metrics metrics() {
        return Metrics.systemMetrics();
    }
}

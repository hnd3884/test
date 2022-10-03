package jdk.jfr;

import java.util.Map;
import java.time.Duration;
import jdk.Exported;

@Exported
public abstract class EventSettings
{
    EventSettings() {
    }
    
    public final EventSettings withStackTrace() {
        return this.with("stackTrace", "true");
    }
    
    public final EventSettings withoutStackTrace() {
        return this.with("stackTrace", "false");
    }
    
    public final EventSettings withoutThreshold() {
        return this.with("threshold", "0 s");
    }
    
    public final EventSettings withPeriod(final Duration duration) {
        return this.with("period", duration.toNanos() + " ns");
    }
    
    public final EventSettings withThreshold(final Duration duration) {
        if (duration == null) {
            return this.with("threshold", "0 ns");
        }
        return this.with("threshold", duration.toNanos() + " ns");
    }
    
    public abstract EventSettings with(final String p0, final String p1);
    
    abstract Map<String, String> toMap();
}

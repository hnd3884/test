package jdk.jfr.consumer;

import java.time.DateTimeException;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import jdk.jfr.internal.consumer.ChunkHeader;
import java.time.ZoneOffset;

final class TimeConverter
{
    private final long startTicks;
    private final long startNanos;
    private final double divisor;
    private final ZoneOffset zoneOffet;
    
    TimeConverter(final ChunkHeader chunkHeader, final int n) {
        this.startTicks = chunkHeader.getStartTicks();
        this.startNanos = chunkHeader.getStartNanos();
        this.divisor = chunkHeader.getTicksPerSecond() / 1.0E9;
        this.zoneOffet = this.zoneOfSet(n);
    }
    
    private ZoneOffset zoneOfSet(final int n) {
        try {
            return ZoneOffset.ofTotalSeconds(n / 1000);
        }
        catch (final DateTimeException ex) {
            Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Could not create ZoneOffset from raw offset " + n);
            return ZoneOffset.UTC;
        }
    }
    
    public long convertTimestamp(final long n) {
        return this.startNanos + (long)((n - this.startTicks) / this.divisor);
    }
    
    public long convertTimespan(final long n) {
        return (long)(n / this.divisor);
    }
    
    public ZoneOffset getZoneOffset() {
        return this.zoneOffet;
    }
}

package java.time.temporal;

import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.LocalTime;
import java.time.Duration;

public interface TemporalUnit
{
    Duration getDuration();
    
    boolean isDurationEstimated();
    
    boolean isDateBased();
    
    boolean isTimeBased();
    
    default boolean isSupportedBy(final Temporal temporal) {
        if (temporal instanceof LocalTime) {
            return this.isTimeBased();
        }
        if (temporal instanceof ChronoLocalDate) {
            return this.isDateBased();
        }
        if (temporal instanceof ChronoLocalDateTime || temporal instanceof ChronoZonedDateTime) {
            return true;
        }
        try {
            temporal.plus(1L, this);
            return true;
        }
        catch (final UnsupportedTemporalTypeException ex) {
            return false;
        }
        catch (final RuntimeException ex2) {
            try {
                temporal.plus(-1L, this);
                return true;
            }
            catch (final RuntimeException ex3) {
                return false;
            }
        }
    }
    
     <R extends Temporal> R addTo(final R p0, final long p1);
    
    long between(final Temporal p0, final Temporal p1);
    
    String toString();
}

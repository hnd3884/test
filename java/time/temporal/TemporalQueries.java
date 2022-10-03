package java.time.temporal;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.chrono.Chronology;
import java.time.ZoneId;

public final class TemporalQueries
{
    static final TemporalQuery<ZoneId> ZONE_ID;
    static final TemporalQuery<Chronology> CHRONO;
    static final TemporalQuery<TemporalUnit> PRECISION;
    static final TemporalQuery<ZoneOffset> OFFSET;
    static final TemporalQuery<ZoneId> ZONE;
    static final TemporalQuery<LocalDate> LOCAL_DATE;
    static final TemporalQuery<LocalTime> LOCAL_TIME;
    
    private TemporalQueries() {
    }
    
    public static TemporalQuery<ZoneId> zoneId() {
        return TemporalQueries.ZONE_ID;
    }
    
    public static TemporalQuery<Chronology> chronology() {
        return TemporalQueries.CHRONO;
    }
    
    public static TemporalQuery<TemporalUnit> precision() {
        return TemporalQueries.PRECISION;
    }
    
    public static TemporalQuery<ZoneId> zone() {
        return TemporalQueries.ZONE;
    }
    
    public static TemporalQuery<ZoneOffset> offset() {
        return TemporalQueries.OFFSET;
    }
    
    public static TemporalQuery<LocalDate> localDate() {
        return TemporalQueries.LOCAL_DATE;
    }
    
    public static TemporalQuery<LocalTime> localTime() {
        return TemporalQueries.LOCAL_TIME;
    }
    
    static {
        ZONE_ID = (temporalAccessor -> temporalAccessor.query(TemporalQueries.ZONE_ID));
        CHRONO = (temporalAccessor3 -> temporalAccessor3.query(TemporalQueries.CHRONO));
        PRECISION = (temporalAccessor5 -> temporalAccessor5.query(TemporalQueries.PRECISION));
        OFFSET = (temporalAccessor7 -> {
            if (temporalAccessor7.isSupported(ChronoField.OFFSET_SECONDS)) {
                return ZoneOffset.ofTotalSeconds(temporalAccessor7.get(ChronoField.OFFSET_SECONDS));
            }
            else {
                return null;
            }
        });
        ZONE = (temporalAccessor9 -> {
            final ZoneId zoneId = temporalAccessor9.query(TemporalQueries.ZONE_ID);
            return (zoneId != null) ? zoneId : temporalAccessor9.query(TemporalQueries.OFFSET);
        });
        LOCAL_DATE = (temporalAccessor11 -> {
            if (temporalAccessor11.isSupported(ChronoField.EPOCH_DAY)) {
                return LocalDate.ofEpochDay(temporalAccessor11.getLong(ChronoField.EPOCH_DAY));
            }
            else {
                return null;
            }
        });
        LOCAL_TIME = (temporalAccessor13 -> {
            if (temporalAccessor13.isSupported(ChronoField.NANO_OF_DAY)) {
                return LocalTime.ofNanoOfDay(temporalAccessor13.getLong(ChronoField.NANO_OF_DAY));
            }
            else {
                return null;
            }
        });
    }
}

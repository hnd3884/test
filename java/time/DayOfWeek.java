package java.time;

import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.time.format.TextStyle;
import java.time.temporal.TemporalField;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAccessor;

public enum DayOfWeek implements TemporalAccessor, TemporalAdjuster
{
    MONDAY, 
    TUESDAY, 
    WEDNESDAY, 
    THURSDAY, 
    FRIDAY, 
    SATURDAY, 
    SUNDAY;
    
    private static final DayOfWeek[] ENUMS;
    
    public static DayOfWeek of(final int n) {
        if (n < 1 || n > 7) {
            throw new DateTimeException("Invalid value for DayOfWeek: " + n);
        }
        return DayOfWeek.ENUMS[n - 1];
    }
    
    public static DayOfWeek from(final TemporalAccessor temporalAccessor) {
        if (temporalAccessor instanceof DayOfWeek) {
            return (DayOfWeek)temporalAccessor;
        }
        try {
            return of(temporalAccessor.get(ChronoField.DAY_OF_WEEK));
        }
        catch (final DateTimeException ex) {
            throw new DateTimeException("Unable to obtain DayOfWeek from TemporalAccessor: " + temporalAccessor + " of type " + temporalAccessor.getClass().getName(), ex);
        }
    }
    
    public int getValue() {
        return this.ordinal() + 1;
    }
    
    public String getDisplayName(final TextStyle textStyle, final Locale locale) {
        return new DateTimeFormatterBuilder().appendText(ChronoField.DAY_OF_WEEK, textStyle).toFormatter(locale).format(this);
    }
    
    @Override
    public boolean isSupported(final TemporalField temporalField) {
        if (temporalField instanceof ChronoField) {
            return temporalField == ChronoField.DAY_OF_WEEK;
        }
        return temporalField != null && temporalField.isSupportedBy(this);
    }
    
    @Override
    public ValueRange range(final TemporalField temporalField) {
        if (temporalField == ChronoField.DAY_OF_WEEK) {
            return temporalField.range();
        }
        return super.range(temporalField);
    }
    
    @Override
    public int get(final TemporalField temporalField) {
        if (temporalField == ChronoField.DAY_OF_WEEK) {
            return this.getValue();
        }
        return super.get(temporalField);
    }
    
    @Override
    public long getLong(final TemporalField temporalField) {
        if (temporalField == ChronoField.DAY_OF_WEEK) {
            return this.getValue();
        }
        if (temporalField instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + temporalField);
        }
        return temporalField.getFrom(this);
    }
    
    public DayOfWeek plus(final long n) {
        return DayOfWeek.ENUMS[(this.ordinal() + ((int)(n % 7L) + 7)) % 7];
    }
    
    public DayOfWeek minus(final long n) {
        return this.plus(-(n % 7L));
    }
    
    @Override
    public <R> R query(final TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.precision()) {
            return (R)ChronoUnit.DAYS;
        }
        return super.query(temporalQuery);
    }
    
    @Override
    public Temporal adjustInto(final Temporal temporal) {
        return temporal.with(ChronoField.DAY_OF_WEEK, this.getValue());
    }
    
    static {
        ENUMS = values();
    }
}

package java.time.chrono;

import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.time.temporal.TemporalField;
import java.time.DateTimeException;

public enum HijrahEra implements Era
{
    AH;
    
    public static HijrahEra of(final int n) {
        if (n == 1) {
            return HijrahEra.AH;
        }
        throw new DateTimeException("Invalid era: " + n);
    }
    
    @Override
    public int getValue() {
        return 1;
    }
    
    @Override
    public ValueRange range(final TemporalField temporalField) {
        if (temporalField == ChronoField.ERA) {
            return ValueRange.of(1L, 1L);
        }
        return super.range(temporalField);
    }
}

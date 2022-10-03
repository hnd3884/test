package java.time.temporal;

import java.time.DayOfWeek;
import java.util.Objects;
import java.time.LocalDate;
import java.util.function.UnaryOperator;

public final class TemporalAdjusters
{
    private TemporalAdjusters() {
    }
    
    public static TemporalAdjuster ofDateAdjuster(final UnaryOperator<LocalDate> unaryOperator) {
        Objects.requireNonNull(unaryOperator, "dateBasedAdjuster");
        return temporal2 -> temporal2.with((TemporalAdjuster)unaryOperator2.apply(LocalDate.from((TemporalAccessor)temporal2)));
    }
    
    public static TemporalAdjuster firstDayOfMonth() {
        return temporal -> temporal.with(ChronoField.DAY_OF_MONTH, 1L);
    }
    
    public static TemporalAdjuster lastDayOfMonth() {
        return temporal -> temporal.with(ChronoField.DAY_OF_MONTH, temporal.range(ChronoField.DAY_OF_MONTH).getMaximum());
    }
    
    public static TemporalAdjuster firstDayOfNextMonth() {
        return temporal -> temporal.with(ChronoField.DAY_OF_MONTH, 1L).plus(1L, ChronoUnit.MONTHS);
    }
    
    public static TemporalAdjuster firstDayOfYear() {
        return temporal -> temporal.with(ChronoField.DAY_OF_YEAR, 1L);
    }
    
    public static TemporalAdjuster lastDayOfYear() {
        return temporal -> temporal.with(ChronoField.DAY_OF_YEAR, temporal.range(ChronoField.DAY_OF_YEAR).getMaximum());
    }
    
    public static TemporalAdjuster firstDayOfNextYear() {
        return temporal -> temporal.with(ChronoField.DAY_OF_YEAR, 1L).plus(1L, ChronoUnit.YEARS);
    }
    
    public static TemporalAdjuster firstInMonth(final DayOfWeek dayOfWeek) {
        return dayOfWeekInMonth(1, dayOfWeek);
    }
    
    public static TemporalAdjuster lastInMonth(final DayOfWeek dayOfWeek) {
        return dayOfWeekInMonth(-1, dayOfWeek);
    }
    
    public static TemporalAdjuster dayOfWeekInMonth(final int n, final DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        dayOfWeek.getValue();
        if (n >= 0) {
            return temporal2 -> {
                temporal2.with(ChronoField.DAY_OF_MONTH, 1L);
                final Temporal temporal3;
                return temporal3.plus((int)((n2 - temporal3.get(ChronoField.DAY_OF_WEEK) + 7) % 7 + (n3 - 1L) * 7L), ChronoUnit.DAYS);
            };
        }
        return temporal5 -> {
            temporal5.with(ChronoField.DAY_OF_MONTH, temporal5.range(ChronoField.DAY_OF_MONTH).getMaximum());
            final Temporal temporal6;
            final int n6 = n4 - temporal6.get(ChronoField.DAY_OF_WEEK);
            return temporal6.plus((int)(((n6 == 0) ? 0 : ((n6 > 0) ? (n6 - 7) : n6)) - (-n5 - 1L) * 7L), ChronoUnit.DAYS);
        };
    }
    
    public static TemporalAdjuster next(final DayOfWeek dayOfWeek) {
        return temporal2 -> {
            dayOfWeek2.getValue();
            final int n2 = temporal2.get(ChronoField.DAY_OF_WEEK) - n;
            return temporal2.plus((n2 >= 0) ? (7 - n2) : ((long)(-n2)), ChronoUnit.DAYS);
        };
    }
    
    public static TemporalAdjuster nextOrSame(final DayOfWeek dayOfWeek) {
        return temporal2 -> {
            dayOfWeek2.getValue();
            temporal2.get(ChronoField.DAY_OF_WEEK);
            final int n2;
            if (n2 == n) {
                return temporal2;
            }
            else {
                final int n3;
                return temporal2.plus((n3 >= 0) ? (7 - n3) : ((long)(-n3)), ChronoUnit.DAYS);
            }
        };
    }
    
    public static TemporalAdjuster previous(final DayOfWeek dayOfWeek) {
        return temporal2 -> {
            dayOfWeek2.getValue();
            final int n2 = n - temporal2.get(ChronoField.DAY_OF_WEEK);
            return temporal2.minus((n2 >= 0) ? (7 - n2) : ((long)(-n2)), ChronoUnit.DAYS);
        };
    }
    
    public static TemporalAdjuster previousOrSame(final DayOfWeek dayOfWeek) {
        return temporal2 -> {
            dayOfWeek2.getValue();
            temporal2.get(ChronoField.DAY_OF_WEEK);
            final int n2;
            if (n2 == n) {
                return temporal2;
            }
            else {
                final int n3;
                return temporal2.minus((n3 >= 0) ? (7 - n3) : ((long)(-n3)), ChronoUnit.DAYS);
            }
        };
    }
}

package microsoft.sql;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Calendar;
import java.sql.Timestamp;
import java.io.Serializable;

public final class DateTimeOffset implements Serializable, Comparable<DateTimeOffset>
{
    private static final long serialVersionUID = 541973748553014280L;
    private final long utcMillis;
    private final int nanos;
    private final int minutesOffset;
    private static final int NANOS_MIN = 0;
    private static final int NANOS_MAX = 999999999;
    private static final int MINUTES_OFFSET_MIN = -840;
    private static final int MINUTES_OFFSET_MAX = 840;
    private static final int HUNDRED_NANOS_PER_SECOND = 10000000;
    private String formattedValue;
    
    private DateTimeOffset(final Timestamp timestamp, final int minutesOffset) {
        this.formattedValue = null;
        if (minutesOffset < -840 || minutesOffset > 840) {
            throw new IllegalArgumentException();
        }
        this.minutesOffset = minutesOffset;
        final int timestampNanos = timestamp.getNanos();
        if (timestampNanos < 0 || timestampNanos > 999999999) {
            throw new IllegalArgumentException();
        }
        final int hundredNanos = (timestampNanos + 50) / 100;
        this.nanos = 100 * (hundredNanos % 10000000);
        this.utcMillis = timestamp.getTime() - timestamp.getNanos() / 1000000 + 1000 * (hundredNanos / 10000000);
        assert this.minutesOffset >= -840 && this.minutesOffset <= 840 : "minutesOffset: " + this.minutesOffset;
        assert this.nanos >= 0 && this.nanos <= 999999999 : "nanos: " + this.nanos;
        assert 0 == this.nanos % 100 : "nanos: " + this.nanos;
        assert 0L == this.utcMillis % 1000L : "utcMillis: " + this.utcMillis;
    }
    
    public static DateTimeOffset valueOf(final Timestamp timestamp, final int minutesOffset) {
        return new DateTimeOffset(timestamp, minutesOffset);
    }
    
    public static DateTimeOffset valueOf(final Timestamp timestamp, final Calendar calendar) {
        calendar.setTimeInMillis(timestamp.getTime());
        return new DateTimeOffset(timestamp, (calendar.get(15) + calendar.get(16)) / 60000);
    }
    
    @Override
    public String toString() {
        String result = this.formattedValue;
        if (null == result) {
            final String formattedOffset = (this.minutesOffset < 0) ? String.format(Locale.US, "-%1$02d:%2$02d", -this.minutesOffset / 60, -this.minutesOffset % 60) : String.format(Locale.US, "+%1$02d:%2$02d", this.minutesOffset / 60, this.minutesOffset % 60);
            final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT" + formattedOffset), Locale.US);
            calendar.setTimeInMillis(this.utcMillis);
            assert this.nanos >= 0 && this.nanos <= 999999999;
            result = (this.formattedValue = ((0 == this.nanos) ? String.format(Locale.US, "%1$tF %1$tT %2$s", calendar, formattedOffset) : String.format(Locale.US, "%1$tF %1$tT.%2$s %3$s", calendar, BigDecimal.valueOf(this.nanos, 9).stripTrailingZeros().toPlainString().substring(2), formattedOffset)));
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateTimeOffset)) {
            return false;
        }
        final DateTimeOffset other = (DateTimeOffset)o;
        return this.utcMillis == other.utcMillis && this.nanos == other.nanos && this.minutesOffset == other.minutesOffset;
    }
    
    @Override
    public int hashCode() {
        assert 0L == this.utcMillis % 1000L;
        final long seconds = this.utcMillis / 1000L;
        int result = 571;
        result = 2011 * result + (int)seconds;
        result = 3217 * result + (int)(seconds / 60L * 60L * 24L * 365L);
        result = 3919 * result + this.nanos / 100000;
        result = 4463 * result + this.nanos / 1000;
        result = 5227 * result + this.nanos;
        result = 6689 * result + this.minutesOffset;
        result = 7577 * result + this.minutesOffset / 60;
        return result;
    }
    
    public Timestamp getTimestamp() {
        final Timestamp timestamp = new Timestamp(this.utcMillis);
        timestamp.setNanos(this.nanos);
        return timestamp;
    }
    
    public OffsetDateTime getOffsetDateTime() {
        final ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(60 * this.minutesOffset);
        final LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(this.utcMillis / 1000L, this.nanos, zoneOffset);
        return OffsetDateTime.of(localDateTime, zoneOffset);
    }
    
    public int getMinutesOffset() {
        return this.minutesOffset;
    }
    
    @Override
    public int compareTo(final DateTimeOffset other) {
        assert this.nanos >= 0;
        assert other.nanos >= 0;
        return (this.utcMillis > other.utcMillis) ? 1 : ((this.utcMillis < other.utcMillis) ? -1 : (this.nanos - other.nanos));
    }
    
    private Object writeReplace() {
        return new SerializationProxy(this);
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("");
    }
    
    private static class SerializationProxy implements Serializable
    {
        private final long utcMillis;
        private final int nanos;
        private final int minutesOffset;
        private static final long serialVersionUID = 664661379547314226L;
        
        SerializationProxy(final DateTimeOffset dateTimeOffset) {
            this.utcMillis = dateTimeOffset.utcMillis;
            this.nanos = dateTimeOffset.nanos;
            this.minutesOffset = dateTimeOffset.minutesOffset;
        }
        
        private Object readResolve() {
            final Timestamp timestamp = new Timestamp(this.utcMillis);
            timestamp.setNanos(this.nanos);
            return new DateTimeOffset(timestamp, this.minutesOffset, null);
        }
    }
}

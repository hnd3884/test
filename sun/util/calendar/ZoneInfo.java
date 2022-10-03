package sun.util.calendar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class ZoneInfo extends TimeZone
{
    private static final int UTC_TIME = 0;
    private static final int STANDARD_TIME = 1;
    private static final int WALL_TIME = 2;
    private static final long OFFSET_MASK = 15L;
    private static final long DST_MASK = 240L;
    private static final int DST_NSHIFT = 4;
    private static final long ABBR_MASK = 3840L;
    private static final int TRANSITION_NSHIFT = 12;
    private static final CalendarSystem gcal;
    private int rawOffset;
    private int rawOffsetDiff;
    private int checksum;
    private int dstSavings;
    private long[] transitions;
    private int[] offsets;
    private int[] simpleTimeZoneParams;
    private boolean willGMTOffsetChange;
    private transient boolean dirty;
    private static final long serialVersionUID = 2653134537216586139L;
    private transient SimpleTimeZone lastRule;
    
    public ZoneInfo() {
        this.rawOffsetDiff = 0;
        this.willGMTOffsetChange = false;
        this.dirty = false;
    }
    
    public ZoneInfo(final String s, final int n) {
        this(s, n, 0, 0, null, null, null, false);
    }
    
    ZoneInfo(final String id, final int rawOffset, final int dstSavings, final int checksum, final long[] transitions, final int[] offsets, final int[] simpleTimeZoneParams, final boolean willGMTOffsetChange) {
        this.rawOffsetDiff = 0;
        this.willGMTOffsetChange = false;
        this.dirty = false;
        this.setID(id);
        this.rawOffset = rawOffset;
        this.dstSavings = dstSavings;
        this.checksum = checksum;
        this.transitions = transitions;
        this.offsets = offsets;
        this.simpleTimeZoneParams = simpleTimeZoneParams;
        this.willGMTOffsetChange = willGMTOffsetChange;
    }
    
    @Override
    public int getOffset(final long n) {
        return this.getOffsets(n, null, 0);
    }
    
    public int getOffsets(final long n, final int[] array) {
        return this.getOffsets(n, array, 0);
    }
    
    public int getOffsetsByStandard(final long n, final int[] array) {
        return this.getOffsets(n, array, 1);
    }
    
    public int getOffsetsByWall(final long n, final int[] array) {
        return this.getOffsets(n, array, 2);
    }
    
    private int getOffsets(long n, final int[] array, final int n2) {
        if (this.transitions == null) {
            final int lastRawOffset = this.getLastRawOffset();
            if (array != null) {
                array[0] = lastRawOffset;
                array[1] = 0;
            }
            return lastRawOffset;
        }
        n -= this.rawOffsetDiff;
        final int transitionIndex = this.getTransitionIndex(n, n2);
        if (transitionIndex < 0) {
            final int lastRawOffset2 = this.getLastRawOffset();
            if (array != null) {
                array[0] = lastRawOffset2;
                array[1] = 0;
            }
            return lastRawOffset2;
        }
        if (transitionIndex < this.transitions.length) {
            final long n3 = this.transitions[transitionIndex];
            final int n4 = this.offsets[(int)(n3 & 0xFL)] + this.rawOffsetDiff;
            if (array != null) {
                final int n5 = (int)(n3 >>> 4 & 0xFL);
                final int n6 = (n5 == 0) ? 0 : this.offsets[n5];
                array[0] = n4 - n6;
                array[1] = n6;
            }
            return n4;
        }
        final SimpleTimeZone lastRule = this.getLastRule();
        if (lastRule != null) {
            final int rawOffset = lastRule.getRawOffset();
            long n7 = n;
            if (n2 != 0) {
                n7 -= this.rawOffset;
            }
            int n8 = lastRule.getOffset(n7) - this.rawOffset;
            if (n8 > 0 && lastRule.getOffset(n7 - n8) == rawOffset) {
                n8 = 0;
            }
            if (array != null) {
                array[0] = rawOffset;
                array[1] = n8;
            }
            return rawOffset + n8;
        }
        final int lastRawOffset3 = this.getLastRawOffset();
        if (array != null) {
            array[0] = lastRawOffset3;
            array[1] = 0;
        }
        return lastRawOffset3;
    }
    
    private int getTransitionIndex(final long n, final int n2) {
        int i = 0;
        int n3 = this.transitions.length - 1;
        while (i <= n3) {
            final int n4 = (i + n3) / 2;
            final long n5 = this.transitions[n4];
            long n6 = n5 >> 12;
            if (n2 != 0) {
                n6 += this.offsets[(int)(n5 & 0xFL)];
            }
            if (n2 == 1) {
                final int n7 = (int)(n5 >>> 4 & 0xFL);
                if (n7 != 0) {
                    n6 -= this.offsets[n7];
                }
            }
            if (n6 < n) {
                i = n4 + 1;
            }
            else {
                if (n6 <= n) {
                    return n4;
                }
                n3 = n4 - 1;
            }
        }
        if (i >= this.transitions.length) {
            return i;
        }
        return i - 1;
    }
    
    @Override
    public int getOffset(final int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        if (n6 < 0 || n6 >= 86400000) {
            throw new IllegalArgumentException();
        }
        if (n == 0) {
            n2 = 1 - n2;
        }
        else if (n != 1) {
            throw new IllegalArgumentException();
        }
        final CalendarDate calendarDate = ZoneInfo.gcal.newCalendarDate(null);
        calendarDate.setDate(n2, n3 + 1, n4);
        if (!ZoneInfo.gcal.validate(calendarDate)) {
            throw new IllegalArgumentException();
        }
        if (n5 < 1 || n5 > 7) {
            throw new IllegalArgumentException();
        }
        if (this.transitions == null) {
            return this.getLastRawOffset();
        }
        return this.getOffsets(ZoneInfo.gcal.getTime(calendarDate) + n6 - this.rawOffset, null, 0);
    }
    
    @Override
    public synchronized void setRawOffset(final int rawOffset) {
        if (rawOffset == this.rawOffset + this.rawOffsetDiff) {
            return;
        }
        this.rawOffsetDiff = rawOffset - this.rawOffset;
        if (this.lastRule != null) {
            this.lastRule.setRawOffset(rawOffset);
        }
        this.dirty = true;
    }
    
    @Override
    public int getRawOffset() {
        if (!this.willGMTOffsetChange) {
            return this.rawOffset + this.rawOffsetDiff;
        }
        final int[] array = new int[2];
        this.getOffsets(System.currentTimeMillis(), array, 0);
        return array[0];
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    private int getLastRawOffset() {
        return this.rawOffset + this.rawOffsetDiff;
    }
    
    @Override
    public boolean useDaylightTime() {
        return this.simpleTimeZoneParams != null;
    }
    
    @Override
    public boolean observesDaylightTime() {
        if (this.simpleTimeZoneParams != null) {
            return true;
        }
        if (this.transitions == null) {
            return false;
        }
        final int transitionIndex = this.getTransitionIndex(System.currentTimeMillis() - this.rawOffsetDiff, 0);
        if (transitionIndex < 0) {
            return false;
        }
        for (int i = transitionIndex; i < this.transitions.length; ++i) {
            if ((this.transitions[i] & 0xF0L) != 0x0L) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean inDaylightTime(final Date date) {
        if (date == null) {
            throw new NullPointerException();
        }
        if (this.transitions == null) {
            return false;
        }
        final int transitionIndex = this.getTransitionIndex(date.getTime() - this.rawOffsetDiff, 0);
        if (transitionIndex < 0) {
            return false;
        }
        if (transitionIndex < this.transitions.length) {
            return (this.transitions[transitionIndex] & 0xF0L) != 0x0L;
        }
        final SimpleTimeZone lastRule = this.getLastRule();
        return lastRule != null && lastRule.inDaylightTime(date);
    }
    
    @Override
    public int getDSTSavings() {
        return this.dstSavings;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[id=\"" + this.getID() + "\",offset=" + this.getLastRawOffset() + ",dstSavings=" + this.dstSavings + ",useDaylight=" + this.useDaylightTime() + ",transitions=" + ((this.transitions != null) ? this.transitions.length : 0) + ",lastRule=" + ((this.lastRule == null) ? this.getLastRuleInstance() : this.lastRule) + "]";
    }
    
    public static String[] getAvailableIDs() {
        return ZoneInfoFile.getZoneIds();
    }
    
    public static String[] getAvailableIDs(final int n) {
        return ZoneInfoFile.getZoneIds(n);
    }
    
    public static TimeZone getTimeZone(final String s) {
        return ZoneInfoFile.getZoneInfo(s);
    }
    
    private synchronized SimpleTimeZone getLastRule() {
        if (this.lastRule == null) {
            this.lastRule = this.getLastRuleInstance();
        }
        return this.lastRule;
    }
    
    public SimpleTimeZone getLastRuleInstance() {
        if (this.simpleTimeZoneParams == null) {
            return null;
        }
        if (this.simpleTimeZoneParams.length == 10) {
            return new SimpleTimeZone(this.getLastRawOffset(), this.getID(), this.simpleTimeZoneParams[0], this.simpleTimeZoneParams[1], this.simpleTimeZoneParams[2], this.simpleTimeZoneParams[3], this.simpleTimeZoneParams[4], this.simpleTimeZoneParams[5], this.simpleTimeZoneParams[6], this.simpleTimeZoneParams[7], this.simpleTimeZoneParams[8], this.simpleTimeZoneParams[9], this.dstSavings);
        }
        return new SimpleTimeZone(this.getLastRawOffset(), this.getID(), this.simpleTimeZoneParams[0], this.simpleTimeZoneParams[1], this.simpleTimeZoneParams[2], this.simpleTimeZoneParams[3], this.simpleTimeZoneParams[4], this.simpleTimeZoneParams[5], this.simpleTimeZoneParams[6], this.simpleTimeZoneParams[7], this.dstSavings);
    }
    
    @Override
    public Object clone() {
        final ZoneInfo zoneInfo = (ZoneInfo)super.clone();
        zoneInfo.lastRule = null;
        return zoneInfo;
    }
    
    @Override
    public int hashCode() {
        return this.getLastRawOffset() ^ this.checksum;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZoneInfo)) {
            return false;
        }
        final ZoneInfo zoneInfo = (ZoneInfo)o;
        return this.getID().equals(zoneInfo.getID()) && this.getLastRawOffset() == zoneInfo.getLastRawOffset() && this.checksum == zoneInfo.checksum;
    }
    
    @Override
    public boolean hasSameRules(final TimeZone timeZone) {
        if (this == timeZone) {
            return true;
        }
        if (timeZone == null) {
            return false;
        }
        if (!(timeZone instanceof ZoneInfo)) {
            return this.getRawOffset() == timeZone.getRawOffset() && (this.transitions == null && !this.useDaylightTime() && !timeZone.useDaylightTime());
        }
        return this.getLastRawOffset() == ((ZoneInfo)timeZone).getLastRawOffset() && this.checksum == ((ZoneInfo)timeZone).checksum;
    }
    
    public static Map<String, String> getAliasTable() {
        return ZoneInfoFile.getAliasMap();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.dirty = true;
    }
    
    static {
        gcal = CalendarSystem.getGregorianCalendar();
    }
}

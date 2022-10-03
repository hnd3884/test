package com.zoho.clustering.filerepl.event;

import java.util.regex.Pattern;

public class EventLogPosition
{
    public static final EventLogPosition START_VALUE;
    private int fileIndex;
    private long byteIndex;
    private static Pattern patternColon;
    private static String[] spaces;
    
    public EventLogPosition(final int fileIndex, final long byteIndex) {
        this.fileIndex = fileIndex;
        this.byteIndex = byteIndex;
    }
    
    public EventLogPosition(final String logPosString) {
        final String[] parts = EventLogPosition.patternColon.split(logPosString);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Wrong LogPosition string [" + logPosString + "]");
        }
        try {
            this.fileIndex = Integer.parseInt(parts[0].trim());
            this.byteIndex = Long.parseLong(parts[1].trim());
        }
        catch (final NumberFormatException exp) {
            throw new IllegalArgumentException("Wrong LogPosition string [" + logPosString + "]");
        }
    }
    
    public int fileIndex() {
        return this.fileIndex;
    }
    
    public long byteIndex() {
        return this.byteIndex;
    }
    
    @Override
    public String toString() {
        return this.fileIndex + ":" + this.byteIndex;
    }
    
    public String serialize() {
        return fileIndexAsPaddedStr(this.fileIndex) + ":" + byteIndexAsPaddedStr(this.byteIndex);
    }
    
    public static String fileIndexAsPaddedStr(final int fileIndex) {
        if (fileIndex > 9999) {
            throw new UnsupportedOperationException("FileIndex > 9999.Not Supported");
        }
        final String fileIndexStr = String.valueOf(fileIndex);
        return EventLogPosition.spaces[4 - fileIndexStr.length()] + fileIndexStr;
    }
    
    private static String byteIndexAsPaddedStr(final long byteIndex) {
        if (byteIndex > 9999999L) {
            throw new UnsupportedOperationException("ByteIndex > 9999999.Not Supported");
        }
        final String byteIndexStr = String.valueOf(byteIndex);
        return EventLogPosition.spaces[7 - byteIndexStr.length()] + byteIndexStr;
    }
    
    static {
        START_VALUE = new EventLogPosition(1, 0L);
        EventLogPosition.patternColon = Pattern.compile(":");
        EventLogPosition.spaces = new String[] { "", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          ", "           ", "            " };
    }
}

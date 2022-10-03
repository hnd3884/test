package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import java.util.Calendar;

public interface FTPTimestampParser
{
    public static final String DEFAULT_SDF = "MMM d yyyy";
    public static final String DEFAULT_RECENT_SDF = "MMM d HH:mm";
    
    Calendar parseTimestamp(final String p0) throws ParseException;
}

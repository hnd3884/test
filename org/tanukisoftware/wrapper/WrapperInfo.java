package org.tanukisoftware.wrapper;

import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

final class WrapperInfo
{
    private static final String m_version = "3.5.35";
    private static final Calendar m_build;
    
    static String getVersion() {
        return "3.5.35";
    }
    
    static String getBuildTime() {
        final DateFormat df = new SimpleDateFormat("HH:mm zz MMM d, yyyy");
        return df.format(WrapperInfo.m_build.getTime());
    }
    
    private WrapperInfo() {
    }
    
    static {
        m_build = Calendar.getInstance();
        final Calendar buildDate = Calendar.getInstance();
        final Calendar buildTime = Calendar.getInstance();
        try {
            buildDate.setTime(new SimpleDateFormat("yyyyMMdd").parse("20180411"));
            buildTime.setTime(new SimpleDateFormat("HHmm").parse("1310"));
            WrapperInfo.m_build.set(buildDate.get(1), buildDate.get(2), buildDate.get(5), buildTime.get(11), buildTime.get(12));
        }
        catch (final ParseException e) {
            System.out.println("WrapperInfo: Can not parse build date: " + e.getMessage());
        }
    }
}

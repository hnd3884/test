package com.lowagie.text.pdf;

import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class PdfDate extends PdfString
{
    private static final int[] DATE_SPACE;
    
    public PdfDate(final Calendar d) {
        final StringBuffer date = new StringBuffer("D:");
        date.append(this.setLength(d.get(1), 4));
        date.append(this.setLength(d.get(2) + 1, 2));
        date.append(this.setLength(d.get(5), 2));
        date.append(this.setLength(d.get(11), 2));
        date.append(this.setLength(d.get(12), 2));
        date.append(this.setLength(d.get(13), 2));
        int timezone = (d.get(15) + d.get(16)) / 3600000;
        if (timezone == 0) {
            date.append('Z');
        }
        else if (timezone < 0) {
            date.append('-');
            timezone = -timezone;
        }
        else {
            date.append('+');
        }
        if (timezone != 0) {
            date.append(this.setLength(timezone, 2)).append('\'');
            final int zone = Math.abs((d.get(15) + d.get(16)) / 60000) - timezone * 60;
            date.append(this.setLength(zone, 2)).append('\'');
        }
        this.value = date.toString();
    }
    
    public PdfDate() {
        this(new GregorianCalendar());
    }
    
    private String setLength(final int i, final int length) {
        final StringBuffer tmp = new StringBuffer();
        tmp.append(i);
        while (tmp.length() < length) {
            tmp.insert(0, "0");
        }
        tmp.setLength(length);
        return tmp.toString();
    }
    
    public String getW3CDate() {
        return getW3CDate(this.value);
    }
    
    public static String getW3CDate(String d) {
        if (d.startsWith("D:")) {
            d = d.substring(2);
        }
        final StringBuffer sb = new StringBuffer();
        if (d.length() < 4) {
            return "0000";
        }
        sb.append(d, 0, 4);
        d = d.substring(4);
        if (d.length() < 2) {
            return sb.toString();
        }
        sb.append('-').append(d, 0, 2);
        d = d.substring(2);
        if (d.length() < 2) {
            return sb.toString();
        }
        sb.append('-').append(d, 0, 2);
        d = d.substring(2);
        if (d.length() < 2) {
            return sb.toString();
        }
        sb.append('T').append(d, 0, 2);
        d = d.substring(2);
        if (d.length() < 2) {
            sb.append(":00Z");
            return sb.toString();
        }
        sb.append(':').append(d, 0, 2);
        d = d.substring(2);
        if (d.length() < 2) {
            sb.append('Z');
            return sb.toString();
        }
        sb.append(':').append(d, 0, 2);
        d = d.substring(2);
        if (d.startsWith("-") || d.startsWith("+")) {
            final String sign = d.substring(0, 1);
            d = d.substring(1);
            String h = "00";
            String m = "00";
            if (d.length() >= 2) {
                h = d.substring(0, 2);
                if (d.length() > 2) {
                    d = d.substring(3);
                    if (d.length() >= 2) {
                        m = d.substring(0, 2);
                    }
                }
                sb.append(sign).append(h).append(':').append(m);
                return sb.toString();
            }
        }
        sb.append('Z');
        return sb.toString();
    }
    
    public static Calendar decode(String s) {
        try {
            if (s.startsWith("D:")) {
                s = s.substring(2);
            }
            int slen = s.length();
            int idx = s.indexOf(90);
            GregorianCalendar calendar;
            if (idx >= 0) {
                slen = idx;
                calendar = new GregorianCalendar(new SimpleTimeZone(0, "ZPDF"));
            }
            else {
                int sign = 1;
                idx = s.indexOf(43);
                if (idx < 0) {
                    idx = s.indexOf(45);
                    if (idx >= 0) {
                        sign = -1;
                    }
                }
                if (idx < 0) {
                    calendar = new GregorianCalendar();
                }
                else {
                    int offset = Integer.parseInt(s.substring(idx + 1, idx + 3)) * 60;
                    if (idx + 5 < s.length()) {
                        offset += Integer.parseInt(s.substring(idx + 4, idx + 6));
                    }
                    calendar = new GregorianCalendar(new SimpleTimeZone(offset * sign * 60000, "ZPDF"));
                    slen = idx;
                }
            }
            calendar.clear();
            idx = 0;
            for (int k = 0; k < PdfDate.DATE_SPACE.length && idx < slen; idx += PdfDate.DATE_SPACE[k + 1], k += 3) {
                calendar.set(PdfDate.DATE_SPACE[k], Integer.parseInt(s.substring(idx, idx + PdfDate.DATE_SPACE[k + 1])) + PdfDate.DATE_SPACE[k + 2]);
            }
            return calendar;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    static {
        DATE_SPACE = new int[] { 1, 4, 0, 2, 2, -1, 5, 2, 0, 11, 2, 0, 12, 2, 0, 13, 2, 0 };
    }
}

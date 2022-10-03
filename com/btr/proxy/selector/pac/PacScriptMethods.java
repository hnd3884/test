package com.btr.proxy.selector.pac;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.StringTokenizer;
import java.net.UnknownHostException;
import com.btr.proxy.util.Logger;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;

public class PacScriptMethods implements ScriptMethods
{
    public static final String OVERRIDE_LOCAL_IP = "com.btr.proxy.pac.overrideLocalIP";
    private static final String GMT = "GMT";
    private static final List<String> DAYS;
    private static final List<String> MONTH;
    private Calendar currentTime;
    
    public boolean isPlainHostName(final String host) {
        return host.indexOf(".") < 0;
    }
    
    public boolean dnsDomainIs(final String host, final String domain) {
        return host.endsWith(domain);
    }
    
    public boolean localHostOrDomainIs(final String host, final String domain) {
        return domain.startsWith(host);
    }
    
    public boolean isResolvable(final String host) {
        try {
            InetAddress.getByName(host).getHostAddress();
            return true;
        }
        catch (final UnknownHostException ex) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "Hostname not resolveable {0}.", host);
            return false;
        }
    }
    
    public boolean isInNet(String host, final String pattern, final String mask) {
        host = this.dnsResolve(host);
        if (host == null || host.length() == 0) {
            return false;
        }
        final long lhost = this.parseIpAddressToLong(host);
        final long lpattern = this.parseIpAddressToLong(pattern);
        final long lmask = this.parseIpAddressToLong(mask);
        return (lhost & lmask) == lpattern;
    }
    
    private long parseIpAddressToLong(final String address) {
        long result = 0L;
        final String[] parts = address.split("\\.");
        long shift = 24L;
        for (final String part : parts) {
            final long lpart = Long.parseLong(part);
            result |= lpart << (int)shift;
            shift -= 8L;
        }
        return result;
    }
    
    public String dnsResolve(final String host) {
        try {
            return InetAddress.getByName(host).getHostAddress();
        }
        catch (final UnknownHostException e) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "DNS name not resolvable {0}.", host);
            return "";
        }
    }
    
    public String myIpAddress() {
        try {
            final String overrideIP = System.getProperty("com.btr.proxy.pac.overrideLocalIP");
            if (overrideIP != null && overrideIP.trim().length() > 0) {
                return overrideIP.trim();
            }
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (final UnknownHostException e) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "Local address not resolvable.", new Object[0]);
            return "";
        }
    }
    
    public int dnsDomainLevels(final String host) {
        int count = 0;
        int startPos = 0;
        while ((startPos = host.indexOf(".", startPos + 1)) > -1) {
            ++count;
        }
        return count;
    }
    
    public boolean shExpMatch(final String str, final String shexp) {
        final StringTokenizer tokenizer = new StringTokenizer(shexp, "*");
        int startPos = 0;
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            final int temp = str.indexOf(token, startPos);
            if (temp == -1) {
                return false;
            }
            startPos = temp + token.length();
        }
        return true;
    }
    
    public boolean weekdayRange(final String wd1, final String wd2, final String gmt) {
        final boolean useGmt = "GMT".equalsIgnoreCase(wd2) || "GMT".equalsIgnoreCase(gmt);
        final Calendar cal = this.getCurrentTime(useGmt);
        final int currentDay = cal.get(7) - 1;
        final int from = PacScriptMethods.DAYS.indexOf((wd1 == null) ? null : wd1.toUpperCase());
        int to = PacScriptMethods.DAYS.indexOf((wd2 == null) ? null : wd2.toUpperCase());
        if (to == -1) {
            to = from;
        }
        if (to < from) {
            return currentDay >= from || currentDay <= to;
        }
        return currentDay >= from && currentDay <= to;
    }
    
    public void setCurrentTime(final Calendar cal) {
        this.currentTime = cal;
    }
    
    private Calendar getCurrentTime(final boolean useGmt) {
        if (this.currentTime != null) {
            return (Calendar)this.currentTime.clone();
        }
        return Calendar.getInstance(useGmt ? TimeZone.getTimeZone("GMT") : TimeZone.getDefault());
    }
    
    public boolean dateRange(final Object day1, final Object month1, final Object year1, final Object day2, final Object month2, final Object year2, final Object gmt) {
        final Map<String, Integer> params = new HashMap<String, Integer>();
        this.parseDateParam(params, day1);
        this.parseDateParam(params, month1);
        this.parseDateParam(params, year1);
        this.parseDateParam(params, day2);
        this.parseDateParam(params, month2);
        this.parseDateParam(params, year2);
        this.parseDateParam(params, gmt);
        final boolean useGmt = params.get("gmt") != null;
        final Calendar cal = this.getCurrentTime(useGmt);
        final Date current = cal.getTime();
        if (params.get("day1") != null) {
            cal.set(5, params.get("day1"));
        }
        if (params.get("month1") != null) {
            cal.set(2, params.get("month1"));
        }
        if (params.get("year1") != null) {
            cal.set(1, params.get("year1"));
        }
        final Date from = cal.getTime();
        if (params.get("day2") != null) {
            cal.set(5, params.get("day2"));
        }
        if (params.get("month2") != null) {
            cal.set(2, params.get("month2"));
        }
        if (params.get("year2") != null) {
            cal.set(1, params.get("year2"));
        }
        Date to = cal.getTime();
        if (to.before(from)) {
            cal.add(2, 1);
            to = cal.getTime();
        }
        if (to.before(from)) {
            cal.add(1, 1);
            cal.add(2, -1);
            to = cal.getTime();
        }
        return current.compareTo(from) >= 0 && current.compareTo(to) <= 0;
    }
    
    private void parseDateParam(final Map<String, Integer> params, final Object value) {
        if (value instanceof Number) {
            final int n = ((Number)value).intValue();
            if (n <= 31) {
                if (params.get("day1") == null) {
                    params.put("day1", n);
                }
                else {
                    params.put("day2", n);
                }
            }
            else if (params.get("year1") == null) {
                params.put("year1", n);
            }
            else {
                params.put("year2", n);
            }
        }
        if (value instanceof String) {
            final int n = PacScriptMethods.MONTH.indexOf(((String)value).toUpperCase());
            if (n > -1) {
                if (params.get("month1") == null) {
                    params.put("month1", n);
                }
                else {
                    params.put("month2", n);
                }
            }
        }
        if ("GMT".equalsIgnoreCase(String.valueOf(value))) {
            params.put("gmt", 1);
        }
    }
    
    public boolean timeRange(final Object hour1, final Object min1, final Object sec1, final Object hour2, final Object min2, final Object sec2, final Object gmt) {
        final boolean useGmt = "GMT".equalsIgnoreCase(String.valueOf(min1)) || "GMT".equalsIgnoreCase(String.valueOf(sec1)) || "GMT".equalsIgnoreCase(String.valueOf(min2)) || "GMT".equalsIgnoreCase(String.valueOf(gmt));
        final Calendar cal = this.getCurrentTime(useGmt);
        cal.set(14, 0);
        final Date current = cal.getTime();
        Date from;
        Date to;
        if (sec2 instanceof Number) {
            cal.set(11, ((Number)hour1).intValue());
            cal.set(12, ((Number)min1).intValue());
            cal.set(13, ((Number)sec1).intValue());
            from = cal.getTime();
            cal.set(11, ((Number)hour2).intValue());
            cal.set(12, ((Number)min2).intValue());
            cal.set(13, ((Number)sec2).intValue());
            to = cal.getTime();
        }
        else if (hour2 instanceof Number) {
            cal.set(11, ((Number)hour1).intValue());
            cal.set(12, ((Number)min1).intValue());
            cal.set(13, 0);
            from = cal.getTime();
            cal.set(11, ((Number)sec1).intValue());
            cal.set(12, ((Number)hour2).intValue());
            cal.set(13, 59);
            to = cal.getTime();
        }
        else if (min1 instanceof Number) {
            cal.set(11, ((Number)hour1).intValue());
            cal.set(12, 0);
            cal.set(13, 0);
            from = cal.getTime();
            cal.set(11, ((Number)min1).intValue());
            cal.set(12, 59);
            cal.set(13, 59);
            to = cal.getTime();
        }
        else {
            cal.set(11, ((Number)hour1).intValue());
            cal.set(12, 0);
            cal.set(13, 0);
            from = cal.getTime();
            cal.set(11, ((Number)hour1).intValue());
            cal.set(12, 59);
            cal.set(13, 59);
            to = cal.getTime();
        }
        if (to.before(from)) {
            cal.setTime(to);
            cal.add(5, 1);
            to = cal.getTime();
        }
        return current.compareTo(from) >= 0 && current.compareTo(to) <= 0;
    }
    
    public boolean isResolvableEx(final String host) {
        return this.isResolvable(host);
    }
    
    public boolean isInNetEx(final String ipAddress, final String ipPrefix) {
        return false;
    }
    
    public String dnsResolveEx(final String host) {
        final StringBuilder result = new StringBuilder();
        try {
            final InetAddress[] arr$;
            final InetAddress[] list = arr$ = InetAddress.getAllByName(host);
            for (final InetAddress inetAddress : arr$) {
                result.append(inetAddress.getHostAddress());
                result.append("; ");
            }
        }
        catch (final UnknownHostException e) {
            Logger.log(JavaxPacScriptParser.class, Logger.LogLevel.DEBUG, "DNS name not resolvable {0}.", host);
        }
        return result.toString();
    }
    
    public String myIpAddressEx() {
        final String overrideIP = System.getProperty("com.btr.proxy.pac.overrideLocalIP");
        if (overrideIP != null && overrideIP.trim().length() > 0) {
            return overrideIP.trim();
        }
        return this.dnsResolveEx("localhost");
    }
    
    public String sortIpAddressList(final String ipAddressList) {
        if (ipAddressList == null || ipAddressList.trim().length() == 0) {
            return "";
        }
        final String[] ipAddressToken = ipAddressList.split(";");
        final List<InetAddress> parsedAddresses = new ArrayList<InetAddress>();
        for (final String ip : ipAddressToken) {
            try {
                parsedAddresses.add(InetAddress.getByName(ip));
            }
            catch (final UnknownHostException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(parsedAddresses, null);
        return ipAddressList;
    }
    
    public String getClientVersion() {
        return "1.0";
    }
    
    static {
        DAYS = Collections.unmodifiableList((List<? extends String>)Arrays.asList("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"));
        MONTH = Collections.unmodifiableList((List<? extends String>)Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"));
    }
}

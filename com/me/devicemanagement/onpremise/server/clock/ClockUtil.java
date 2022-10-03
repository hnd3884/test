package com.me.devicemanagement.onpremise.server.clock;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.Utils;
import java.util.Date;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ClockUtil
{
    private static Logger LOG;
    
    private static JSONObject getInternetTime() {
        JSONObject internetTimeJSON = null;
        final String[] hostnames = { "time.apple.com", "time.google.com", "time.windows.com" };
        for (int i = 0; i < hostnames.length && internetTimeJSON == null; internetTimeJSON = SNTPClient.getInternetTime(hostnames[i]), ++i) {}
        if (internetTimeJSON != null) {
            try {
                internetTimeJSON.put(SNTPConstants.INTERNET_TIME_DATE_FORMAT, (Object)new Date(internetTimeJSON.getLong(SNTPConstants.INTERNET_TIME_LONG)));
                internetTimeJSON.put(SNTPConstants.LOCAL_TIME_DATE_FORMAT, (Object)new Date(internetTimeJSON.getLong(SNTPConstants.LOCAL_TIME_LONG)));
                internetTimeJSON.put(SNTPConstants.INTERNET_TIME_PERSONALIZED_FORMAT, (Object)Utils.getTime(Long.valueOf(internetTimeJSON.getLong(SNTPConstants.INTERNET_TIME_LONG))));
                internetTimeJSON.put(SNTPConstants.LOCAL_TIME_PERSONALIZED_FORMAT, (Object)Utils.getTime(Long.valueOf(internetTimeJSON.getLong(SNTPConstants.LOCAL_TIME_LONG))));
                ClockUtil.LOG.log(Level.INFO, "Server time sync Parsed: " + internetTimeJSON);
            }
            catch (final JSONException ex) {
                ClockUtil.LOG.log(Level.SEVERE, "Error while getting Internet Time: ", (Throwable)ex);
            }
        }
        return internetTimeJSON;
    }
    
    public static JSONObject getInternetTimeJSON() {
        JSONObject internetTime = null;
        internetTime = getInternetTime();
        Long offsetTime = null;
        if (internetTime != null) {
            try {
                offsetTime = internetTime.getLong(SNTPConstants.TIME_DIFFERENCE_LONG);
                String clocktype = SNTPConstants.DIFFERENCE_TYPE_CLOCK_EQUAL;
                if (offsetTime < 0L) {
                    clocktype = SNTPConstants.DIFFERENCE_TYPE_CLOCK_AHEAD;
                }
                else if (offsetTime > 0L) {
                    clocktype = SNTPConstants.DIFFERENCE_TYPE_CLOCK_BEHIND;
                }
                internetTime.put(SNTPConstants.DIFFERENCE_TYPE, (Object)clocktype);
                offsetTime = Math.abs(offsetTime);
                final long diffMilliSeconds = offsetTime % 60L;
                final long diffSeconds = offsetTime / 1000L % 60L;
                final long diffMinutes = offsetTime / 60000L % 60L;
                final long diffHours = offsetTime / 3600000L % 24L;
                final long diffDays = offsetTime / 86400000L;
                internetTime.put(SNTPConstants.DIFFERENCE_IN_DAYS, diffDays);
                internetTime.put(SNTPConstants.DIFFERENCE_IN_HOURS, diffHours);
                internetTime.put(SNTPConstants.DIFFERENCE_IN_MINUTES, diffMinutes);
                internetTime.put(SNTPConstants.DIFFERENCE_IN_SECONDS, diffSeconds);
                internetTime.put(SNTPConstants.DIFFERENCE_IN_MILLI_SECONDS, diffMilliSeconds);
                final List<String> timeMsg = new ArrayList<String>();
                if (diffDays != 0L) {
                    timeMsg.add(diffDays + " day(s)");
                }
                if (diffHours != 0L) {
                    timeMsg.add(diffHours + " hour(s)");
                }
                if (diffMinutes != 0L) {
                    timeMsg.add(diffMinutes + " Minute(s)");
                }
                if (diffSeconds != 0L) {
                    timeMsg.add(diffSeconds + " Second(s)");
                }
                internetTime.put(SNTPConstants.MESSAGE, (Object)String.join(" ", timeMsg));
                ClockUtil.LOG.log(Level.INFO, "\n======Internet time sync starts========");
                ClockUtil.LOG.log(Level.INFO, "Server time sync: " + internetTime.toString(4));
                ClockUtil.LOG.log(Level.INFO, "\n======Internet time ends========");
                return internetTime;
            }
            catch (final Exception ex) {
                ClockUtil.LOG.log(Level.SEVERE, "Error while returning Internet Time JSON: ", ex);
            }
        }
        return internetTime;
    }
    
    public static boolean isServerTimeDifferedBy(final long thresholdMinutes) {
        try {
            final JSONObject minuteTimeDifferenceJSON = getInternetTimeJSON();
            if (minuteTimeDifferenceJSON == null) {
                return false;
            }
            final long minuteTimeDifference = minuteTimeDifferenceJSON.getLong(SNTPConstants.LOCAL_TIME_LONG) / 60000L - minuteTimeDifferenceJSON.getLong(SNTPConstants.INTERNET_TIME_LONG) / 60000L;
            return Math.abs(minuteTimeDifference) > thresholdMinutes;
        }
        catch (final JSONException ex) {
            ClockUtil.LOG.log(Level.SEVERE, "Error while parsing JSON object: ", (Throwable)ex);
            return false;
        }
    }
    
    static {
        ClockUtil.LOG = Logger.getLogger(ClockUtil.class.getName());
    }
}

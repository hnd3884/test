package com.me.framework.server.core;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import org.json.simple.parser.JSONParser;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import java.io.UnsupportedEncodingException;
import java.util.TimeZone;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.framework.server.model.DCTimezone;
import java.util.List;
import org.json.simple.JSONObject;

public class TimezoneUtil
{
    private static JSONObject timezoneVSCountryCodeDB;
    public static String timezoneVSCountryCodeDBJsonPath;
    
    public static List<DCTimezone> getAvailableTimezones() throws UnsupportedEncodingException, ParseException, JSONException {
        final boolean timeZonesAvailable = (boolean)FrameworkConfigurations.getSpecificPropertyIfExists("country_provider", "limited.user.personalise.timezones.enabled", (Object)false);
        return getTimezone(timeZonesAvailable ? getTimeZoneIds() : TimeZone.getAvailableIDs());
    }
    
    public static String[] getTimeZoneIds() throws UnsupportedEncodingException, ParseException {
        TimezoneUtil.timezoneVSCountryCodeDB = (JSONObject)new JSONParser().parse(new String(FileAccessUtil.getFileAsByteArray(TimezoneUtil.timezoneVSCountryCodeDBJsonPath), "UTF-8"));
        return Arrays.copyOf(TimezoneUtil.timezoneVSCountryCodeDB.keySet().toArray(), TimezoneUtil.timezoneVSCountryCodeDB.size(), (Class<? extends String[]>)String[].class);
    }
    
    private static List<DCTimezone> getTimezone(final String[] zoneIDs) {
        final List<DCTimezone> dcTimezones = new ArrayList<DCTimezone>();
        for (final String zoneID : zoneIDs) {
            final TimeZone timeZone = TimeZone.getTimeZone(zoneID);
            final DCTimezone dcTimezone = new DCTimezone();
            final int rawOffset = timeZone.getRawOffset();
            final Calendar calendar = Calendar.getInstance();
            final Date date = new Date();
            dcTimezone.setName(timeZone.getDisplayName(timeZone.inDaylightTime(date), 1));
            date.setTime((rawOffset > 0) ? ((long)rawOffset) : ((long)(86400000 - rawOffset)));
            calendar.setTime(date);
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            StringBuilder strOffset = new StringBuilder();
            final int minute = calendar.get(12);
            strOffset.append("GMT").append((rawOffset < 0) ? "-" : "+").append(calendar.get(11)).append(":").append(minute).append((minute < 10) ? "0" : "");
            dcTimezone.setId(timeZone.getID());
            dcTimezone.setOffset(strOffset.toString());
            strOffset = new StringBuilder().append("(").append(dcTimezone.getOffset()).append(") ").append(dcTimezone.getName()).append(" (").append(dcTimezone.getId()).append(")");
            dcTimezone.setDisplayName(strOffset.toString());
            dcTimezones.add(dcTimezone);
        }
        sortTimezone(dcTimezones);
        return dcTimezones;
    }
    
    private static void sortTimezone(final List<DCTimezone> dcTimezones) {
        final Comparator<DCTimezone> dcTimezoneComparator = new Comparator<DCTimezone>() {
            @Override
            public int compare(final DCTimezone dcTimezone1, final DCTimezone dcTimezone2) {
                final String[] strOffsets = { dcTimezone1.getOffset().substring(3), dcTimezone2.getOffset().substring(3) };
                final int[] offset = new int[2];
                for (int i = 0; i < 2; ++i) {
                    final String[] timeSplits = strOffsets[i].split(":");
                    offset[i] = Integer.parseInt(timeSplits[0]) * 100 + Integer.parseInt(timeSplits[1]);
                }
                return offset[0] - offset[1];
            }
        };
        Collections.sort(dcTimezones, dcTimezoneComparator);
    }
    
    public static DCTimezone getTimeZone(final String zoneID) {
        final List<DCTimezone> timezone = getTimezone(new String[] { zoneID });
        if (!timezone.isEmpty()) {
            return timezone.get(0);
        }
        return null;
    }
    
    public static boolean ValidateTime(final String scheduleTime, final String scheduleTimeZone) throws Exception {
        final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy, HH:mm");
        final Date scheduledTime = dateFormat.parse(scheduleTime);
        final Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone(scheduleTimeZone));
        final Calendar timeOfScheduler = Calendar.getInstance();
        timeOfScheduler.setTime(scheduledTime);
        if (currentTime.get(1) > timeOfScheduler.get(1)) {
            return false;
        }
        if (currentTime.get(1) == timeOfScheduler.get(1)) {
            if (currentTime.get(2) > timeOfScheduler.get(2)) {
                return false;
            }
            if (currentTime.get(2) == timeOfScheduler.get(2)) {
                if (currentTime.get(5) > timeOfScheduler.get(5)) {
                    return false;
                }
                if (currentTime.get(5) == timeOfScheduler.get(5)) {
                    if (currentTime.get(11) > timeOfScheduler.get(11)) {
                        return false;
                    }
                    if (currentTime.get(11) == timeOfScheduler.get(11) && currentTime.get(12) >= timeOfScheduler.get(12)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    static {
        TimezoneUtil.timezoneVSCountryCodeDB = null;
        TimezoneUtil.timezoneVSCountryCodeDBJsonPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "timezoneid-vs-countrycode-db.json";
    }
}

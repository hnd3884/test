package com.me.mdm.api.core.misc;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Comparator;
import org.json.JSONArray;
import java.util.TimeZone;
import java.util.ArrayList;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class TimeZonesAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public TimeZonesAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final List<TimeZone> comboDropDownItem = new ArrayList<TimeZone>();
            final String[] ids = TimeZone.getAvailableIDs();
            boolean filter = false;
            if (apiRequest.parameterList.containsKey("purpose") && apiRequest.parameterList.get("purpose").equals("androidrestrictionprofile")) {
                filter = true;
            }
            for (final String id : ids) {
                comboDropDownItem.add(TimeZone.getTimeZone(id));
            }
            final JSONArray result = new JSONArray();
            comboDropDownItem.sort(Comparator.comparingInt(TimeZone::getRawOffset));
            for (final TimeZone instance : comboDropDownItem) {
                final JSONObject timezone = new JSONObject();
                if (filter && !this.isSafeToAdd(instance.getID())) {
                    continue;
                }
                timezone.put("key", (Object)instance.getID());
                timezone.put("value", (Object)getTimeZone(instance));
                result.put((Object)timezone);
            }
            final JSONObject output = new JSONObject();
            output.put("timezones", (Object)result);
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)result);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in TimeZonesAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private boolean isSafeToAdd(final String timezone) {
        final String[] exclusionList = { "AST", "PST", "PNT", "CST", "IET", "PRT", "AGT", "BET", "ECT", "ART", "CAT", "EAT", "NET", "PLT", "BST", "VST", "CTT", "JST", "AET", "SST", "NST", "MIT", "CNT", "IST", "ACT" };
        return !Arrays.asList(exclusionList).contains(timezone);
    }
    
    private static String getTimeZone(final TimeZone tz) {
        final long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        final Date today = new Date();
        final String longName = tz.getDisplayName(tz.inDaylightTime(today), 1);
        String result = "";
        if (hours > 0L) {
            result = String.format("(GMT+%d:%02d) %s ( %s )", hours, minutes, longName, tz.getID());
        }
        else if (hours < 0L) {
            result = String.format("(GMT%d:%02d) %s ( %s )", hours, minutes, longName, tz.getID());
        }
        else {
            result = String.format("(GMT 0:0) %s ( %s )", longName, tz.getID());
        }
        return result;
    }
}

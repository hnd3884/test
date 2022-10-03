package com.me.mdm.onpremise.server.time;

import java.text.DateFormat;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.clock.SNTPConstants;
import com.me.devicemanagement.onpremise.server.clock.ClockUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ServerTimeValidationUtil
{
    private static final Logger LOGGER;
    private static final long THRESHOLD_MINUTE_DIFFERENCE = 5L;
    
    public static JSONObject getTimeDifferenceInfo() {
        final JSONObject timeInfo = ClockUtil.getInternetTimeJSON();
        if (timeInfo == null) {
            return null;
        }
        JSONObject returnInfo = new JSONObject();
        try {
            final double offset = (double)timeInfo.getLong(SNTPConstants.TIME_DIFFERENCE_LONG);
            final double diffMinutes = offset / 60000.0;
            returnInfo.put("sync_needed", Math.abs(diffMinutes) > 5.0);
            final String diffType = timeInfo.getString(SNTPConstants.DIFFERENCE_TYPE);
            if (diffType.equals(SNTPConstants.DIFFERENCE_TYPE_CLOCK_AHEAD)) {
                returnInfo.put("difference_type", (Object)"ahead");
            }
            else if (diffType.equals(SNTPConstants.DIFFERENCE_TYPE_CLOCK_BEHIND)) {
                returnInfo.put("difference_type", (Object)"behind");
            }
            else {
                returnInfo.put("difference_type", (Object)"equal");
            }
            returnInfo.put("difference_value", (Object)timeInfo.getString(SNTPConstants.MESSAGE));
        }
        catch (final Exception e) {
            ServerTimeValidationUtil.LOGGER.warning("Exception while parsing JSON object" + e);
            returnInfo = null;
        }
        return returnInfo;
    }
    
    public static void showMessage(final JSONObject timeDiffInfo) {
        try {
            SyMUtil.updateSyMParameter("MDMOP_is_time_synced", Boolean.toString(false));
            SyMUtil.updateSyMParameter("MDMOP_time_diff_type", timeDiffInfo.getString("difference_type"));
            SyMUtil.updateSyMParameter("MDMOP_time_diff_value", timeDiffInfo.getString("difference_value"));
            MessageProvider.getInstance().unhideMessage("SERVER_TIME_MISMATCH");
        }
        catch (final Exception e) {
            ServerTimeValidationUtil.LOGGER.warning("Error while parsing JSON for showing message " + e);
        }
    }
    
    public static void hideMessage() {
        String isSynced = SyMUtil.getSyMParameter("MDMOP_is_time_synced");
        if (isSynced == null) {
            isSynced = Boolean.toString(true);
        }
        final boolean alreadySynced = Boolean.parseBoolean(isSynced);
        if (!alreadySynced) {
            String userName = "DC-SYSTEM-USER";
            try {
                userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            }
            catch (final Exception exception) {
                ServerTimeValidationUtil.LOGGER.log(Level.WARNING, "Error while getting user name: ", exception);
            }
            final String i18n = "mdm.onpremise.serverTime.audit.synced";
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String curDateTime = dateFormat.format(new Date());
            DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, i18n, (Object)curDateTime, true);
        }
        SyMUtil.updateSyMParameter("MDMOP_is_time_synced", Boolean.toString(true));
        MessageProvider.getInstance().hideMessage("SERVER_TIME_MISMATCH");
    }
    
    public static void setNTPFetchError() {
        MessageProvider.getInstance().hideMessage("SERVER_TIME_MISMATCH");
        SyMUtil.updateSyMParameter("MDMOP_is_time_synced", Boolean.toString(false));
        SyMUtil.updateSyMParameter("MDMOP_time_diff_value", "unable to get time difference");
    }
    
    public static JSONObject getMETrackDetails() {
        String isSynced = SyMUtil.getSyMParameter("MDMOP_is_time_synced");
        if (isSynced == null) {
            isSynced = Boolean.toString(true);
        }
        final String diffType = SyMUtil.getSyMParameter("MDMOP_time_diff_type");
        final String diffValue = SyMUtil.getSyMParameter("MDMOP_time_diff_value");
        String timeDiff;
        if (diffValue != null) {
            if (!diffValue.equalsIgnoreCase("unable to get time difference") && diffType != null) {
                timeDiff = diffValue + " " + diffType;
            }
            else {
                timeDiff = diffValue;
            }
        }
        else {
            timeDiff = "";
        }
        return new JSONObject().put("is_synced", Boolean.parseBoolean(isSynced)).put("time_diff", (Object)timeDiff);
    }
    
    static {
        LOGGER = Logger.getLogger(ServerTimeValidationUtil.class.getName());
    }
}

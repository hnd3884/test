package com.me.mdm.onpremise.server.time;

import java.util.Hashtable;
import java.util.ArrayList;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class ServerTimeMismatchMsgHandler implements MsgHandler
{
    private static final Logger LOGGER;
    
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) throws Exception {
        final String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
        ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent.replace("{0}", this.getDiffType()).replace("{1}", this.getDiffValue()));
        return msgProperties;
    }
    
    private String getDiffType() {
        String diffType = SyMUtil.getSyMParameter("MDMOP_time_diff_type");
        if (diffType != null) {
            try {
                if (diffType.equals("behind")) {
                    diffType = I18N.getMsg("mdm.onpremise.serverTime.mismatch.behind", new Object[0]);
                }
                else if (diffType.equals("ahead")) {
                    diffType = I18N.getMsg("mdm.onpremise.serverTime.mismatch.ahead", new Object[0]);
                }
            }
            catch (final Exception e) {
                ServerTimeMismatchMsgHandler.LOGGER.warning("Error while getting I18N: " + e);
            }
        }
        return diffType;
    }
    
    private String getDiffValue() {
        String diffValue = SyMUtil.getSyMParameter("MDMOP_time_diff_value");
        if (diffValue != null) {
            final String[] tokens = diffValue.split("\\s+");
            try {
                final ArrayList<String> i18nString = new ArrayList<String>();
                for (int i = 1; i < tokens.length; i += 2) {
                    i18nString.add(this.getTimeUnit(tokens[i - 1], tokens[i]));
                }
                diffValue = String.join(" ", i18nString);
            }
            catch (final Exception e) {
                ServerTimeMismatchMsgHandler.LOGGER.warning("Error while getting I18N: " + e);
            }
        }
        return diffValue;
    }
    
    private String getTimeUnit(final String num, String unit) throws Exception {
        if (unit.equalsIgnoreCase("day(s)")) {
            if (num.equals("1")) {
                unit = I18N.getMsg("dc.common.DAY", new Object[0]);
            }
            else {
                unit = I18N.getMsg("dc.common.DAYS", new Object[0]);
            }
        }
        else if (unit.equalsIgnoreCase("hour(s)")) {
            if (num.equals("1")) {
                unit = I18N.getMsg("dc.common.HOUR", new Object[0]);
            }
            else {
                unit = I18N.getMsg("dc.common.HOURS", new Object[0]);
            }
        }
        else if (unit.equalsIgnoreCase("Minute(s)")) {
            if (num.equals("1")) {
                unit = I18N.getMsg("dc.common.MINUTE", new Object[0]);
            }
            else {
                unit = I18N.getMsg("dc.common.MINUTES", new Object[0]);
            }
        }
        else if (unit.equalsIgnoreCase("Second(s)")) {
            if (num.equals("1")) {
                unit = I18N.getMsg("dc.common.SECOND", new Object[0]);
            }
            else {
                unit = I18N.getMsg("dc.common.SECONDS", new Object[0]);
            }
        }
        return num + " " + unit;
    }
    
    static {
        LOGGER = Logger.getLogger(ServerTimeMismatchMsgHandler.class.getName());
    }
}

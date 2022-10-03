package com.me.mdm.onpremise.server.integration.jira;

import java.util.logging.Level;
import com.me.mdm.onpremise.server.integration.MDMIntegrationUtil;
import java.util.logging.Logger;

public class MDMJiraIntegrationUtil
{
    public Logger INTEGLOGGER;
    private static MDMJiraIntegrationUtil integUtil;
    
    public MDMJiraIntegrationUtil() {
        this.INTEGLOGGER = Logger.getLogger("MDMIntegrationLog");
    }
    
    public static MDMJiraIntegrationUtil getInstance() {
        if (MDMJiraIntegrationUtil.integUtil == null) {
            MDMJiraIntegrationUtil.integUtil = new MDMJiraIntegrationUtil();
        }
        return MDMJiraIntegrationUtil.integUtil;
    }
    
    public void handleJiraMETrack(final String reqURI, final String queryString) {
        String integParam = "";
        try {
            if (this.containsStr(reqURI, "remoteInv")) {
                integParam = "MDMP_JIRA_REMOTE_CONTROL_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/profiles") && this.containsStr(reqURI, "groups")) {
                integParam = "MDMP_JIRA_ASSIGN_PROFILE_GROUP_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/profiles") && this.containsStr(reqURI, "devices")) {
                integParam = "MDMP_JIRA_ASSIGN_PROFILE_DEVICE_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/apps") && this.containsStr(reqURI, "groups")) {
                integParam = "MDMP_JIRA_ASSIGN_APP_GROUP_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/apps") && this.containsStr(reqURI, "devices")) {
                integParam = "MDMP_JIRA_ASSIGN_APP_DEVICE_COUNT";
            }
            else if (this.containsStr(reqURI, "mdm/devices")) {
                if (this.containsStr(reqURI, "locations")) {
                    integParam = "MDMP_JIRA_LOCATE_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "deprovision")) {
                    integParam = "MDMP_JIRA_DEPROVISION_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "lock")) {
                    integParam = "MDMP_JIRA_LOCK_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "alarm")) {
                    integParam = "MDMP_JIRA_ALARM_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "enterprise/erase")) {
                    integParam = "MDMP_JIRA_CORPORATE_WIPE_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "erase")) {
                    integParam = "MDMP_JIRA_COMPLETE_WIPE_DEVICE_COUNT";
                }
                else if (this.containsStr(reqURI, "passcode/clear")) {
                    integParam = "MDMP_JIRA_CLEAR_PASSCODE_DEVICE_COUNT";
                }
            }
            MDMIntegrationUtil.getInstance().incrementIntegCount(integParam);
        }
        catch (final Exception ex) {
            this.INTEGLOGGER.log(Level.SEVERE, "Exception in handling ME track for SDP UI", ex);
        }
    }
    
    private boolean containsStr(final String queryString, final String containVal) {
        final int index = queryString.indexOf(containVal);
        return index != -1;
    }
    
    static {
        MDMJiraIntegrationUtil.integUtil = null;
    }
}

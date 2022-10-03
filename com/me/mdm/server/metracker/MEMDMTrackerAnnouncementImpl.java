package com.me.mdm.server.metracker;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerAnnouncementImpl extends MEMDMTrackerConstants
{
    private Properties mdmAnnouncementTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerAnnouncementImpl() {
        this.mdmAnnouncementTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerAnnouncementImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM Announcement implementation starts");
            if (!this.mdmAnnouncementTrackerProperties.isEmpty()) {
                this.mdmAnnouncementTrackerProperties = new Properties();
            }
            this.addAnnouncementDetails();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Announcement Summary : " + this.mdmAnnouncementTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMAnnouncementTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmAnnouncementTrackerProperties;
    }
    
    public void addAnnouncementDetails() {
        final JSONObject announcementDetailsJSON = new JSONObject();
        DMDataSetWrapper ds = null;
        try {
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                final SelectQuery announcementQuery = MDMCoreQuery.getInstance().getMDMQueryMap("ANNOUNCEMENT_QUERY");
                final String sql = RelationalAPI.getInstance().getSelectSQL((Query)announcementQuery);
                SyMLogger.info(this.logger, sql, "getProperties", "MDM Announcement Tracker impl starts");
                ds = DMDataSetWrapper.executeQuery((Object)announcementQuery);
                while (ds.next()) {
                    announcementDetailsJSON.put("ANNOUNCEMENT_PROFILE_COUNT", (Object)ds.getValue("ANNOUNCEMENT_PROFILE_COUNT").toString());
                    announcementDetailsJSON.put("ANNOUNCEMENT_DISTRIBUTED_COUNT", (Object)ds.getValue("ANNOUNCEMENT_DISTRIBUTED_COUNT").toString());
                    announcementDetailsJSON.put("ANNOUNCEMENT_NEED_ACK_BUTTON", (Object)ds.getValue("ANNOUNCEMENT_NEED_ACK_BUTTON").toString());
                    announcementDetailsJSON.put("ANNOUNCEMENT_FAILURE_COUNT", (Object)ds.getValue("ANNOUNCEMENT_FAILURE_COUNT").toString());
                }
            }
            this.mdmAnnouncementTrackerProperties.setProperty("ANNOUNCEMENT_DETAILS", announcementDetailsJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "announcementDetails", "Exception : ", (Throwable)ex);
        }
    }
}

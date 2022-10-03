package com.me.mdm.server.announcement.handler;

import java.util.Iterator;
import java.util.HashMap;
import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.List;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import java.util.ArrayList;
import com.me.uem.announcement.AnnouncementHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.mdm.server.audit.AuditLogHandlerInterface;

public class AnnouncementAuditLogHandler implements AuditLogHandlerInterface
{
    private static AnnouncementAuditLogHandler announcementAuditLogHandler;
    private Logger logger;
    
    public AnnouncementAuditLogHandler() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
    
    public static AnnouncementAuditLogHandler getInstance() {
        if (AnnouncementAuditLogHandler.announcementAuditLogHandler == null) {
            AnnouncementAuditLogHandler.announcementAuditLogHandler = new AnnouncementAuditLogHandler();
        }
        return AnnouncementAuditLogHandler.announcementAuditLogHandler;
    }
    
    @Override
    public void addEventLogEntry(final DCQueueData qData) {
        final JSONObject eventDetails = new JSONObject(qData.queueData.toString());
        final JSONObject baseJsonObject = (JSONObject)eventDetails.get("baseJsonObject");
        try {
            final Long customerId = baseJsonObject.getLong("customer_id");
            final Integer eventType = (Integer)eventDetails.get("eventType");
            final JSONArray announcementArray = baseJsonObject.getJSONArray("announcement_list");
            final List<Long> announcementList = JSONUtil.getInstance().convertLongJSONArrayTOList(announcementArray);
            final JSONArray resourceArray = baseJsonObject.getJSONArray("resource_list");
            final List<Long> resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceArray);
            final HashMap<Long, String> announcementMap = AnnouncementHandler.newInstance().getAnnouncementNames(announcementList);
            final List<Object> remarkArgs = new ArrayList<Object>();
            final HashMap<Long, String> resourceMap = MDMResourceDataProvider.getResourceNames(resourceList);
            for (final Long announcementId : announcementList) {
                final String announcementName = announcementMap.get(announcementId);
                for (final Long resourceId : resourceList) {
                    final String resourceName = resourceMap.get(resourceId);
                    final Object remark = announcementName + "@@@" + resourceName;
                    remarkArgs.add(remark);
                }
            }
            final Long userID = baseJsonObject.getLong("user_id");
            final String sUserName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(userID));
            final String remarks = this.getEvenLogRemarksKey(eventType, (Boolean)baseJsonObject.get("isGroup"));
            MDMEventLogHandler.getInstance().addEvent(eventType, resourceList, sUserName, remarks, remarkArgs, customerId, System.currentTimeMillis());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  addEventLogForDeviceDistribution {0}", ex);
        }
    }
    
    private String getEvenLogRemarksKey(final Integer eventType, final Boolean isGroup) {
        String sEventLogRemarksKey;
        if (isGroup) {
            sEventLogRemarksKey = ((eventType == 75002) ? "mdm.actionlog.announcement.group_distribution" : "mdm.actionlog.announcement.group_disassociated");
        }
        else {
            sEventLogRemarksKey = ((eventType == 75002) ? "mdm.actionlog.announcement.device_distribution" : "mdm.actionlog.announcement.device_disassociated");
        }
        return sEventLogRemarksKey;
    }
    
    static {
        AnnouncementAuditLogHandler.announcementAuditLogHandler = null;
    }
}

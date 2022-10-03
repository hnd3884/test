package com.me.mdm.server.audit;

import com.me.mdm.server.announcement.handler.AnnouncementAuditLogHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class AuditLogHandler
{
    public static AuditLogHandlerInterface auditLogHandlerInterface;
    
    public static AuditLogHandlerInterface getInstance(final DCQueueData dcQueueData) {
        switch (dcQueueData.queueDataType) {
            case 201: {
                AuditLogHandler.auditLogHandlerInterface = AppsAuditLogHandler.getInstance();
                break;
            }
            case 200: {
                AuditLogHandler.auditLogHandlerInterface = ProfilesAuditLogHandler.getInstance();
                break;
            }
            case 202: {
                AuditLogHandler.auditLogHandlerInterface = AnnouncementAuditLogHandler.getInstance();
                break;
            }
        }
        return AuditLogHandler.auditLogHandlerInterface;
    }
}

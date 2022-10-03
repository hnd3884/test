package com.me.mdm.directory.service.mam;

import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;

public class AzureMamAuditHandler
{
    private static AzureMamAuditHandler azureMamAuditHandler;
    
    public static AzureMamAuditHandler getInstance() {
        if (AzureMamAuditHandler.azureMamAuditHandler == null) {
            AzureMamAuditHandler.azureMamAuditHandler = new AzureMamAuditHandler();
        }
        return AzureMamAuditHandler.azureMamAuditHandler;
    }
    
    public void logEvent(final int eventID, final Long resourceID, final String remarks, final Object remarksArgs, final Long customerId) {
        try {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(eventID, resourceID, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), remarks, remarksArgs, customerId);
        }
        catch (final Exception ex) {
            IDPSlogger.SOM.log(Level.SEVERE, null, ex);
        }
    }
    
    static {
        AzureMamAuditHandler.azureMamAuditHandler = null;
    }
}

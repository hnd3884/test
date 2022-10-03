package com.adventnet.sym.server.mdm.util;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.HashMap;

public class MDMEventLogHandler
{
    private static MDMEventLogHandler mdmEventLogHandler;
    
    public static MDMEventLogHandler getInstance() {
        if (MDMEventLogHandler.mdmEventLogHandler == null) {
            MDMEventLogHandler.mdmEventLogHandler = new MDMEventLogHandler();
        }
        return MDMEventLogHandler.mdmEventLogHandler;
    }
    
    public void MDMEventLogEntry(final int eventID, final Long resourceID, final String userName, final String remarks, final Object remarksArgs, final Long customerId) {
        final HashMap eventLogHash = new HashMap();
        eventLogHash.put("resourceID", resourceID);
        DCEventLogUtil.getInstance().addEvent(eventID, userName, eventLogHash, remarks, remarksArgs, true, customerId);
    }
    
    public void addEvent(final int eventID, final HashMap resourceVsArgName, final String userName, final String remarks, final Long customerId, final Long eventTimeStamp) {
        final List resourceIDs = new ArrayList();
        final List<Object> remarksArgs = new ArrayList<Object>();
        for (final Object key : resourceVsArgName.keySet()) {
            resourceIDs.add(key);
            remarksArgs.add(resourceVsArgName.get(key));
        }
        this.addEvent(eventID, resourceIDs, userName, remarks, remarksArgs, customerId, eventTimeStamp);
    }
    
    public void addEvent(final int eventID, final String userName, final String remarks, final List<Object> remarksArgs, final Long customerId, final Long eventTimeStamp) {
        this.addEvent(eventID, new ArrayList(), userName, remarks, remarksArgs, customerId, eventTimeStamp);
    }
    
    public void addEvent(final int eventID, final List resourceIds, final String userName, final String remarks, final List<Object> remarksArgs, final Long customerId, final Long eventTimeStamp) {
        final HashMap eventLogHash = new HashMap();
        eventLogHash.put("resourceIDs", resourceIds);
        DCEventLogUtil.getInstance().addEventForAll(eventID, userName, eventLogHash, remarks, (List)remarksArgs, true, customerId, (Long)null, eventTimeStamp);
    }
    
    static {
        MDMEventLogHandler.mdmEventLogHandler = null;
    }
}

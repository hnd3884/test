package com.me.idps.core.sync.events;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.sql.Connection;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;

public class DirectoryEventsUtil
{
    private static DirectoryEventsUtil directoryEventsUtil;
    
    public static DirectoryEventsUtil getInstance() {
        if (DirectoryEventsUtil.directoryEventsUtil == null) {
            DirectoryEventsUtil.directoryEventsUtil = new DirectoryEventsUtil();
        }
        return DirectoryEventsUtil.directoryEventsUtil;
    }
    
    private Long getNewEventID(final Long dmDomainID, final int eventType) throws Exception {
        final DataObject dobj = (DataObject)new WritableDataObject();
        final Row row = new Row("DirectoryEventToken");
        row.set("DM_DOMAIN_ID", (Object)dmDomainID);
        row.set("EVENT_TYPE", (Object)eventType);
        row.set("ADDED_AT", (Object)System.currentTimeMillis());
        row.set("STATUS_ID", (Object)951);
        row.set("USER_ID", (Object)DMDomainSyncDetailsDataHandler.getInstance().getSyncIntiatedByUserID(dmDomainID));
        dobj.addRow(row);
        SyMUtil.getPersistenceLite().add(dobj);
        return (Long)row.get("EVENT_TOKEN_ID");
    }
    
    public void markEventSucceeded(final Connection connection, final Long dmDomainID, final Long dirEventID) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("DirectoryEventToken", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectoryEventToken", "EVENT_TOKEN_ID"), (Object)dirEventID, 0)).and(new Criteria(Column.getColumn("DirectoryEventToken", "STATUS_ID"), (Object)951, 0));
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectoryEventToken");
        updateQuery.setCriteria(criteria);
        updateQuery.setUpdateColumn("STATUS_ID", (Object)921);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
    }
    
    public Long populateEvents(final Connection connection, final Long dmDomainID, final Long collationID, final IdpEventConstants eventType, final SelectQuery eventInsQuery, final Column resIDcol, final Column eventTimeStampCol, final Column eventDetailCol) throws Exception {
        final HashMap<String, String> replaceMap = new HashMap<String, String>();
        final Long dirEventID = this.getNewEventID(dmDomainID, eventType.getEventType());
        final HashMap<String, Column> insertColMap = new HashMap<String, Column>();
        insertColMap.put("EVENT_TOKEN_ID", null);
        insertColMap.put("RESOURCE_ID", resIDcol);
        insertColMap.put("EVENT_DETAILS", eventDetailCol);
        insertColMap.put("EVENT_TIME_STAMP", eventTimeStampCol);
        replaceMap.put(",EVENT_TIME_STAMP) SELECT ", ",EVENT_TIME_STAMP) SELECT " + String.valueOf(dirEventID) + ",");
        DirectoryQueryutil.getInstance().executeInsertQuery(connection, dmDomainID, collationID, eventInsQuery, "DirectoryEventDetails", insertColMap, replaceMap, "DirectoryEventsUtil", null, false);
        IDPSlogger.DBO.log(Level.INFO, "obtained directory event ID {0} for {1}", new Object[] { String.valueOf(dirEventID), eventType.toString() });
        final List<Column> columns = eventInsQuery.getSelectColumns();
        for (final Column selectCol : columns) {
            eventInsQuery.removeSelectColumn(selectCol);
        }
        return dirEventID;
    }
    
    static {
        DirectoryEventsUtil.directoryEventsUtil = null;
    }
}

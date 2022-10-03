package com.me.devicemanagement.framework.server.eventlog;

import com.me.devicemanagement.framework.server.eventlog.factory.EventLogFactoryProvider;
import java.util.StringTokenizer;
import java.net.UnknownHostException;
import java.net.Inet6Address;
import java.net.InetAddress;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;

public class DCEventLogUtil
{
    protected static Logger logger;
    protected static DCEventLogUtil dceventlog;
    private static final int BATCH_SIZE = 1000;
    
    public static DCEventLogUtil getInstance() {
        if (DCEventLogUtil.dceventlog == null) {
            DCEventLogUtil.dceventlog = new DCEventLogUtil();
        }
        return DCEventLogUtil.dceventlog;
    }
    
    protected Row getEventLogRow(final int eventID, final String userName, final String remarks, Object remarksArgs) {
        final String sourceMethod = "DCEventLogUtil::getEventLogRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventID : " + eventID + " userName : " + userName + " remarks : " + remarks + " remarksArgs : " + remarksArgs);
        final long currentTime = System.currentTimeMillis();
        final Row eventLogRow = new Row("EventLog");
        eventLogRow.set("EVENT_ID", (Object)new Integer(eventID));
        eventLogRow.set("LOGON_USER_NAME", (Object)userName);
        eventLogRow.set("EVENT_TIMESTAMP", (Object)new Long(currentTime));
        eventLogRow.set("EVENT_REMARKS", (Object)remarks);
        if (remarksArgs != null && remarksArgs.toString().length() > 1999) {
            remarksArgs = remarksArgs.toString().substring(0, 1999);
        }
        eventLogRow.set("EVENT_REMARKS_EN", (Object)I18NUtil.transformRemarksInEnglish(remarks, (String)remarksArgs));
        eventLogRow.set("EVENT_REMARKS_ARGS", remarksArgs);
        eventLogRow.set("EVENT_SOURCE_IP", (Object)EventLogThreadLocal.getSourceIpAddress());
        eventLogRow.set("EVENT_SOURCE_HOSTNAME", (Object)getHostNameForEventSource());
        return eventLogRow;
    }
    
    protected Row getEventTimeDurationRow(final Object eventlogID, final Long startTime, final Long endTime, final String viewerIP, final String promptStatus) {
        final String sourceMethod = "DCEventLogUtil::getEventTimeDurationRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventlogID : " + eventlogID + " startTime : " + startTime + " endTime : " + endTime);
        final Row eventTDRow = new Row("EventTimeDuration");
        eventTDRow.set("EVENT_LOG_ID", eventlogID);
        eventTDRow.set("EVENT_START_TIME", (Object)new Long(startTime));
        if (endTime != null) {
            eventTDRow.set("EVENT_END_TIME", (Object)new Long(endTime));
            final long timeDiff = endTime - startTime;
            final int timeDuration = (timeDiff / 60000L).intValue();
            DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventlogID : " + timeDuration);
            eventTDRow.set("EVENT_TIME_DURATION", (Object)new Integer(timeDuration));
        }
        if (viewerIP != null) {
            eventTDRow.set("VIEWER_IP", (Object)viewerIP);
        }
        return eventTDRow;
    }
    
    protected Row getResourceEventLogRelRow(final Object eventlogID, final Long resourceID, final String computerName, final String domainName) {
        final String sourceMethod = "DCEventLogUtil::getResourceEventLogRelRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventlogID : " + eventlogID + " resourceID : " + resourceID);
        final Row eventResRelRow = new Row("ResourceEventLogRel");
        eventResRelRow.set("EVENT_LOG_ID", eventlogID);
        eventResRelRow.set("RESOURCE_ID", (Object)resourceID);
        if (computerName != null) {
            eventResRelRow.set("RESOURCE_NAME", (Object)computerName);
        }
        if (domainName != null) {
            eventResRelRow.set("DOMAIN_NETBIOS_NAME", (Object)domainName);
        }
        return eventResRelRow;
    }
    
    protected Row getCustomerEventLogRow(final Object eventLogID, final Long customerID) {
        final String sourceMethod = "DCEventLogUtil::getCustomerEventLogRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventLogID : " + eventLogID + " customerID : " + customerID);
        final Row custEventLogRow = new Row("CustomerEventLog");
        custEventLogRow.set("EVENT_LOG_ID", eventLogID);
        custEventLogRow.set("CUSTOMER_ID", (Object)customerID);
        return custEventLogRow;
    }
    
    protected Row getEventLogAndConsentSRelRow(final Object eventLogID, final Long consent_id) {
        final String sourceMethod = "DCEventLogUtil::getCustomerEventLogRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventLogID : " + eventLogID + " consent id : " + consent_id);
        final Row eventModuleSubCategory = new Row("EventLogAndConsentsRel");
        eventModuleSubCategory.set("EVENT_LOG_ID", eventLogID);
        eventModuleSubCategory.set("CONSENT_ID", (Object)consent_id);
        return eventModuleSubCategory;
    }
    
    protected void addCustomerEventLog(final Long eventLogID, final Long customerID) {
        final String sourceMethod = "DCEventLogUtil::addCustomerEventLog";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventLogID : " + eventLogID + " customerID : " + customerID);
        try {
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject dataObject = persistence.constructDataObject();
            if (customerID != null) {
                final Row custEventLogRow = this.getCustomerEventLogRow(eventLogID, customerID);
                dataObject.addRow(custEventLogRow);
                persistence.add(dataObject);
                DCEventLogUtil.logger.log(Level.FINEST, sourceMethod + " --> added row : ", custEventLogRow);
            }
        }
        catch (final Exception e) {
            DCEventLogUtil.logger.log(Level.WARNING, sourceMethod + " --> Caught expection while adding customer eventlog entry : " + e);
        }
    }
    
    public Long addEventLogEntry(final int eventID, final String userName, final String remarks, final Object remarksArgs) {
        final String sourceMethod = "DCEventLogUtil::addEventLogEntry";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventID : " + eventID + " userName : " + userName + " remarks : " + remarks + " remarksArgs : " + remarksArgs);
        try {
            final Persistence persistence = SyMUtil.getPersistence();
            DataObject dataObject = persistence.constructDataObject();
            Row eventLogRow = this.getEventLogRow(eventID, userName, remarks, remarksArgs);
            dataObject.addRow(eventLogRow);
            dataObject = persistence.add(dataObject);
            eventLogRow = dataObject.getRow("EventLog");
            DCEventLogUtil.logger.log(Level.FINEST, sourceMethod + " --> added row : ", eventLogRow);
            return (Long)eventLogRow.get("EVENT_LOG_ID");
        }
        catch (final Exception e) {
            DCEventLogUtil.logger.log(Level.WARNING, sourceMethod + " --> Caught expection while adding eventlog entry : " + e);
            return null;
        }
    }
    
    public Long addEvent(final int eventID, final String userName, final HashMap resMap, final String remarks, final Object remarksArgs, final boolean updateTime) {
        final String sourceMethod = "DCEventLogUtil::addEvent";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> Method starts here, eventID : " + eventID + " userName : " + userName + " resHashMap : " + resMap + " remarks : " + remarks + " updateTime : " + updateTime);
        this.addEvent(eventID, userName, resMap, remarks, remarksArgs, updateTime, null);
        return null;
    }
    
    public Long addEvent(final int eventID, final String userName, final HashMap resMap, final String remarks, final Object remarksArgs, final boolean updateTime, final Long customerID) {
        final String sourceMethod = "DCEventLogUtil::addEvent";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> Method starts here, eventID : " + eventID + " userName : " + userName + " resHashMap : " + resMap + " remarks : " + remarks + " updateTime : " + updateTime + " customerID : " + customerID);
        this.addEvent(eventID, userName, resMap, remarks, remarksArgs, updateTime, customerID, null);
        return null;
    }
    
    public Long addEvent(final int eventID, final String userName, final HashMap resMap, final String remarks, final Object remarksArgs, final boolean updateTime, final Long customerID, final Long consent_id) {
        final String sourceMethod = "DCEventLogUtil::addEvent";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> Method starts here, eventID : " + eventID + " userName : " + userName + " resHashMap : " + resMap + " remarks : " + remarks + " updateTime : " + updateTime + " customerID : " + customerID + "content id" + consent_id);
        final long currentTime = System.currentTimeMillis();
        Long resourceID = null;
        String computerName = null;
        String domainName = null;
        String reasonMessage = null;
        String promptStatus = null;
        if (resMap != null) {
            if (resMap.get("resourceID") != null) {
                resourceID = resMap.get("resourceID");
            }
            if (resMap.get("computerName") != null) {
                computerName = resMap.get("computerName");
            }
            if (resMap.get("domainName") != null) {
                domainName = resMap.get("domainName");
            }
            if (resMap.get("reasonMessage") != null) {
                reasonMessage = resMap.get("reasonMessage");
            }
            if (resMap.get("promptStatus") != null) {
                promptStatus = resMap.get("promptStatus");
                if (promptStatus.equals("1")) {
                    promptStatus = "Enabled";
                }
                else {
                    promptStatus = "Disabled";
                }
            }
        }
        try {
            DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            DCEventLogUtil.logger.log(Level.FINEST, sourceMethod + " --> dataObject before : ", dataObject);
            Row eventLogRow = this.getEventLogRow(eventID, userName, remarks, remarksArgs);
            dataObject.addRow(eventLogRow);
            final Object eventLogId = eventLogRow.get("EVENT_LOG_ID");
            if (updateTime) {
                final Row eventTDRow = this.getEventTimeDurationRow(eventLogId, currentTime, currentTime, null, promptStatus);
                dataObject.addRow(eventTDRow);
            }
            if (resourceID != null) {
                final Row eventResRelRow = this.getResourceEventLogRelRow(eventLogId, resourceID, computerName, domainName);
                dataObject.addRow(eventResRelRow);
            }
            if (reasonMessage != null && CustomerInfoUtil.isDC()) {
                final Row eventRDSRow = ApiFactoryProvider.getEventLoggerAPI().getRDSConnectionReasonRow(eventLogId, reasonMessage);
                dataObject.addRow(eventRDSRow);
            }
            if (customerID != null) {
                final Row custEventLogRow = this.getCustomerEventLogRow(eventLogId, customerID);
                dataObject.addRow(custEventLogRow);
            }
            if (consent_id != null) {
                final Row consentEventLogRow = this.getEventLogAndConsentSRelRow(eventLogId, consent_id);
                dataObject.addRow(consentEventLogRow);
            }
            final Persistence persistence = SyMUtil.getPersistence();
            DCEventLogUtil.logger.log(Level.FINEST, sourceMethod + " --> dataObject after : ", dataObject);
            dataObject = persistence.add(dataObject);
            eventLogRow = dataObject.getRow("EventLog");
            this.updateEventLogDataToSummaryServer((Long)eventLogRow.get("EVENT_LOG_ID"), eventID, userName, remarks, remarksArgs, updateTime, customerID, consent_id);
            return (Long)eventLogRow.get("EVENT_LOG_ID");
        }
        catch (final Exception e) {
            DCEventLogUtil.logger.log(Level.WARNING, sourceMethod + " --> Caught exception while add/update event log entry : ", e);
            return null;
        }
    }
    
    public Long addEventForAll(final int eventID, final String userName, final HashMap resMap, final String remarks, final List<Object> remarksArgs, final boolean updateTime, final Long customerID) {
        this.addEventForAll(eventID, userName, resMap, remarks, remarksArgs, updateTime, customerID, null);
        return null;
    }
    
    public Long addEventForAll(final int eventID, final String userName, final HashMap resMap, final String remarks, final List<Object> remarksArgs, final boolean updateTime, final Long customerID, final Long consent_id) {
        return this.addEventForAll(eventID, userName, resMap, remarks, remarksArgs, updateTime, customerID, consent_id, null);
    }
    
    public Long addEventForAll(final int eventID, final String userName, final HashMap resMap, final String remarks, final List<Object> remarksArgs, final boolean updateTime, final Long customerID, final Long consent_id, final Long eventTimeStamp) {
        final long starttime = System.currentTimeMillis();
        final String sourceMethod = "DCEventLogUtil::addEventForAll";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> Method starts here, eventID : " + eventID + " userName : " + userName + " resHashMap : " + resMap + " remarks : " + remarks + " updateTime : " + updateTime + " customerID : " + customerID + " content id : " + consent_id);
        final long currentTime = System.currentTimeMillis();
        String computerName = null;
        String domainName = null;
        String promptStatus = null;
        List resourceIds = null;
        if (resMap != null) {
            if (resMap.get("resourceIDs") != null) {
                resourceIds = ((resMap.get("resourceIDs") != null) ? resMap.get("resourceIDs") : new ArrayList());
            }
            if (resMap.get("computerName") != null) {
                computerName = resMap.get("computerName");
            }
            if (resMap.get("domainName") != null) {
                domainName = resMap.get("domainName");
            }
            if (resMap.get("promptStatus") != null) {
                promptStatus = resMap.get("promptStatus");
                if (promptStatus.equals("1")) {
                    promptStatus = "Enabled";
                }
                else {
                    promptStatus = "Disabled";
                }
            }
        }
        try {
            for (int batchStart = 0; batchStart < remarksArgs.size(); batchStart += 1000) {
                final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
                final int batchend = (remarksArgs.size() > batchStart + 1000) ? (batchStart + 1000) : remarksArgs.size();
                DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> dataObject before : ", dataObject);
                final List eventLogId = this.addEventLogRow(eventID, userName, remarks, remarksArgs.subList(batchStart, batchend), dataObject, eventTimeStamp);
                if (updateTime) {
                    this.addEventTimeDurationRow(eventLogId, currentTime, currentTime, null, promptStatus, dataObject);
                }
                if (resourceIds.size() > 0) {
                    this.addResourceEventLogRelRow(eventLogId, resourceIds.subList(batchStart, batchend), computerName, domainName, dataObject);
                }
                if (customerID != null) {
                    this.addCustomerEventLogRow(eventLogId, customerID, dataObject);
                }
                final Persistence persistence = SyMUtil.getPersistence();
                DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> dataObject after : {0}", new Object[] { dataObject.toString().length() });
                persistence.add(dataObject);
            }
            DCEventLogUtil.logger.log(Level.INFO, "Time Taken For Log Entry - {0}, No.Of Rows - {1}", new Object[] { System.currentTimeMillis() - starttime, remarksArgs.size() });
        }
        catch (final Exception e) {
            DCEventLogUtil.logger.log(Level.WARNING, sourceMethod + " --> Caught expection while add/update eventlog entry : ", e);
        }
        return null;
    }
    
    protected List addEventLogRow(final int eventID, final String userName, final String remarks, final List<Object> remarksArgsList, final DataObject dataObject) throws DataAccessException {
        return this.addEventLogRow(eventID, userName, remarks, remarksArgsList, dataObject, null);
    }
    
    protected List addEventLogRow(final int eventID, final String userName, final String remarks, final List<Object> remarksArgsList, final DataObject dataObject, final Long eventTimeStamp) throws DataAccessException {
        final String sourceMethod = "DCEventLogUtil::addEventLogRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventID : " + eventID + " userName : " + userName + " remarks : " + remarks + " remarksArgsList Size : " + remarksArgsList.size());
        final List<Object> eventLogIds = new ArrayList<Object>();
        for (Object remarksArgs : remarksArgsList) {
            final long currentTime = System.currentTimeMillis();
            final Row eventLogRow = new Row("EventLog");
            eventLogRow.set("EVENT_ID", (Object)new Integer(eventID));
            eventLogRow.set("LOGON_USER_NAME", (Object)userName);
            eventLogRow.set("EVENT_TIMESTAMP", (Object)((eventTimeStamp != null) ? eventTimeStamp : new Long(currentTime)));
            eventLogRow.set("EVENT_REMARKS", (Object)remarks);
            if (remarksArgs != null && remarksArgs.toString().length() > 1999) {
                remarksArgs = remarksArgs.toString().substring(0, 1999);
            }
            eventLogRow.set("EVENT_REMARKS_EN", (Object)I18NUtil.transformRemarksInEnglish(remarks, (String)remarksArgs));
            eventLogRow.set("EVENT_REMARKS_ARGS", remarksArgs);
            eventLogRow.set("EVENT_SOURCE_IP", (Object)EventLogThreadLocal.getSourceIpAddress());
            eventLogRow.set("EVENT_SOURCE_HOSTNAME", (Object)getHostNameForEventSource());
            dataObject.addRow(eventLogRow);
            eventLogIds.add(eventLogRow.get("EVENT_LOG_ID"));
        }
        return eventLogIds;
    }
    
    protected void addEventTimeDurationRow(final List eventlogIDs, final Long startTime, final Long endTime, final String viewerIP, final String promptStatus, final DataObject dataObject) throws DataAccessException {
        final String sourceMethod = "DCEventLogUtil::addEventTimeDurationRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventlogIDList Size : " + eventlogIDs.size() + " startTime : " + startTime + " endTime : " + endTime);
        for (final Object eventlogID : eventlogIDs) {
            final Row eventTDRow = new Row("EventTimeDuration");
            eventTDRow.set("EVENT_LOG_ID", eventlogID);
            eventTDRow.set("EVENT_START_TIME", (Object)new Long(startTime));
            if (endTime != null) {
                eventTDRow.set("EVENT_END_TIME", (Object)new Long(endTime));
                final long timeDiff = endTime - startTime;
                final int timeDuration = (timeDiff / 60000L).intValue();
                eventTDRow.set("EVENT_TIME_DURATION", (Object)new Integer(timeDuration));
            }
            if (viewerIP != null) {
                eventTDRow.set("VIEWER_IP", (Object)viewerIP);
            }
            dataObject.addRow(eventTDRow);
        }
    }
    
    protected void addResourceEventLogRelRow(final List eventlogIDs, final List resourceIDs, final String computerName, final String domainName, final DataObject dataObject) throws DataAccessException {
        final String sourceMethod = "DCEventLogUtil::addResourceEventLogRelRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventlogIDList size : " + eventlogIDs.size() + " resourceIDList Size : " + resourceIDs.size());
        for (int i = 0; i < resourceIDs.size(); ++i) {
            final Row eventResRelRow = new Row("ResourceEventLogRel");
            eventResRelRow.set("EVENT_LOG_ID", eventlogIDs.get(i));
            eventResRelRow.set("RESOURCE_ID", resourceIDs.get(i));
            if (computerName != null) {
                eventResRelRow.set("RESOURCE_NAME", (Object)computerName);
            }
            if (domainName != null) {
                eventResRelRow.set("DOMAIN_NETBIOS_NAME", (Object)domainName);
            }
            dataObject.addRow(eventResRelRow);
        }
    }
    
    protected void addCustomerEventLogRow(final List eventLogIDs, final Long customerID, final DataObject dataObject) throws DataAccessException {
        final String sourceMethod = "DCEventLogUtil::addCustomerEventLogRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventLogIDList Size : " + eventLogIDs.size() + " customerID : " + customerID);
        for (final Object eventLogID : eventLogIDs) {
            final Row custEventLogRow = new Row("CustomerEventLog");
            custEventLogRow.set("EVENT_LOG_ID", eventLogID);
            custEventLogRow.set("CUSTOMER_ID", (Object)customerID);
            dataObject.addRow(custEventLogRow);
        }
    }
    
    public void addorUpdateEvent(final Long eventLogID, final int eventID, final String userName, final HashMap resMap, final String remarks, final Object remarksArgs, final Boolean updateTime, final Long customerId) {
        final String sourceMethod = "DCEventLogUtil::addorUpdateEvent";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> Method starts here, eventLogID" + eventLogID + "eventID : " + eventID + " userName : " + userName + " resHashMap : " + resMap + " remarks : " + remarks + " updateTime : " + updateTime);
        Long resourceID = null;
        String computerName = null;
        String domainName = null;
        if (resMap != null) {
            if (resMap.get("resourceID") != null) {
                resourceID = resMap.get("resourceID");
            }
            if (resMap.get("computerName") != null) {
                computerName = resMap.get("computerName");
            }
            if (resMap.get("domainName") != null) {
                domainName = resMap.get("domainName");
            }
        }
        final long currentTime = System.currentTimeMillis();
        boolean update = false;
        try {
            DataObject dataObject = null;
            if (eventLogID != null) {
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EventLog"));
                query.addJoin(new Join("EventLog", "EventTimeDuration", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1));
                query.addJoin(new Join("EventLog", "ResourceEventLogRel", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1));
                query.addJoin(new Join("EventLog", "CustomerEventLog", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1));
                final Criteria criteria = new Criteria(Column.getColumn("EventLog", "EVENT_LOG_ID"), (Object)eventLogID, 0);
                query.addSelectColumn(Column.getColumn((String)null, "*"));
                query.addSortColumn(new SortColumn("EventLog", "EVENT_LOG_ID", false));
                query.setCriteria(criteria);
                dataObject = SyMUtil.getPersistence().get(query);
            }
            else {
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EventLog"));
                Criteria criteria = new Criteria(Column.getColumn("EventLog", "EVENT_ID"), (Object)eventID, 0);
                if (userName != null) {
                    final Criteria userCrit = new Criteria(Column.getColumn("EventLog", "LOGON_USER_NAME"), (Object)userName, 0);
                    criteria = criteria.and(userCrit);
                }
                if (updateTime) {
                    query.addJoin(new Join("EventLog", "EventTimeDuration", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 2));
                }
                if (resourceID != null) {
                    query.addJoin(new Join("EventLog", "ResourceEventLogRel", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 2));
                    final Criteria resCrit = new Criteria(Column.getColumn("ResourceEventLogRel", "RESOURCE_ID"), (Object)resourceID, 0);
                    criteria = criteria.and(resCrit);
                }
                if (customerId != null) {
                    query.addJoin(new Join("EventLog", "CustomerEventLog", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 2));
                    final Criteria custCrit = new Criteria(Column.getColumn("CustomerEventLog", "CUSTOMER_ID"), (Object)customerId, 0);
                    criteria = criteria.and(custCrit);
                }
                query.addSelectColumn(Column.getColumn((String)null, "*"));
                query.addSortColumn(new SortColumn("EventLog", "EVENT_LOG_ID", false));
                query.setCriteria(criteria);
                dataObject = SyMUtil.getPersistence().get(query);
            }
            DCEventLogUtil.logger.log(Level.FINEST, sourceMethod + " --> dataObject before : ", dataObject);
            String tableName = "EventLog";
            Row eventLogRow = dataObject.getRow(tableName);
            if (eventLogRow == null) {
                eventLogRow = this.getEventLogRow(eventID, userName, remarks, remarksArgs);
                dataObject.addRow(eventLogRow);
            }
            else {
                if (remarks != null) {
                    eventLogRow.set("EVENT_REMARKS", (Object)remarks);
                    eventLogRow.set("EVENT_REMARKS_ARGS", remarksArgs);
                    eventLogRow.set("EVENT_REMARKS_EN", (Object)I18NUtil.transformRemarksInEnglish(remarks, (String)remarksArgs));
                }
                dataObject.updateRow(eventLogRow);
                update = true;
            }
            tableName = "EventTimeDuration";
            if (updateTime) {
                Row eventTDRow = dataObject.getRow(tableName);
                if (eventTDRow == null) {
                    eventTDRow = this.getEventTimeDurationRow(eventLogRow.get("EVENT_LOG_ID"), currentTime, currentTime, null, null);
                    dataObject.addRow(eventTDRow);
                }
                else {
                    final long startTime = (long)eventTDRow.get("EVENT_START_TIME");
                    eventTDRow = this.getEventTimeDurationRow(eventLogRow.get("EVENT_LOG_ID"), startTime, currentTime, null, null);
                    dataObject.updateRow(eventTDRow);
                    update = true;
                }
            }
            tableName = "ResourceEventLogRel";
            if (resourceID != null) {
                Row eventResRelRow = dataObject.getRow(tableName);
                if (eventResRelRow == null) {
                    eventResRelRow = this.getResourceEventLogRelRow(eventLogRow.get("EVENT_LOG_ID"), resourceID, computerName, domainName);
                    dataObject.addRow(eventResRelRow);
                }
            }
            if (customerId != null) {
                Row custEventLogRow = dataObject.getRow("CustomerEventLog");
                if (custEventLogRow == null) {
                    custEventLogRow = this.getCustomerEventLogRow(eventLogRow.get("EVENT_LOG_ID"), customerId);
                    dataObject.addRow(custEventLogRow);
                }
            }
            final Persistence persistence = SyMUtil.getPersistence();
            DCEventLogUtil.logger.log(Level.FINEST, sourceMethod + " --> dataObject after : ", dataObject);
            if (!update) {
                persistence.add(dataObject);
            }
            else {
                persistence.update(dataObject);
            }
            this.updateEventLogDataToSummaryServer(eventLogID, eventID, userName, remarks, remarksArgs, updateTime, customerId, null);
        }
        catch (final Exception e) {
            DCEventLogUtil.logger.log(Level.WARNING, sourceMethod + " --> Caught expection while add/update eventlog entry : " + e);
        }
    }
    
    protected Row getEventLogRow(final int eventID, final String userName, final String remarks) {
        final String sourceMethod = "DCEventLogUtil::getEventLogRow";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> eventID : " + eventID + " userName : " + userName + " remarks : " + remarks);
        final long currentTime = System.currentTimeMillis();
        final Row eventLogRow = new Row("EventLog");
        eventLogRow.set("EVENT_ID", (Object)new Integer(eventID));
        eventLogRow.set("LOGON_USER_NAME", (Object)userName);
        eventLogRow.set("EVENT_TIMESTAMP", (Object)new Long(currentTime));
        eventLogRow.set("EVENT_REMARKS", (Object)remarks);
        eventLogRow.set("EVENT_REMARKS_EN", (Object)I18NUtil.transformRemarksInEnglish(remarks, null));
        eventLogRow.set("EVENT_SOURCE_IP", (Object)EventLogThreadLocal.getSourceIpAddress());
        eventLogRow.set("EVENT_SOURCE_HOSTNAME", (Object)getHostNameForEventSource());
        return eventLogRow;
    }
    
    public Long addEvent(final int eventID, final String userName, final HashMap resMap, final String remarks, final boolean updateTime, final Long customerID) {
        final String sourceMethod = "DCEventLogUtil::addEvent";
        DCEventLogUtil.logger.log(Level.INFO, sourceMethod + " --> Method starts here, eventID : " + eventID + " userName : " + userName + " resHashMap : " + resMap + " remarks : " + remarks + " updateTime : " + updateTime + " customerID : " + customerID);
        final long currentTime = System.currentTimeMillis();
        Long resourceID = null;
        String computerName = null;
        String domainName = null;
        String reasonMessage = null;
        String promptStatus = null;
        if (resMap != null) {
            if (resMap.get("resourceID") != null) {
                resourceID = resMap.get("resourceID");
            }
            if (resMap.get("computerName") != null) {
                computerName = resMap.get("computerName");
            }
            if (resMap.get("domainName") != null) {
                domainName = resMap.get("domainName");
            }
            if (resMap.get("reasonMessage") != null) {
                reasonMessage = resMap.get("reasonMessage");
            }
            if (resMap.get("promptStatus") != null) {
                promptStatus = resMap.get("promptStatus");
                if (promptStatus.equals("1")) {
                    promptStatus = "Enabled";
                }
                else {
                    promptStatus = "Disabled";
                }
            }
        }
        try {
            DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            DCEventLogUtil.logger.log(Level.FINEST, sourceMethod + " --> dataObject before : ", dataObject);
            Row eventLogRow = this.getEventLogRow(eventID, userName, remarks);
            dataObject.addRow(eventLogRow);
            final Object eventLogId = eventLogRow.get("EVENT_LOG_ID");
            if (updateTime) {
                final Row eventTDRow = this.getEventTimeDurationRow(eventLogId, currentTime, currentTime, null, promptStatus);
                dataObject.addRow(eventTDRow);
            }
            if (resourceID != null) {
                final Row eventResRelRow = this.getResourceEventLogRelRow(eventLogId, resourceID, computerName, domainName);
                dataObject.addRow(eventResRelRow);
            }
            if (reasonMessage != null && CustomerInfoUtil.isDC()) {
                final Row eventRDSRow = ApiFactoryProvider.getEventLoggerAPI().getRDSConnectionReasonRow(eventLogId, reasonMessage);
                dataObject.addRow(eventRDSRow);
            }
            if (customerID != null) {
                final Row custEventLogRow = this.getCustomerEventLogRow(eventLogId, customerID);
                dataObject.addRow(custEventLogRow);
            }
            final Persistence persistence = SyMUtil.getPersistence();
            DCEventLogUtil.logger.log(Level.FINEST, sourceMethod + " --> dataObject after : ", dataObject);
            dataObject = persistence.add(dataObject);
            eventLogRow = dataObject.getRow("EventLog");
            return (Long)eventLogRow.get("EVENT_LOG_ID");
        }
        catch (final Exception e) {
            DCEventLogUtil.logger.log(Level.WARNING, sourceMethod + " --> Caught expection while add/update eventlog entry : " + e);
            return null;
        }
    }
    
    public static String getHostNameForEventSource() {
        try {
            final String requestIpAddress = EventLogThreadLocal.getSourceIpAddress();
            String requestHostName = EventLogThreadLocal.getSourceHostName();
            if (requestHostName != null && !requestHostName.isEmpty()) {
                if (!requestIpAddress.equalsIgnoreCase(requestHostName)) {
                    return requestHostName;
                }
                if (requestIpAddress.equals("127.0.0.1") || requestIpAddress.equals("::1")) {
                    return InetAddress.getLocalHost().getHostName();
                }
                final byte[] ipAddrInBytes = asBytes(requestIpAddress);
                if (ipAddrInBytes != null) {
                    final InetAddress inetAddress = InetAddress.getByAddress(ipAddrInBytes);
                    requestHostName = inetAddress.getHostName();
                    if (!requestIpAddress.equalsIgnoreCase(requestHostName)) {
                        return requestHostName;
                    }
                    if (inetAddress instanceof Inet6Address) {
                        final Inet6Address inet6Address = (Inet6Address)InetAddress.getByAddress(ipAddrInBytes);
                        requestHostName = inet6Address.getHostName();
                        final String requestIp6Address = inetAddress.getHostAddress();
                        if (!requestIp6Address.equalsIgnoreCase(requestHostName) && !requestIpAddress.equalsIgnoreCase(requestHostName)) {
                            return requestHostName;
                        }
                    }
                }
            }
        }
        catch (final UnknownHostException unknownHostEx) {
            return "--";
        }
        catch (final Exception e) {
            DCEventLogUtil.logger.log(Level.WARNING, "Exception occured while resolving hostname for Ipaddress: " + e);
        }
        return "--";
    }
    
    public static final byte[] asBytes(final String addr) {
        final int ipInt = parseNumericAddress(addr);
        if (ipInt == 0) {
            return null;
        }
        final byte[] ipByts = { (byte)(ipInt >> 24 & 0xFF), (byte)(ipInt >> 16 & 0xFF), (byte)(ipInt >> 8 & 0xFF), (byte)(ipInt & 0xFF) };
        return ipByts;
    }
    
    public static final int parseNumericAddress(final String ipaddr) {
        if (ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15) {
            return 0;
        }
        final StringTokenizer token = new StringTokenizer(ipaddr, ".");
        if (token.countTokens() != 4) {
            return 0;
        }
        int ipInt = 0;
        while (token.hasMoreTokens()) {
            final String ipNum = token.nextToken();
            try {
                final int ipVal = Integer.valueOf(ipNum);
                if (ipVal < 0 || ipVal > 255) {
                    return 0;
                }
                ipInt = (ipInt << 8) + ipVal;
            }
            catch (final NumberFormatException ex) {
                return 0;
            }
        }
        return ipInt;
    }
    
    private void updateEventLogDataToSummaryServer(final Long eventLogID, final int eventID, final String userName, final String remarks, final Object remarksArgs, final Boolean updateTime, final Long customerID, final Long consent_id) {
        try {
            EventLogFactoryProvider.getEventLogUtil().updateEventLogDataToSummaryServer(eventLogID, eventID, userName, remarks, remarksArgs, updateTime, customerID, consent_id);
        }
        catch (final Exception e) {
            DCEventLogUtil.logger.log(Level.WARNING, "Exception while updateEventLogDataToSummaryServer");
        }
    }
    
    static {
        DCEventLogUtil.logger = Logger.getLogger("EventLogLogger");
        DCEventLogUtil.dceventlog = null;
    }
}

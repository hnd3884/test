package com.me.idps.core.sync.events;

import java.util.Hashtable;
import com.adventnet.db.api.RelationalAPI;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.util.concurrent.TimeUnit;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.idps.core.sync.synch.DirectoryMetricsDataHandler;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Set;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.me.idps.core.util.IdpsJSONutil;
import org.json.simple.JSONArray;
import java.text.MessageFormat;
import com.me.idps.core.util.DirectoryUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.idps.core.util.DirQueue;

public class DirectoryEventsHandler extends DirQueue
{
    public long getPartitionFeedId(final DCQueueData qData) {
        long domainID = 0L;
        try {
            final JSONObject qNode = (JSONObject)new JSONParser().parse((String)qData.queueData);
            if (qNode.containsKey((Object)"DOMAIN_ID")) {
                final Object domainIDobj = qNode.getOrDefault((Object)"DOMAIN_ID", (Object)null);
                domainID = Long.valueOf(String.valueOf(domainIDobj));
            }
        }
        catch (final Exception ex) {
            domainID = 0L;
            IDPSlogger.ERR.log(Level.FINE, "could not get domainID", ex);
        }
        return domainID;
    }
    
    public boolean isParallelProcessingQueue() {
        return false;
    }
    
    private String getMetricsEventKey(final IdpEventConstants eventType) {
        switch (eventType) {
            case MEMBER_ADDED_EVENT: {
                return "ADDED_EVENT_COUNT";
            }
            case MEMBER_REMOVED_EVENT: {
                return "REMOVED_EVENT_COUNT";
            }
            case USER_MODIFIED_EVENT: {
                return "MODIFIED_EVENT_COUNT";
            }
            case GROUP_MODIFIED_EVENT: {
                return "GROUP_MODIFIED_EVENT_COUNT";
            }
            case USER_DELETED_EVENT: {
                return "USER_DELETED_EVENT_COUNT";
            }
            case USER_ACTIVATED_EVENT: {
                return "USER_ACTIVATED_EVENT_COUNT";
            }
            case USER_DIR_DISABLED_EVENT: {
                return "USER_DIR_DISABLED_EVENT_COUNT";
            }
            case USER_SYNC_DISABLED_EVENT: {
                return "USER_SYNC_DISABLED_EVENT_COUNT";
            }
            case GROUP_DELETED_EVENT: {
                return "GROUP_DELETED_EVENT_COUNT";
            }
            case GROUP_ACTIVATED_EVENT: {
                return "GROUP_ACTIVATED_EVENT_COUNT";
            }
            case GROUP_DIR_DISABLED_EVENT: {
                return "GROUP_DIR_DISABLED_EVENT_COUNT";
            }
            case GROUP_SYNC_DISABLED_EVENT: {
                return "GROUP_SYNC_DISABLED_EVENT_COUNT";
            }
            default: {
                return "NA";
            }
        }
    }
    
    private String getHumanReadableEventType(final IdpEventConstants eventType) throws Exception {
        switch (eventType) {
            case MEMBER_ADDED_EVENT: {
                return I18N.getMsg("dir.member.add", new Object[0]);
            }
            case MEMBER_REMOVED_EVENT: {
                return I18N.getMsg("dir.member.remove", new Object[0]);
            }
            case USER_MODIFIED_EVENT: {
                return I18N.getMsg("dir.user.modify", new Object[0]);
            }
            case GROUP_MODIFIED_EVENT: {
                return I18N.getMsg("dir.group.modify", new Object[0]);
            }
            case USER_DELETED_EVENT: {
                return I18N.getMsg("dir.user.delete", new Object[0]);
            }
            case USER_ACTIVATED_EVENT: {
                return I18N.getMsg("dir.user.activate", new Object[0]);
            }
            case USER_DIR_DISABLED_EVENT: {
                return I18N.getMsg("dir.user.dir.disable", new Object[0]);
            }
            case USER_SYNC_DISABLED_EVENT: {
                return I18N.getMsg("dir.user.sync.disable", new Object[0]);
            }
            case GROUP_DELETED_EVENT: {
                return I18N.getMsg("dir.group.delete", new Object[0]);
            }
            case GROUP_ACTIVATED_EVENT: {
                return I18N.getMsg("dir.group.activate", new Object[0]);
            }
            case GROUP_DIR_DISABLED_EVENT: {
                return I18N.getMsg("dir.group.dir.disable", new Object[0]);
            }
            case GROUP_SYNC_DISABLED_EVENT: {
                return I18N.getMsg("dir.group.sync.disable", new Object[0]);
            }
            default: {
                return "NA";
            }
        }
    }
    
    private JSONObject generateEventsDurationJSobj(final JSONObject eventDurationLog, final int eventsCount, final long duration, final String eventTypeStr, final Properties dmDomainProps) {
        if (eventsCount > 0 && !SyMUtil.isStringEmpty(eventTypeStr)) {
            JSONObject eventDetails = new JSONObject();
            if (eventDurationLog.containsKey((Object)eventTypeStr)) {
                eventDetails = (JSONObject)eventDurationLog.get((Object)eventTypeStr);
            }
            int totalEventsCount = 0;
            final String eventsCountKey = "count";
            if (eventDetails.containsKey((Object)eventsCountKey)) {
                totalEventsCount = Integer.valueOf(String.valueOf(eventDetails.get((Object)eventsCountKey)));
            }
            totalEventsCount += eventsCount;
            eventDetails.put((Object)eventsCountKey, (Object)totalEventsCount);
            long totalDuration = 0L;
            if (eventDetails.containsKey((Object)"T")) {
                totalDuration = Long.valueOf(String.valueOf(eventDetails.get((Object)"T")));
            }
            totalDuration += duration;
            eventDetails.put((Object)"T", (Object)totalDuration);
            eventDurationLog.put((Object)eventTypeStr, (Object)eventDetails);
            MessageFormat.format("completed invoking listeners for {0}, {3}, eventsNum:{4}, duration:{5}", dmDomainProps.toString(), eventTypeStr, String.valueOf(eventsCount), DirectoryUtil.getInstance().formatDurationMS(duration));
        }
        return eventDurationLog;
    }
    
    private JSONObject formatRelData(final JSONObject collatedResEventDetails, final Long resID, final String eventDetail) {
        final JSONArray eventDetails = (JSONArray)collatedResEventDetails.getOrDefault((Object)resID, (Object)new JSONArray());
        eventDetails.add((Object)Long.valueOf(eventDetail));
        collatedResEventDetails.put((Object)resID, (Object)eventDetails);
        return collatedResEventDetails;
    }
    
    private JSONObject getNewModifiedFiledObj(final JSONObject jsonObject) {
        final JSONObject newModifiedFiledObj = new JSONObject();
        if (jsonObject.containsKey((Object)"NAME")) {
            final String resName = (String)jsonObject.get((Object)"NAME");
            if (!SyMUtil.isStringEmpty(resName)) {
                newModifiedFiledObj.put((Object)"NAME", (Object)resName);
            }
        }
        return newModifiedFiledObj;
    }
    
    private JSONObject formatObjModifiedData(final JSONObject collatedResEventDetails, final Long resID, final String modifiedFiled, final JSONObject jsonObject) {
        if (!SyMUtil.isStringEmpty(modifiedFiled)) {
            final JSONObject modifiedObjDetails = (JSONObject)collatedResEventDetails.getOrDefault((Object)resID, (Object)this.getNewModifiedFiledObj(jsonObject));
            final JSONArray modifiedFields = (JSONArray)modifiedObjDetails.getOrDefault((Object)"MODIFIED_FIELDS", (Object)new JSONArray());
            if (!modifiedFields.contains((Object)modifiedFiled)) {
                modifiedFields.add((Object)modifiedFiled);
                modifiedObjDetails.put((Object)"MODIFIED_FIELDS", (Object)modifiedFields);
                collatedResEventDetails.put((Object)resID, (Object)modifiedObjDetails);
            }
        }
        return collatedResEventDetails;
    }
    
    private JSONObject formatStatusChangeData(final JSONObject collatedResEventDetails, final Long resID) {
        final JSONArray resAr = (JSONArray)collatedResEventDetails.getOrDefault((Object)"RESOURCE_ID", (Object)new JSONArray());
        if (!resAr.contains((Object)resID)) {
            resAr.add((Object)resID);
            collatedResEventDetails.put((Object)"RESOURCE_ID", (Object)resAr);
        }
        return collatedResEventDetails;
    }
    
    private JSONObject collateResDetails(final JSONArray jsArray, final IdpEventConstants eventType) {
        JSONObject collatedResEventDetails = new JSONObject();
        if (jsArray != null && !jsArray.isEmpty()) {
            for (int i = 0; i < jsArray.size(); ++i) {
                final JSONObject jsonObject = (JSONObject)jsArray.get(i);
                final String eventDetail = String.valueOf(jsonObject.get((Object)"EVENT_DETAILS"));
                final Long resID = Long.valueOf(String.valueOf(jsonObject.get((Object)"RESOURCE_ID")));
                if (eventType == IdpEventConstants.MEMBER_ADDED_EVENT || eventType == IdpEventConstants.MEMBER_REMOVED_EVENT) {
                    collatedResEventDetails = this.formatRelData(collatedResEventDetails, resID, eventDetail);
                }
                else if (eventType == IdpEventConstants.USER_MODIFIED_EVENT) {
                    collatedResEventDetails = this.formatObjModifiedData(collatedResEventDetails, resID, eventDetail, jsonObject);
                }
                else if (eventType == IdpEventConstants.GROUP_MODIFIED_EVENT) {
                    if (eventDetail.equalsIgnoreCase(String.valueOf(103L))) {
                        collatedResEventDetails = this.formatObjModifiedData(collatedResEventDetails, resID, "DESCRIPTION", jsonObject);
                        collatedResEventDetails = this.formatObjModifiedData(collatedResEventDetails, resID, "GROUP_DESCRIPTION", jsonObject);
                    }
                    else {
                        collatedResEventDetails = this.formatObjModifiedData(collatedResEventDetails, resID, eventDetail, jsonObject);
                    }
                }
                else if (eventType.getEventType() >= IdpEventConstants.USER_DELETED_EVENT.getEventType() || eventType.getEventType() <= IdpEventConstants.GROUP_SYNC_DISABLED_EVENT.getEventType()) {
                    collatedResEventDetails = this.formatStatusChangeData(collatedResEventDetails, resID);
                }
            }
        }
        return collatedResEventDetails;
    }
    
    private void triggerEvents(final Properties dmDomainProps, final IdpEventConstants eventType, final JSONObject eventData, final long aaaUserID, final String userName) throws Exception {
        final Set keys = eventData.keySet();
        switch (eventType) {
            case MEMBER_ADDED_EVENT:
            case MEMBER_REMOVED_EVENT: {
                for (final Object key : keys) {
                    final Long groupResID = Long.valueOf(String.valueOf(key));
                    final Long[] membersResIDs = IdpsJSONutil.convertJSONArrayToLongArray((JSONArray)eventData.get(key));
                    final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                    dirProdImplRequest.userName = userName;
                    dirProdImplRequest.eventType = eventType;
                    dirProdImplRequest.aaaUserID = aaaUserID;
                    dirProdImplRequest.dmDomainProps = dmDomainProps;
                    dirProdImplRequest.args = new Object[] { groupResID, membersResIDs };
                    DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
                }
                break;
            }
            case USER_MODIFIED_EVENT:
            case GROUP_MODIFIED_EVENT: {
                for (final Object key : keys) {
                    final Long resID = Long.valueOf(String.valueOf(key));
                    final JSONObject modifiedObjFields = (JSONObject)eventData.get((Object)resID);
                    final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                    dirProdImplRequest.userName = userName;
                    dirProdImplRequest.eventType = eventType;
                    dirProdImplRequest.aaaUserID = aaaUserID;
                    dirProdImplRequest.dmDomainProps = dmDomainProps;
                    dirProdImplRequest.args = new Object[] { resID, modifiedObjFields };
                    DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
                }
                break;
            }
            case USER_DELETED_EVENT:
            case USER_ACTIVATED_EVENT:
            case USER_DIR_DISABLED_EVENT:
            case USER_SYNC_DISABLED_EVENT:
            case GROUP_DELETED_EVENT:
            case GROUP_ACTIVATED_EVENT:
            case GROUP_DIR_DISABLED_EVENT:
            case GROUP_SYNC_DISABLED_EVENT: {
                final DirProdImplRequest dirProdImplRequest2 = new DirProdImplRequest();
                dirProdImplRequest2.userName = userName;
                dirProdImplRequest2.eventType = eventType;
                dirProdImplRequest2.aaaUserID = aaaUserID;
                dirProdImplRequest2.dmDomainProps = dmDomainProps;
                dirProdImplRequest2.args = new Object[] { eventData.get((Object)"RESOURCE_ID") };
                DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest2);
                break;
            }
        }
    }
    
    private JSONObject invokeListener(final Connection connection, final Properties dmDomainProps, final IdpEventConstants eventType, final Long aaaUserID, final String userName, JSONObject eventDurationLog) throws Exception {
        if (eventType.getEventType() != -1) {
            final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            final Column resIDcol = Column.getColumn("DirectoryEventDetails", "RESOURCE_ID");
            final Criteria baseCri = new Criteria(Column.getColumn("DirectoryEventToken", "USER_ID"), (Object)aaaUserID, 0).and(new Criteria(Column.getColumn("DirectoryEventDetails", "EVENT_TYPE"), (Object)eventType.getEventType(), 0)).and(new Criteria(Column.getColumn("DirectoryEventToken", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirectoryEventToken", "STATUS_ID"), (Object)921, 0));
            final Join join = new Join("DirectoryEventDetails", "DirectoryEventToken", new String[] { "EVENT_TOKEN_ID" }, new String[] { "EVENT_TOKEN_ID" }, 2);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectoryEventDetails"));
            selectQuery.addJoin(join);
            selectQuery.setCriteria(baseCri);
            selectQuery.addSelectColumn(resIDcol);
            selectQuery.addSelectColumn(Column.getColumn("DirectoryEventDetails", "EVENT_DETAILS"));
            if (eventType == IdpEventConstants.USER_MODIFIED_EVENT) {
                selectQuery.addJoin(new Join("DirectoryEventDetails", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            }
            selectQuery.addSortColumn(new SortColumn(resIDcol, true));
            selectQuery.setRange(new Range(0, 10000));
            for (JSONArray events = IdpsUtil.executeSelectQuery(connection, selectQuery); events != null && !events.isEmpty(); events = IdpsUtil.executeSelectQuery(connection, selectQuery)) {
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", I18N.getMsg("mdm.dir.events.trigger", new Object[] { events.size(), this.getHumanReadableEventType(eventType) }));
                final int eventsCount = events.size();
                final JSONObject collatedResEventDetails = this.collateResDetails(events, eventType);
                final long startTime = System.currentTimeMillis();
                this.triggerEvents(dmDomainProps, eventType, collatedResEventDetails, aaaUserID, userName);
                final long endTime = System.currentTimeMillis();
                DirectoryMetricsDataHandler.getInstance().enQueueIncrementTask(((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID"), this.getMetricsEventKey(eventType), eventsCount);
                eventDurationLog = this.generateEventsDurationJSobj(eventDurationLog, eventsCount, endTime - startTime, eventType.toString(), dmDomainProps);
                final Range curRange = selectQuery.getRange();
                selectQuery.setRange(new Range(curRange.getStartIndex() + curRange.getNumberOfObjects(), 10000));
            }
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirectoryEventDetails");
            deleteQuery.addJoin(join);
            deleteQuery.setCriteria(baseCri);
            DirectoryQueryutil.getInstance().executeDeleteQuery(deleteQuery, false);
        }
        return eventDurationLog;
    }
    
    private JSONArray getEventSummary(final Connection connection, final Long dmDomainID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectoryEventToken"));
        selectQuery.addJoin(new Join("DirectoryEventToken", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("DirectoryEventToken", "DirectoryEventDetails", new String[] { "EVENT_TOKEN_ID" }, new String[] { "EVENT_TOKEN_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DirectoryEventToken", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectoryEventDetails", "EVENT_TYPE"), (Object)null, 1)).and(new Criteria(Column.getColumn("DirectoryEventToken", "STATUS_ID"), (Object)921, 0)));
        final Column userNamecol = Column.getColumn("AaaUser", "FIRST_NAME");
        final Column aaaUserIDcol = Column.getColumn("DirectoryEventToken", "USER_ID");
        final Column eventTypeCol = Column.getColumn("DirectoryEventDetails", "EVENT_TYPE");
        selectQuery.addSelectColumn(userNamecol);
        selectQuery.addSelectColumn(aaaUserIDcol);
        selectQuery.addSelectColumn(eventTypeCol);
        selectQuery.addSelectColumn(IdpsUtil.getCountOfColumn("DirectoryEventDetails", "RESOURCE_ID", "count"));
        selectQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(eventTypeCol, aaaUserIDcol, userNamecol))));
        return this.formatEventSummary(IdpsUtil.executeSelectQuery(connection, selectQuery));
    }
    
    private JSONArray formatEventSummary(final JSONArray eventsSummary) {
        final JSONArray res = new JSONArray();
        for (int i = 0; i < eventsSummary.size(); ++i) {
            final JSONObject jsonObject = (JSONObject)eventsSummary.get(i);
            final int eventType = Integer.valueOf(String.valueOf(jsonObject.get((Object)"EVENT_TYPE")));
            final JSONObject newJSONobj = new JSONObject();
            newJSONobj.put((Object)"count", jsonObject.get((Object)"count"));
            newJSONobj.put((Object)"EVENT_TYPE", (Object)eventType);
            newJSONobj.put((Object)"FIRST_NAME", jsonObject.get((Object)"FIRST_NAME"));
            newJSONobj.put((Object)"USER_ID", jsonObject.get((Object)"USER_ID"));
            newJSONobj.put((Object)"DIRECTORYEVENTDETAILS.EVENT_TYPE", (Object)IdpEventConstants.getEventType(eventType));
            res.add((Object)newJSONobj);
        }
        return res;
    }
    
    private Criteria getEventTypeCri(final int resType, final int statusVal) {
        return new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)resType, 0).and(new Criteria(Column.getColumn("DirectoryEventDetails", "EVENT_DETAILS"), (Object)String.valueOf(statusVal), 0, false));
    }
    
    private Criteria getEventTokenTypeCri(final int resType, final int eventType) {
        return new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)resType, 0).and(new Criteria(Column.getColumn("DirectoryEventToken", "EVENT_TYPE"), (Object)eventType, 0));
    }
    
    private void prepareEventData(final Connection connection, final Long dmDomainID) throws Exception {
        final Criteria nullDerivedEventTypeCri = new Criteria(Column.getColumn("DirectoryEventDetails", "EVENT_TYPE"), (Object)null, 0);
        final Criteria criteria = new Criteria(Column.getColumn("DirectoryEventToken", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectoryEventToken", "STATUS_ID"), (Object)921, 0));
        final Criteria statusChangeCri = new Criteria(Column.getColumn("DirectoryEventToken", "EVENT_TYPE"), (Object)IdpEventConstants.STATUS_CHANGE_EVENT.getEventType(), 0);
        final Join join = new Join("DirectoryEventDetails", "DirectoryEventToken", new String[] { "EVENT_TOKEN_ID" }, new String[] { "EVENT_TOKEN_ID" }, 2);
        final Criteria coreCri = criteria.and(nullDerivedEventTypeCri);
        final Criteria baseCri = coreCri.and(statusChangeCri);
        UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectoryEventDetails");
        updateQuery.addJoin(join);
        updateQuery.addJoin(new Join("DirectoryEventDetails", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(2, 1)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.USER_ACTIVATED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(2, 4)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.USER_DELETED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(2, 5)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.USER_DELETED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(2, 3)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.USER_DIR_DISABLED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(2, 2)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.USER_SYNC_DISABLED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(101, 1)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.GROUP_ACTIVATED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(101, 4)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.GROUP_DELETED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(101, 5)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.GROUP_DELETED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(101, 3)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.GROUP_DIR_DISABLED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(baseCri.and(this.getEventTypeCri(101, 2)));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.GROUP_SYNC_DISABLED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(coreCri.and(this.getEventTokenTypeCri(2, IdpEventConstants.MODIFIED_EVENT.getEventType())));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.USER_MODIFIED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery.setCriteria(coreCri.and(this.getEventTokenTypeCri(101, IdpEventConstants.MODIFIED_EVENT.getEventType())));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)IdpEventConstants.GROUP_MODIFIED_EVENT.getEventType());
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectoryEventDetails");
        updateQuery.addJoin(join);
        updateQuery.setCriteria(criteria.and(nullDerivedEventTypeCri));
        updateQuery.setUpdateColumn("EVENT_TYPE", (Object)Column.getColumn("DirectoryEventToken", "EVENT_TYPE"));
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
    }
    
    private void cleanUpEvents(final Connection connection, final Long dmDomainID) throws Exception {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirectoryEventToken");
        deleteQuery.setCriteria(new Criteria(Column.getColumn("DirectoryEventToken", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectoryEventToken", "STATUS_ID"), (Object)921, 0).or(new Criteria(Column.getColumn("DirectoryEventToken", "ADDED_AT"), (Object)(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3L)), 0))));
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
    }
    
    private JSONObject invokeListener(final Connection connection, final Long dmDomainID, final String dmDomainName, final Long customerID) throws Exception {
        JSONObject eventsLogPrint = null;
        if (dmDomainID == null) {
            throw new Exception("invalid input for invoking directory events");
        }
        final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainById(dmDomainID);
        dmDomainProps.remove("CRD_PASSWORD");
        dmDomainProps.remove("CRD_USERNAME");
        dmDomainProps.remove("CREDENTIAL_ID");
        this.prepareEventData(connection, dmDomainID);
        JSONObject eventDurationLog = new JSONObject();
        final JSONArray eventsSummary = this.getEventSummary(connection, dmDomainID);
        IDPSlogger.EVENT.log(Level.INFO, "summary of obtained events for {0},{1},{2}: {3}", new String[] { String.valueOf(dmDomainName), String.valueOf(customerID), String.valueOf(dmDomainID), IdpsUtil.getPrettyJSON(eventsSummary) });
        for (int i = 0; i < eventsSummary.size(); ++i) {
            final JSONObject jsObject = (JSONObject)eventsSummary.get(i);
            final String userName = String.valueOf(jsObject.get((Object)"FIRST_NAME"));
            final int eventCount = Integer.valueOf(String.valueOf(jsObject.get((Object)"count")));
            final Long aaaUserID = Long.valueOf(String.valueOf(jsObject.get((Object)"USER_ID")));
            final int eventType = Integer.valueOf(String.valueOf(jsObject.get((Object)"EVENT_TYPE")));
            if (eventCount > 0) {
                eventDurationLog = this.invokeListener(connection, dmDomainProps, IdpEventConstants.getEvent(eventType), aaaUserID, userName, eventDurationLog);
            }
        }
        DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "SYNC_STATUS", "");
        if (!eventDurationLog.isEmpty()) {
            final Iterator itr = eventDurationLog.keySet().iterator();
            while (itr != null && itr.hasNext()) {
                final String eventTypeStr = itr.next();
                final JSONObject eventDetails = (JSONObject)eventDurationLog.get((Object)eventTypeStr);
                final Long durationL = Long.valueOf(String.valueOf(eventDetails.get((Object)"T")));
                eventDetails.put((Object)"T", (Object)DirectoryUtil.getInstance().formatDurationMS(durationL));
                eventDurationLog.put((Object)eventTypeStr, (Object)eventDetails);
            }
            eventsLogPrint = new JSONObject();
            eventsLogPrint.put((Object)"NAME", (Object)dmDomainName);
            eventsLogPrint.put((Object)"DOMAIN_ID", (Object)dmDomainID);
            eventsLogPrint.put((Object)"CUSTOMER_ID", (Object)customerID);
            eventsLogPrint.put((Object)"events", (Object)eventDurationLog);
            IDPSlogger.AUDIT.log(Level.INFO, "events summary {0}", new Object[] { IdpsUtil.getPrettyJSON(eventsLogPrint) });
            IDPSlogger.EVENT.log(Level.INFO, "events summary {0}", new Object[] { IdpsUtil.getPrettyJSON(eventsLogPrint) });
        }
        this.cleanUpEvents(connection, dmDomainID);
        return eventsLogPrint;
    }
    
    @Override
    protected void processDirTask(final String taskType, final String dmDomainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClient, final JSONObject qData) throws Exception {
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            final JSONObject eventDetails = this.invokeListener(connection, dmDomainID, dmDomainName, customerID);
            if (qData.containsKey((Object)"DirectoryMetrics")) {
                final JSONObject metrics = (JSONObject)qData.get((Object)"DirectoryMetrics");
                if (metrics != null && eventDetails != null) {
                    qData.put((Object)"DirectoryEventDetails", (Object)eventDetails);
                }
            }
            qData.put((Object)"TASK_TYPE", (Object)"END_STATE");
            DirectoryUtil.getInstance().addTaskToQueue("adProc-task", null, qData);
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
    }
}

package com.me.mdm.server.events;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class DeviceEventDataHandler
{
    private Logger logger;
    
    public DeviceEventDataHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public Long getDeviceEventId(final String deviceEventName) {
        Long deviceEventId = null;
        final Criteria eventNameCriteria = new Criteria(Column.getColumn("DeviceEvents", "EVENT_NAME"), (Object)deviceEventName, 0);
        try {
            final DataObject deviceEventDO = MDMUtil.getPersistence().get("DeviceEvents", eventNameCriteria);
            if (!deviceEventDO.isEmpty()) {
                final Row row = deviceEventDO.getFirstRow("DeviceEvents");
                deviceEventId = (Long)row.get("DEVICE_EVENT_ID");
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getDeviceEventId", (Throwable)ex);
        }
        this.logger.log(Level.INFO, "Device Event Name : {0}, Event Id: {1}", new Object[] { deviceEventName, deviceEventId });
        return deviceEventId;
    }
    
    public DeviceEvent addSingleDeviceEvent(DeviceEvent deviceEvent) {
        deviceEvent = this.addDeviceEventHistory(deviceEvent);
        this.addOrUpdateRecentDeviceEvent(deviceEvent);
        return deviceEvent;
    }
    
    public Map<String, DeviceEvent> addMultipleDeviceEvents(final DeviceEvent[] deviceEvents) {
        final Map<String, DeviceEvent> recentDeviceEvents = new HashMap<String, DeviceEvent>();
        try {
            for (int i = 0; i < deviceEvents.length; ++i) {
                DeviceEvent deviceEvent = deviceEvents[i];
                deviceEvent = this.addDeviceEventHistory(deviceEvent);
                final Long eventTime = deviceEvent.getDeviceEventTime();
                final String deviceEventName = deviceEvent.deviceEventName;
                final Long lastEventTime = recentDeviceEvents.containsKey(deviceEventName) ? recentDeviceEvents.get(deviceEventName).getDeviceEventTime() : -1L;
                if (lastEventTime <= eventTime) {
                    recentDeviceEvents.put(deviceEventName, deviceEvent);
                }
            }
            for (final Map.Entry<String, DeviceEvent> evtRel : recentDeviceEvents.entrySet()) {
                this.addOrUpdateRecentDeviceEvent(evtRel.getValue());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addMultipleDeviceEvents", ex);
        }
        return recentDeviceEvents;
    }
    
    public DeviceEvent addDeviceEventHistory(final DeviceEvent deviceEvent) {
        Long deviceEventHistoryId = null;
        try {
            DataObject deviceEvtDO = MDMUtil.getPersistence().constructDataObject();
            final Row deviceEvtRow = new Row("DeviceEventHistory");
            deviceEvtRow.set("RESOURCE_ID", (Object)deviceEvent.resourceId);
            deviceEvtRow.set("DEVICE_EVENT_ID", (Object)deviceEvent.eventId);
            final Long eventTime = deviceEvent.getDeviceEventTime();
            if (eventTime != null) {
                deviceEvtRow.set("EVENT_TIME", (Object)eventTime);
            }
            deviceEvtDO.addRow(deviceEvtRow);
            deviceEvtDO = MDMUtil.getPersistence().add(deviceEvtDO);
            deviceEventHistoryId = (Long)deviceEvtDO.getFirstRow("DeviceEventHistory").get("EVENT_HISTORY_ID");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addDeviceEventHistory", ex);
        }
        this.logger.log(Level.INFO, "Event Added to history {0}, EVENTHISTORYID:{1}", new Object[] { deviceEvent.toString(), deviceEventHistoryId });
        return deviceEvent.addDeviceEventHistoryId(deviceEventHistoryId);
    }
    
    public void addOrUpdateRecentDeviceEvent(final DeviceEvent deviceEvent) {
        try {
            final Long deviceEventHistoryId = deviceEvent.getDeviceEventHistoryId();
            if (deviceEventHistoryId != null) {
                final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentDeviceEvents", "RESOURCE_ID"), (Object)deviceEvent.resourceId, 0);
                final Criteria eventCriteria = new Criteria(Column.getColumn("RecentDeviceEvents", "DEVICE_EVENT_ID"), (Object)deviceEvent.eventId, 0);
                final Criteria criteria = resourceCriteria.and(eventCriteria);
                final DataObject recentDeviceEvtDO = MDMUtil.getPersistence().get("RecentDeviceEvents", criteria);
                if (recentDeviceEvtDO.isEmpty()) {
                    final Row recentDeviceEventRow = new Row("RecentDeviceEvents");
                    recentDeviceEventRow.set("RESOURCE_ID", (Object)deviceEvent.resourceId);
                    recentDeviceEventRow.set("DEVICE_EVENT_ID", (Object)deviceEvent.eventId);
                    recentDeviceEventRow.set("EVENT_HISTORY_ID", (Object)deviceEvent.getDeviceEventHistoryId());
                    recentDeviceEvtDO.addRow(recentDeviceEventRow);
                    MDMUtil.getPersistence().add(recentDeviceEvtDO);
                    this.logger.log(Level.INFO, "Recent Device Event Added;ResourceId: {0}, EventHistoryID:{1}", new Object[] { deviceEvent.resourceId, deviceEvent.getDeviceEventHistoryId() });
                }
                else {
                    final Row recentDeviceEventRow = recentDeviceEvtDO.getFirstRow("RecentDeviceEvents");
                    recentDeviceEventRow.set("EVENT_HISTORY_ID", (Object)deviceEvent.getDeviceEventHistoryId());
                    recentDeviceEvtDO.updateRow(recentDeviceEventRow);
                    MDMUtil.getPersistence().update(recentDeviceEvtDO);
                    this.logger.log(Level.INFO, "Recent Device Event Added;ResourceId: {0}, EventHistoryID:{1}", new Object[] { deviceEvent.resourceId, deviceEvent.getDeviceEventHistoryId() });
                }
            }
            else {
                this.logger.log(Level.INFO, "Recent Device events can not add. Because EVENT_HISTORY_ID is null for ResourceId:{0}", deviceEvent.resourceId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateRecentDeviceEvent", ex);
        }
    }
    
    public Map<String, DeviceEvent> getRecentDeviceEvents(final Long resourceId) {
        Map<String, DeviceEvent> deviceEventsMap = null;
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("DeviceEventHistory", "RESOURCE_ID"), (Object)resourceId, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEvents"));
            final Join devEvtHistoryJoin = new Join("DeviceEvents", "DeviceEventHistory", new String[] { "DEVICE_EVENT_ID" }, new String[] { "DEVICE_EVENT_ID" }, 2);
            final Join recDevEvtJoin = new Join("DeviceEventHistory", "RecentDeviceEvents", new String[] { "EVENT_HISTORY_ID" }, new String[] { "EVENT_HISTORY_ID" }, 2);
            selectQuery.addJoin(devEvtHistoryJoin);
            selectQuery.addJoin(recDevEvtJoin);
            selectQuery.setCriteria(resourceCriteria);
            selectQuery.addSelectColumn(Column.getColumn("DeviceEvents", "DEVICE_EVENT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEvents", "EVENT_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEventHistory", "EVENT_HISTORY_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEventHistory", "DEVICE_EVENT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEventHistory", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEventHistory", "EVENT_TIME"));
            final DataObject deviceEventDO = MDMUtil.getPersistence().get("RecentDeviceEvents", resourceCriteria);
            if (!deviceEventDO.isEmpty()) {
                deviceEventsMap = new HashMap<String, DeviceEvent>();
                final Iterator devEvtIterator = deviceEventDO.getRows("DeviceEvents");
                while (devEvtIterator.hasNext()) {
                    final Row deviceEventRow = devEvtIterator.next();
                    final String deviceEventName = (String)deviceEventRow.get("EVENT_NAME");
                    final Long deviceEventId = (Long)deviceEventRow.get("DEVICE_EVENT_ID");
                    final DeviceEvent deviceEvent = new DeviceEvent(deviceEventName, deviceEventId);
                    deviceEventsMap.put(deviceEventName, deviceEvent);
                }
                for (final Map.Entry<String, DeviceEvent> devEvents : deviceEventsMap.entrySet()) {
                    final String eventName = devEvents.getKey();
                    final DeviceEvent deviceEvent = devEvents.getValue();
                    final Criteria evtCriteria = new Criteria(Column.getColumn("DeviceEventHistory", "DEVICE_EVENT_ID"), (Object)deviceEvent.eventId, 0);
                    final Row deviceEventHistoryRow = deviceEventDO.getRow("DeviceEventHistory", evtCriteria);
                    deviceEvent.addResourceId((Long)deviceEventHistoryRow.get("RESOURCE_ID"));
                    deviceEvent.addDeviceEventHistoryId((Long)deviceEventHistoryRow.get("EVENT_HISTORY_ID"));
                    deviceEvent.addEventTime((Long)deviceEventHistoryRow.get("EVENT_TIME"));
                    deviceEventsMap.put(eventName, deviceEvent);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getRecentDeviceEvents", ex);
        }
        return deviceEventsMap;
    }
    
    public void handleDeviceEventsUpdate(final Long resourceID, final JSONObject devResData) {
        try {
            final JSONArray deviceEventArr = devResData.getJSONArray("DeviceEvents");
            final DeviceEvent[] deviceEvents = this.prepareDeviceEventUpdate(resourceID, deviceEventArr).toArray(new DeviceEvent[0]);
            this.addMultipleDeviceEvents(deviceEvents);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while handleDeviceEventsUpdate", ex);
        }
    }
    
    private List<DeviceEvent> prepareDeviceEventUpdate(final Long resourceId, final JSONArray eventArr) {
        final List<DeviceEvent> deviceEventList = new ArrayList<DeviceEvent>();
        for (int i = 0; i < eventArr.length(); ++i) {
            final JSONObject deviceEventJSON = eventArr.optJSONObject(i);
            if (deviceEventJSON != null) {
                final DeviceEvent deviceEvent = new DeviceEvent(resourceId, deviceEventJSON.optString("EventName"));
                deviceEvent.addEventTime(deviceEventJSON.optLong("EventTriggeredTime"));
                deviceEventList.add(deviceEvent);
            }
        }
        return deviceEventList;
    }
}

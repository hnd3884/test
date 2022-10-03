package com.me.mdm.server.events;

public class DeviceEvent
{
    public Long resourceId;
    public String deviceEventName;
    public Long eventId;
    private Long deviceEventTime;
    private Long deviceEventHistoryId;
    
    private DeviceEvent() {
        this.deviceEventTime = -1L;
        this.deviceEventHistoryId = null;
    }
    
    public DeviceEvent(final Long resourceId, final String deviceEventName) {
        this.deviceEventTime = -1L;
        this.deviceEventHistoryId = null;
        final DeviceEventDataHandler deviceEventDataHandler = new DeviceEventDataHandler();
        this.deviceEventName = deviceEventName;
        this.eventId = deviceEventDataHandler.getDeviceEventId(deviceEventName);
        this.resourceId = resourceId;
    }
    
    public DeviceEvent(final String deviceEventName, final Long eventId) {
        this.deviceEventTime = -1L;
        this.deviceEventHistoryId = null;
        this.deviceEventName = deviceEventName;
        this.eventId = eventId;
    }
    
    public DeviceEvent addResourceId(final Long resourceId) {
        this.resourceId = resourceId;
        return this;
    }
    
    public DeviceEvent addEventTime(final Long deviceEventTime) {
        this.deviceEventTime = deviceEventTime;
        return this;
    }
    
    public DeviceEvent addDeviceEventHistoryId(final Long evtHistoryId) {
        this.deviceEventHistoryId = evtHistoryId;
        return this;
    }
    
    public Long getDeviceEventTime() {
        return this.deviceEventTime;
    }
    
    public Long getDeviceEventHistoryId() {
        return this.deviceEventHistoryId;
    }
    
    @Override
    public String toString() {
        return "EventName:" + this.deviceEventName + ";EventId:" + this.eventId + ";EventTime:" + this.deviceEventTime + ";ResourceId:" + this.resourceId;
    }
}

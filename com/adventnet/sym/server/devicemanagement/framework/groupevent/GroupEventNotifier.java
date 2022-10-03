package com.adventnet.sym.server.devicemanagement.framework.groupevent;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class GroupEventNotifier
{
    private static final int EVENT_COMPLETE = 1;
    private static final int ACTION_COMPLETE = 2;
    private static final int EVENT_MISSING = 3;
    private static final int MEMBER_UPDATED = 4;
    private static final int EVENT_TIMEOUT = 5;
    private static final int ACTION_CLEANED = 6;
    private static final String EVENT_KEY = "DM_GroupEvents";
    private static final String STATUS_KEY = "DM_GroupEventNotifierStatus";
    public static Logger logger;
    private static final String EVENT_LISTENER_KEY = "DM_GroupEventListener";
    HashMap<String, List<GroupEventListener>> eventListenersMap;
    
    private GroupEventNotifier() {
        this.eventListenersMap = new HashMap<String, List<GroupEventListener>>();
    }
    
    public static GroupEventNotifier getInstance() {
        return new GroupEventNotifier();
    }
    
    public static String getGroupEventNotifierStatus(final String action) {
        return (String)ApiFactoryProvider.getCacheAccessAPI().getCache(action + "_" + "DM_GroupEventNotifierStatus", 2);
    }
    
    public static void setGroupEventNotifierStatus(final String action, final String status) {
        ApiFactoryProvider.getCacheAccessAPI().putCache(action + "_" + "DM_GroupEventNotifierStatus", (Object)status, 2);
    }
    
    public static void removeGroupEventNotifierStatus(final String action) {
        ApiFactoryProvider.getCacheAccessAPI().removeCache(action + "_" + "DM_GroupEventNotifierStatus", 2);
    }
    
    public static HashMap getGroupEventStats() {
        return getInstance().getActionVsEventsMap();
    }
    
    private HashMap getActionVsEventsMap() {
        HashMap<Integer, List> map = (HashMap<Integer, List>)ApiFactoryProvider.getCacheAccessAPI().getCache("DM_GroupEvents", 2);
        if (map == null) {
            map = new HashMap<Integer, List>();
        }
        return map;
    }
    
    private void setActionVsEventsMap(HashMap map) {
        if (map == null) {
            map = new HashMap();
        }
        ApiFactoryProvider.getCacheAccessAPI().putCache("DM_GroupEvents", (Object)map, 2);
    }
    
    private ArrayList getActionList(final String action) {
        final HashMap map = this.getActionVsEventsMap();
        ArrayList actionList = map.get(action);
        if (actionList == null) {
            actionList = new ArrayList();
        }
        return actionList;
    }
    
    private void setActionList(final String action, final List actionItems) {
        final HashMap map = this.getActionVsEventsMap();
        map.put(action, actionItems);
        this.setActionVsEventsMap(map);
    }
    
    public long addGroupIdsForNotification(final ArrayList<Long> groupMembers, final String action, final GroupEventProperties eventProp) throws Exception {
        Long eventId = null;
        eventId = eventProp.getGroupEventId();
        if (eventId == null) {
            eventId = System.currentTimeMillis();
            eventProp.setGroupEventId(eventId);
        }
        final List actionList = this.getActionList(action);
        eventProp.setAction(action);
        eventProp.setActiveMembers((ArrayList<Long>)groupMembers.clone());
        eventProp.setMembersList(groupMembers);
        final long eventTime = System.currentTimeMillis();
        eventProp.setAddedTime(eventTime);
        actionList.add(eventProp);
        GroupEventNotifier.logger.log(Level.INFO, "Going to add groupEvent with props:" + eventProp);
        this.setActionList(action, actionList);
        eventId = eventProp.getGroupEventId();
        if (eventProp.getTimeOut() != null && eventProp.getTimeOut() > 0L) {
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "GroupEventTimeoutTask_" + eventId);
            taskInfoMap.put("schedulerTime", eventTime + eventProp.getTimeOut());
            if (eventProp.getThreadPool() != null) {
                taskInfoMap.put("poolName", eventProp.getThreadPool());
            }
            final Properties taskProp = new Properties();
            taskProp.setProperty("eventId", eventId.toString());
            taskProp.setProperty("action", action.toString());
            setGroupEventNotifierStatus(action, "started");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay(GroupEventTimeoutTask.class.getCanonicalName(), taskInfoMap, taskProp);
        }
        GroupEventNotifier.logger.log(Level.INFO, "End of addGroupIdsForNotification,Current map is:" + getGroupEventStats());
        return eventId;
    }
    
    public void actionCompleted(final Long item, final String action) throws Exception {
        this.actionCompleted(item, action, false);
        GroupEventNotifier.logger.log(Level.INFO, "End of actionCompleted,Current map is:" + getGroupEventStats());
    }
    
    private void actionCompleted(final Long item, final String action, final boolean isSilent) throws Exception {
        GroupEventNotifier.logger.log(Level.INFO, "Action completed for item:" + item + ",action:" + action);
        this.fireEventForAction(4, action, null, isSilent);
        final ArrayList<GroupEventProperties> actionList = this.getActionList(action);
        if (actionList != null && actionList.size() > 0) {
            final Iterator<GroupEventProperties> iterator = actionList.iterator();
            while (iterator.hasNext()) {
                final GroupEventProperties event = iterator.next();
                if (event.getActiveMembers().contains(item)) {
                    final ArrayList activeMembers = event.getActiveMembers();
                    activeMembers.remove(item);
                    event.setActiveMembers(activeMembers);
                    ArrayList completedMembers = event.getCompletedMembers();
                    if (completedMembers == null) {
                        completedMembers = new ArrayList();
                    }
                    completedMembers.add(item);
                    event.setCompletedMembers(completedMembers);
                    if (activeMembers.size() != 0) {
                        continue;
                    }
                    this.fireEventForAction(1, action, event, isSilent);
                    this.cleanUpEvent(event.getGroupEventId());
                    iterator.remove();
                }
                else {
                    GroupEventNotifier.logger.log(Level.INFO, "Member is not available in the action list to remove, List:" + actionList);
                }
            }
            if (actionList.size() == 0) {
                this.fireEventForAction(2, action, null, isSilent);
            }
            this.setActionList(action, actionList);
        }
        GroupEventNotifier.logger.log(Level.INFO, "Action complete, Current map is:" + this.getActionVsEventsMap());
    }
    
    public void removeEvent(final long eventId, final String action) throws Exception {
        GroupEventNotifier.logger.log(Level.INFO, "Going to cleanup event:" + eventId);
        final ArrayList<GroupEventProperties> actionList = this.getActionList(action);
        if (actionList != null) {
            for (final GroupEventProperties gProps : actionList) {
                if (gProps.getGroupEventId() == eventId) {
                    actionList.remove(gProps);
                    this.setActionList(action, actionList);
                    break;
                }
            }
        }
        this.cleanUpEvent(eventId);
        GroupEventNotifier.logger.log(Level.INFO, "End of removeEvent,Current map is:" + getGroupEventStats());
    }
    
    private void cleanUpAction(final String action) throws Exception {
        this.fireEventForAction(6, action, null, false);
        this.setActionList(action, null);
    }
    
    private void cleanUpEvent(final long eventId) throws Exception {
        this.cleanUpScheduler(eventId);
    }
    
    private void cleanUpScheduler(final long eventId) throws Exception {
        final SelectQueryImpl selectJob = new SelectQueryImpl(new Table("SchedulerClasses"));
        selectJob.addSelectColumn(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"));
        final Criteria nameCri = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)("GroupEventTimeoutTask_" + eventId), 0);
        selectJob.setCriteria(nameCri);
        final DataObject taskDo = SyMUtil.getPersistence().get((SelectQuery)selectJob);
        if (!taskDo.isEmpty()) {
            final long schedulerClassId = (long)taskDo.getFirstRow("SchedulerClasses").get("SCHEDULER_CLASS_ID");
            ApiFactoryProvider.getSchedulerAPI().setSchedulerState(false, Long.valueOf(schedulerClassId));
        }
    }
    
    private void fireEventForAction(final int type, final String action, final GroupEventProperties props, final boolean isSilent) throws Exception {
        if (isSilent) {
            GroupEventNotifier.logger.log(Level.INFO, "silent event,type:" + type + ",action:" + action + ",props:" + props);
            return;
        }
        GroupEventNotifier.logger.log(Level.INFO, "Going to fire event,type:" + type + ",action:" + action + ",props:" + props);
        final List<String> listeners = getEventListenersForAction(action);
        if (listeners != null) {
            for (final String listenerClass : listeners) {
                final GroupEventListener listener = (GroupEventListener)Class.forName(listenerClass).newInstance();
                this.invokeMethodOfListener(listener, props, type, action);
            }
        }
    }
    
    private void invokeMethodOfListener(final GroupEventListener listener, final GroupEventProperties props, final int type, final String action) throws Exception {
        if (listener == null) {
            GroupEventNotifier.logger.log(Level.SEVERE, "GroupEventListener is null");
        }
        switch (type) {
            case 1: {
                listener.onGroupEventCompleted(props);
                break;
            }
            case 2: {
                listener.onActionCompleted(props);
            }
            case 5: {
                listener.onGroupEventTimeOut(props);
                break;
            }
            case 6: {
                final List<GroupEventProperties> propsList = this.getActionList(action);
                listener.onActionCleaned(propsList);
                break;
            }
        }
    }
    
    private static HashMap getEventListenerMap() {
        return (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("DM_GroupEventListener", 2);
    }
    
    private static void setEventListenerMap(final HashMap map) {
        ApiFactoryProvider.getCacheAccessAPI().putCache("DM_GroupEventListener", (Object)map, 2);
    }
    
    private static List<String> getEventListenersForAction(final String action) throws Exception {
        HashMap<String, List<String>> map = getEventListenerMap();
        if (map == null) {
            map = new HashMap<String, List<String>>();
        }
        final List<String> listeners = map.get(action);
        return listeners;
    }
    
    public static void addEventListenerForAction(final String action, final List<String> listeners) throws Exception {
        HashMap<String, List<String>> map = getEventListenerMap();
        if (map == null) {
            map = new HashMap<String, List<String>>();
        }
        map.put(action, listeners);
        setEventListenerMap(map);
    }
    
    public static void removeEventListenerForAction(final String action) {
        final HashMap<String, List<GroupEventListener>> map = getEventListenerMap();
        if (map != null) {
            map.remove(action);
            setEventListenerMap(map);
        }
    }
    
    public void clearAction(final String action) throws Exception {
        this.cleanUpAction(action);
        GroupEventNotifier.logger.log(Level.INFO, "End of clearAction,Current map is:" + getGroupEventStats());
    }
    
    public static void removeAction(final String action) throws Exception {
        ApiFactoryProvider.getCacheAccessAPI().removeCache(action, 2);
    }
    
    public void clearUserAction(final String action, final long userId) throws Exception {
        final ArrayList<GroupEventProperties> actionList = this.getActionList(action);
        final Iterator<GroupEventProperties> iterator = actionList.iterator();
        while (iterator.hasNext()) {
            final GroupEventProperties gProps = iterator.next();
            if (gProps.getUserId().equals(userId)) {
                this.cleanUpEvent(gProps.getGroupEventId());
                iterator.remove();
            }
        }
        this.setActionList(action, actionList);
        GroupEventNotifier.logger.log(Level.INFO, "End of clearUserAction,Current map is:" + getGroupEventStats());
    }
    
    void onEventTimeoutTask(final long eventId, final String action) throws Exception {
        final ArrayList<GroupEventProperties> actionList = this.getActionList(action);
        boolean isEventExist = false;
        final Iterator<GroupEventProperties> iterator = actionList.iterator();
        while (iterator.hasNext()) {
            final GroupEventProperties event = iterator.next();
            if (event.getGroupEventId() == eventId) {
                this.fireEventForAction(5, action, event, false);
                this.cleanUpEvent(eventId);
                iterator.remove();
                isEventExist = true;
            }
        }
        if (!isEventExist) {
            GroupEventNotifier.logger.log(Level.SEVERE, "Event-timeout invoked for a non existing event.");
            return;
        }
        this.setActionList(action, actionList);
    }
    
    public void removeSingleMemberEvent(final Long item, final String action) throws Exception {
        this.actionCompleted(item, action, true);
        GroupEventNotifier.logger.log(Level.INFO, "End of removeSingleMemberEvent,Current map is:" + getGroupEventStats());
    }
    
    static {
        GroupEventNotifier.logger = Logger.getLogger(GroupEventNotifier.class.getCanonicalName());
    }
}

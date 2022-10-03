package com.adventnet.sym.server.devicemanagement.framework.groupevent;

import java.util.ArrayList;
import java.util.Properties;
import java.io.Serializable;

public class GroupEventProperties implements Serializable
{
    private Properties properties;
    private Long timeOut;
    private ArrayList<Long> membersList;
    private String action;
    private Long groupEventId;
    private Long addedTime;
    private ArrayList<Long> activeMembers;
    private ArrayList<Long> completedMembers;
    private String threadPool;
    private Long userId;
    
    public Properties getProperties() {
        return this.properties;
    }
    
    public void setCustomProperties(final Properties properties) {
        this.properties = properties;
    }
    
    public Long getTimeOut() {
        return this.timeOut;
    }
    
    public void setTimeOut(final Long timeOut) {
        this.timeOut = timeOut;
    }
    
    public ArrayList<Long> getMembersList() {
        return this.membersList;
    }
    
    void setMembersList(final ArrayList<Long> membersList) {
        this.membersList = membersList;
    }
    
    public String getAction() {
        return this.action;
    }
    
    void setAction(final String action) {
        this.action = action;
    }
    
    public Long getGroupEventId() {
        return this.groupEventId;
    }
    
    void setGroupEventId(final Long groupEventId) {
        this.groupEventId = groupEventId;
    }
    
    public Long getAddedTime() {
        return this.addedTime;
    }
    
    void setAddedTime(final Long addedTime) {
        this.addedTime = addedTime;
    }
    
    public ArrayList<Long> getCompletedMembers() {
        return this.completedMembers;
    }
    
    void setCompletedMembers(final ArrayList<Long> completedMembers) {
        this.completedMembers = completedMembers;
    }
    
    public ArrayList<Long> getActiveMembers() {
        return this.activeMembers;
    }
    
    void setActiveMembers(final ArrayList<Long> activeMembers) {
        this.activeMembers = activeMembers;
    }
    
    @Override
    public String toString() {
        return "Action:" + this.getAction() + ",EventId:" + this.getGroupEventId() + ",Timeout:" + this.getTimeOut() + ",All Members:" + this.getMembersList() + ",Completed Members:" + this.getCompletedMembers() + ",Pending Members:" + this.getActiveMembers();
    }
    
    public String getThreadPool() {
        return this.threadPool;
    }
    
    public void setThreadPool(final String threadPool) {
        this.threadPool = threadPool;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Long userId) {
        this.userId = userId;
    }
}

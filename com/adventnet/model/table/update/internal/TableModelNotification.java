package com.adventnet.model.table.update.internal;

import java.util.List;
import java.io.Serializable;

public class TableModelNotification implements Serializable
{
    private List tableRowActions;
    private int modelInstanceId;
    private int currentBaseDataStateId;
    int listenerId;
    
    public TableModelNotification(final int modelInstanceId, final List tableRowActions, final int currentBaseDataStateId) {
        this.modelInstanceId = modelInstanceId;
        this.tableRowActions = tableRowActions;
        this.currentBaseDataStateId = currentBaseDataStateId;
    }
    
    public int getModelInstanceId() {
        return this.modelInstanceId;
    }
    
    public List getTableRowActions() {
        return this.tableRowActions;
    }
    
    public int getCurrentBaseDataStateId() {
        return this.currentBaseDataStateId;
    }
    
    public int getListenerId() {
        return this.listenerId;
    }
    
    public void setListenerId(final int v) {
        this.listenerId = v;
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("\n<TableModelNotification listenerId=");
        buff.append(this.listenerId);
        buff.append(" modelInstanceId=");
        buff.append(this.modelInstanceId);
        buff.append(" currentBaseDataStateId=");
        buff.append(this.currentBaseDataStateId);
        buff.append(" >");
        buff.append("\n  <tableRowActions>");
        buff.append(this.tableRowActions.toString().replaceAll("\n", "\n\t"));
        buff.append("\n  </tableRowActions>");
        buff.append("\n</TableModelNotification>");
        return buff.toString();
    }
}

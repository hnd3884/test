package com.adventnet.model.table.internal;

import java.util.List;
import java.io.Serializable;

public class CVTableModelRow implements Serializable
{
    private List tablesList;
    private List rowContents;
    private List key;
    
    public void setTableList(final List tablesList) {
        this.tablesList = tablesList;
    }
    
    public List getTableList() {
        return this.tablesList;
    }
    
    public CVTableModelRow(final List rowContents, final List key) {
        this.rowContents = rowContents;
        this.key = key;
    }
    
    public List getRowContents() {
        return this.rowContents;
    }
    
    public void setRowContents(final List v) {
        this.rowContents = v;
    }
    
    public List getKey() {
        return this.key;
    }
    
    public void setKey(final List v) {
        this.key = v;
    }
    
    @Override
    public String toString() {
        return "<CVTableModelRow key=" + this.key + " rowContents=" + this.rowContents + " tableList=" + this.tablesList + " />";
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        final CVTableModelRow row = (CVTableModelRow)obj;
        return this.isEqual(this.key, row.key) && this.isEqual(this.tablesList, row.tablesList) && this.isEqual(this.rowContents, row.rowContents);
    }
    
    private boolean isEqual(final List list1, final List list2) {
        return list1 == list2 || (list1 != null && list2 != null && list1.equals(list2));
    }
}

package com.adventnet.db.archive;

import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import com.adventnet.ds.query.ArchiveTable;
import java.util.Map;
import java.util.List;

public class DefaultArchiveNotificationHandler implements ArchiveNotificationHandler
{
    private ArchivePolicyInfo currentPolicy;
    private List<String> unprocessedTableNames;
    private List<String> processedTableNames;
    private Map<String, ArchiveTable> archiveMap;
    private boolean startedArchiving;
    private boolean completedArchiving;
    private String currentTable;
    private int statusCode;
    
    public DefaultArchiveNotificationHandler() {
        this.processedTableNames = new ArrayList<String>();
        this.startedArchiving = false;
        this.completedArchiving = false;
        this.currentTable = null;
        this.statusCode = -1;
    }
    
    @Override
    public void currentRunningPolicy(final ArchivePolicyInfo policy) {
        this.currentPolicy = policy;
    }
    
    public ArchivePolicyInfo getCurrentRunningPolicy() {
        return this.currentPolicy;
    }
    
    @Override
    public void tableNamesToBeArchived(final List<String> tableNameList) {
        this.unprocessedTableNames = tableNameList;
    }
    
    public List<String> getTableNamesToBeArchived() {
        return this.unprocessedTableNames;
    }
    
    @Override
    public void archiveMap(final Map<String, ArchiveTable> archiveMap) {
        this.archiveMap = archiveMap;
    }
    
    public Map<String, ArchiveTable> getArchiveMap() {
        return this.archiveMap;
    }
    
    @Override
    public void startedArchiving(final boolean start) {
        this.startedArchiving = start;
    }
    
    @Override
    public void processTable(final String tableName) {
        this.currentTable = tableName;
    }
    
    @Override
    public void finishedTable(final String tableName) {
        this.processedTableNames.add(tableName);
    }
    
    public boolean isStartedArchiving() {
        return this.startedArchiving;
    }
    
    public boolean isCompletedArchiving() {
        return this.completedArchiving;
    }
    
    @Override
    public void completedArchiving(final boolean completed, final int statusCode, final DataObject archiveTableDetails) {
        this.completedArchiving = completed;
        this.statusCode = statusCode;
    }
    
    public String getCurrentTable() {
        return this.currentTable;
    }
    
    public List<String> getCompletedList() {
        return this.processedTableNames;
    }
    
    public int getCompletedStatusCode() {
        return this.statusCode;
    }
}

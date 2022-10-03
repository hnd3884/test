package com.zoho.mickey.tools.crypto;

import java.util.List;

public class ECTagModifierStatus
{
    public static final int STARTED = 1;
    public static final int ADDEDTEMPCOLUMN = 2;
    public static final int DATAENCRYPTEDINTEMP = 3;
    public static final int MODIFIEDTEMPCOLUMN = 4;
    public static final int RENAMEDACTUALCOLUMNTOTEMP = 5;
    public static final int RENAMEDTEMPTOACTAULCOLUMN = 6;
    public static final int COMPLETED = 7;
    public static final int PROCESSSTARTED = 8;
    public static final int PROCESSCOMPLETED = 9;
    private List<String> tableInvolved;
    private String processingTableName;
    private int currentStatus;
    private int numberOfTablesCompleted;
    private String processingColumnName;
    
    public ECTagModifierStatus() {
        this.tableInvolved = null;
        this.processingTableName = null;
        this.numberOfTablesCompleted = 0;
        this.processingColumnName = null;
    }
    
    public void initialize(final List<String> tableNames) {
        this.tableInvolved = tableNames;
    }
    
    public List<String> getTableNames() {
        return this.tableInvolved;
    }
    
    public void setProcessingTableName(final String tableName) {
        this.processingTableName = tableName;
    }
    
    public String getProcessingTableName() {
        return this.processingTableName;
    }
    
    public void setProcessingColumnName(final String processingColName) {
        this.processingColumnName = processingColName;
    }
    
    public String getProcessingColumnName() {
        return this.processingColumnName;
    }
    
    public void setCurrentStatus(final int status) {
        this.currentStatus = status;
    }
    
    public int getCurrentStatus() {
        return this.currentStatus;
    }
    
    public void setCompletedTableCount(final int numberOftables) {
        this.numberOfTablesCompleted = numberOftables;
    }
    
    public int getCompetedTableCount() {
        return this.numberOfTablesCompleted;
    }
}

package com.adventnet.db.migration.report;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public final class DBMigrationStatusSummary
{
    String tabName;
    OperationSummary task;
    List<OperationSummary> executedDurations;
    OperationSummary tableCreation;
    OperationSummary dataPopulation;
    Map<String, OperationSummary> consCreationMap;
    long executedIntervalTime;
    private static final String HEADER_BORDER;
    private static final String FOOTER_BORDER;
    
    public DBMigrationStatusSummary(final String tableName) {
        this.tabName = null;
        this.task = null;
        this.executedDurations = new ArrayList<OperationSummary>();
        this.tableCreation = new OperationSummary();
        this.dataPopulation = new OperationSummary();
        this.consCreationMap = new ConcurrentHashMap<String, OperationSummary>();
        this.executedIntervalTime = 0L;
        this.tabName = tableName;
    }
    
    protected void setTaskStartTime() {
        if (this.task == null) {
            this.task = new OperationSummary();
        }
        else {
            this.executedDurations.add(this.task);
            this.task = new OperationSummary();
        }
        this.task.startTime = System.currentTimeMillis();
    }
    
    protected void setTaskEndTime() {
        this.task.endTime = System.currentTimeMillis();
        this.executedIntervalTime += this.task.endTime - this.task.startTime;
    }
    
    protected void setDataPopulationStartTime() {
        this.dataPopulation.startTime = System.currentTimeMillis();
    }
    
    protected void setDataPopulationEndTime() {
        this.dataPopulation.endTime = System.currentTimeMillis();
    }
    
    protected void setTableCreationStartTime() {
        this.tableCreation.startTime = System.currentTimeMillis();
    }
    
    protected void setTableCreationEndTime() {
        this.tableCreation.endTime = System.currentTimeMillis();
    }
    
    protected void markStartTimeForCreateConstraint(final String constraintName, final boolean isTrigger) {
        final OperationSummary opr = this.getOperation(constraintName);
        opr.startTime = System.currentTimeMillis();
        opr.isTrigger = isTrigger;
        this.consCreationMap.put(constraintName, opr);
    }
    
    protected void markEndTimeForCreateConstraint(final String constraintName) {
        final OperationSummary opr = this.getOperation(constraintName);
        opr.endTime = System.currentTimeMillis();
        this.consCreationMap.put(constraintName, opr);
    }
    
    private OperationSummary getOperation(final String constraintName) {
        OperationSummary opr = this.consCreationMap.get(constraintName);
        if (opr == null) {
            opr = new OperationSummary(constraintName);
        }
        return opr;
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        final String nl = System.getProperty("line.separator");
        final String format = "%-40s %-30s %-30s %-35s";
        buff.append(nl);
        buff.append(DBMigrationStatusSummary.HEADER_BORDER).append(nl);
        buff.append("\t\t\t\tTable name :: ").append(this.tabName).append(nl);
        buff.append(DBMigrationStatusSummary.HEADER_BORDER).append(nl);
        buff.append(DBMigrationStatusSummary.FOOTER_BORDER).append(nl);
        buff.append("Table creation process summary ").append(nl);
        buff.append(DBMigrationStatusSummary.FOOTER_BORDER).append(nl);
        buff.append(String.format(format, "Operation", "Start time", "End time", "Total executed time")).append(nl);
        buff.append(String.format(format, "Table creation ", this.getDateTime(this.tableCreation.startTime), this.getDateTime(this.tableCreation.endTime), this.getExecutedTime(this.tableCreation.startTime, this.tableCreation.endTime))).append(nl);
        buff.append(DBMigrationStatusSummary.FOOTER_BORDER).append(nl);
        buff.append("Data population process summary ").append(nl);
        buff.append(DBMigrationStatusSummary.FOOTER_BORDER).append(nl);
        buff.append(String.format(format, "Operation", "Start time", "End time", "Total executed time")).append(nl);
        buff.append(String.format(format, "Data population ", this.getDateTime(this.dataPopulation.startTime), this.getDateTime(this.dataPopulation.endTime), this.getExecutedTime(this.dataPopulation.startTime, this.dataPopulation.endTime))).append(nl);
        buff.append(DBMigrationStatusSummary.FOOTER_BORDER).append(nl);
        buff.append("Constraint creation process summary ").append(nl);
        buff.append(DBMigrationStatusSummary.FOOTER_BORDER).append(nl);
        buff.append(String.format(format, "Constraint name", "Start time", "End time", "Total executed time")).append(nl);
        for (final String consName : this.consCreationMap.keySet()) {
            final OperationSummary opr = this.consCreationMap.get(consName);
            buff.append(String.format(format, consName + (opr.isTrigger ? "[TR]" : ""), this.getDateTime(opr.startTime), this.getDateTime(opr.endTime), this.getExecutedTime(opr.startTime, opr.endTime))).append(nl);
        }
        buff.append(nl);
        buff.append(DBMigrationStatusSummary.FOOTER_BORDER).append(nl);
        buff.append("Task execution summary ").append(nl);
        buff.append(DBMigrationStatusSummary.FOOTER_BORDER).append(nl);
        int i = 1;
        final Iterator<OperationSummary> iterator2 = this.executedDurations.iterator();
        while (iterator2.hasNext()) {
            final OperationSummary opr = iterator2.next();
            buff.append(String.format(format, "Execution time of LEVEL" + i++, this.getDateTime(opr.startTime), this.getDateTime(opr.endTime), "")).append(nl);
        }
        buff.append(String.format(format, "Execution time of LEVEL" + i++, this.getDateTime(this.task.startTime), this.getDateTime(this.task.endTime), "")).append(nl);
        buff.append("Total time elapsed to migrate table :::::: ").append(this.getExecutedTime(this.executedIntervalTime)).append(nl);
        buff.append(DBMigrationStatusSummary.HEADER_BORDER).append(nl).append(nl).append(nl);
        return buff.toString();
    }
    
    private String getExecutedTime(final long startTime, final long endTime) {
        if (startTime == 0L || endTime == 0L) {
            return "Execution incomplete (or) skipped";
        }
        return this.getExecutedTime(endTime - startTime);
    }
    
    private String getExecutedTime(final long executedTime) {
        final long totalExecutedSeconds = TimeUnit.MILLISECONDS.toSeconds(executedTime);
        final long min = totalExecutedSeconds / 60L;
        final long sec = totalExecutedSeconds - min * 60L;
        final long msec = TimeUnit.MILLISECONDS.toMillis(executedTime - (TimeUnit.SECONDS.toMillis(sec) + TimeUnit.SECONDS.toMillis(min * 60L)));
        return String.format("%d Min: %d Sec : %d milliSec", min, sec, msec);
    }
    
    private String getDateTime(final long millis) {
        if (millis != 0L) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format(new Date(millis));
        }
        return "Time value not initialized";
    }
    
    static {
        HEADER_BORDER = new String(new char[140]).replace("\u0000", "=");
        FOOTER_BORDER = new String(new char[140]).replace("\u0000", "-");
    }
    
    protected class OperationSummary
    {
        protected boolean isTrigger;
        protected String constraintName;
        protected long startTime;
        protected long endTime;
        
        protected OperationSummary() {
            this.isTrigger = false;
            this.constraintName = null;
            this.startTime = 0L;
            this.endTime = 0L;
        }
        
        protected OperationSummary(final String constName) {
            this.isTrigger = false;
            this.constraintName = null;
            this.startTime = 0L;
            this.endTime = 0L;
            this.constraintName = constName;
        }
    }
}

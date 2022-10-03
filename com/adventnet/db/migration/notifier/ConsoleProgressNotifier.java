package com.adventnet.db.migration.notifier;

import com.adventnet.mfw.ConsoleOut;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.List;

public class ConsoleProgressNotifier implements ProgressNotifier
{
    private static final String FORMAT = "%s[%-50s] %3d%% %s %5d/%-5d\r";
    protected int maxStringSizePrinted;
    protected List<String> tableNameList;
    protected int totalOperations;
    protected Queue<String> processedTableNameListForLevel1;
    protected Queue<String> processedTableNameListForLevel2;
    protected Queue<String> processingTableNames;
    
    public ConsoleProgressNotifier() {
        this.maxStringSizePrinted = 0;
        this.tableNameList = null;
        this.processedTableNameListForLevel1 = new ConcurrentLinkedQueue<String>();
        this.processedTableNameListForLevel2 = new ConcurrentLinkedQueue<String>();
        this.processingTableNames = new ConcurrentLinkedQueue<String>();
    }
    
    @Override
    public void initialize(final List<String> tableNames, final int maxOperations) {
        this.tableNameList = tableNames;
        this.totalOperations = maxOperations;
    }
    
    @Override
    public void migrationStarted() {
        this.printMessage("DBMigration process started");
    }
    
    @Override
    public synchronized void startedProcessingTable(final String tableName) {
        this.processingTableNames.add(tableName);
    }
    
    @Override
    public void startedLevel1(final String tableName) {
    }
    
    @Override
    public synchronized void startedLevel2(final String tableName) {
    }
    
    @Override
    public void completedPercentageOfLevel1(final String tableName, final int percentage) {
    }
    
    @Override
    public void migratedRows(final String tableName, final long totalNoOfRows, final long migratedRows) {
    }
    
    @Override
    public void completedPercentageOfLevel2(final String tableName, final int percentage) {
    }
    
    @Override
    public synchronized void completedLevel1(final String tableName) {
        if (!this.processedTableNameListForLevel1.contains(tableName)) {
            this.processedTableNameListForLevel1.add(tableName);
            this.printProgressInConsole("", this.totalOperations, this.processedTableNameListForLevel1.size());
        }
    }
    
    @Override
    public synchronized void completedLevel2(final String tableName) {
        if (!this.processedTableNameListForLevel2.contains(tableName)) {
            this.processedTableNameListForLevel2.add(tableName);
            this.printProgressInConsole("", this.totalOperations, this.processedTableNameListForLevel2.size());
        }
    }
    
    @Override
    public void completedProcessingTable(final String tableName) {
        if (!this.processedTableNameListForLevel2.contains(tableName)) {
            this.processedTableNameListForLevel2.add(tableName);
            this.printProgressInConsole("", this.totalOperations, this.processedTableNameListForLevel2.size());
        }
    }
    
    @Override
    public void startedLevel1Process() {
        this.printMessage("Creating tables and migrating data.");
    }
    
    @Override
    public void startedLevel2Process() {
        this.printMessage("Creating table relations.");
    }
    
    @Override
    public void completedLevel1Process() {
        this.printMessage("Completed...");
    }
    
    @Override
    public void completedLevel2Process() {
        this.printMessage("Completed...");
    }
    
    @Override
    public void isFailed(final String tableName) {
        this.printMessage("Exception occured while migrating table [" + tableName + "]. See logs for more details.");
    }
    
    @Override
    public void migrationStopped() {
        this.printMessage("Migration process interrupted. See log files for more details.");
    }
    
    protected void printProgressInConsole(final String message, final int total, final int processed) {
        final String position = new String(new char[this.maxStringSizePrinted]).replace("\u0000", "\b");
        final int pers = processed * 100 / total;
        String completed = "";
        if (pers != 0) {
            completed = new String(new char[pers / 2]).replace("\u0000", "=");
        }
        String msgToBePrinted = String.format("%s[%-50s] %3d%% %s %5d/%-5d\r", position, completed, pers, message, processed, total);
        if (msgToBePrinted.length() < this.maxStringSizePrinted) {
            msgToBePrinted += new String(new char[this.maxStringSizePrinted - msgToBePrinted.length()]).replace("\u0000", " ");
        }
        this.maxStringSizePrinted = msgToBePrinted.length();
        ConsoleOut.print(msgToBePrinted);
        if (pers == 100) {
            ConsoleOut.println("");
        }
    }
    
    @Override
    public void printMessage(final String message) {
        ConsoleOut.println(message);
    }
}

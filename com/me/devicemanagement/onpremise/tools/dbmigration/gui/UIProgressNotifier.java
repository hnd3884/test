package com.me.devicemanagement.onpremise.tools.dbmigration.gui;

import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.List;
import com.adventnet.db.migration.notifier.ProgressNotifier;

public class UIProgressNotifier implements ProgressNotifier
{
    protected List<String> tableNameList;
    protected int totalOperations;
    protected Queue<String> processedTableNameListForLevel1;
    protected Queue<String> processedTableNameListForLevel2;
    private Logger logger;
    
    public UIProgressNotifier() {
        this.tableNameList = null;
        this.processedTableNameListForLevel1 = new ConcurrentLinkedQueue<String>();
        this.processedTableNameListForLevel2 = new ConcurrentLinkedQueue<String>();
        this.logger = Logger.getLogger("UIProgressNotifier");
    }
    
    public void initialize(final List<String> tableNames, final int maxOperations) {
        this.tableNameList = tableNames;
        this.totalOperations = maxOperations;
        ChangeDBServerGUI.getInstance().showProgress();
    }
    
    public void migrationStarted() {
        this.logger.log(Level.INFO, "DBMigration process started");
    }
    
    public synchronized void startedProcessingTable(final String tableName) {
    }
    
    public void startedLevel1(final String tableName) {
    }
    
    public synchronized void startedLevel2(final String tableName) {
    }
    
    public void completedPercentageOfLevel1(final String tableName, final int percentage) {
    }
    
    public void migratedRows(final String tableName, final long totalNoOfRows, final long migratedRows) {
    }
    
    public void completedPercentageOfLevel2(final String tableName, final int percentage) {
    }
    
    public synchronized void completedLevel1(final String tableName) {
        if (!this.processedTableNameListForLevel1.contains(tableName)) {
            this.processedTableNameListForLevel1.add(tableName);
            this.setPercentageValue(this.processedTableNameListForLevel1.size() * 100 / this.totalOperations);
        }
    }
    
    public synchronized void completedLevel2(final String tableName) {
        if (!this.processedTableNameListForLevel2.contains(tableName)) {
            this.processedTableNameListForLevel2.add(tableName);
            this.setPercentageValue(this.processedTableNameListForLevel2.size() * 100 / this.totalOperations);
        }
    }
    
    public void completedProcessingTable(final String tableName) {
    }
    
    public void startedLevel1Process() {
        this.print(BackupRestoreUtil.getString("desktopcentral.tools.changedb.progress.creating_tables", (Locale)null) + " <font color=\"rgb(165,165,165)\">(Step 1 of 2)</font>");
        this.setPercentageValue(0);
    }
    
    public void startedLevel2Process() {
        this.print(BackupRestoreUtil.getString("desktopcentral.tools.changedb.progress.creating_relations", (Locale)null) + " <font color=\"rgb(165,165,165)\">(Step 2 of 2)</font>");
        this.setPercentageValue(0);
    }
    
    public void completedLevel1Process() {
        this.print(BackupRestoreUtil.getString("desktopcentral.tools.changedb.progress.level1_complete", (Locale)null));
    }
    
    public void completedLevel2Process() {
        this.print(BackupRestoreUtil.getString("desktopcentral.tools.changedb.progress.level2_complete", (Locale)null));
    }
    
    public void isFailed(final String tableName) {
        this.printMessage("Exception occured while migrating table [" + tableName + "]. See logs for more details.");
    }
    
    public void migrationStopped() {
        this.printMessage("Migration process interrupted. See log files for more details.");
    }
    
    public void printMessage(final String message) {
        this.logger.log(Level.INFO, message);
    }
    
    private void print(final String message) {
        ChangeDBServerGUI.getInstance().setProgressText(message);
        this.logger.log(Level.INFO, message);
    }
    
    private void setPercentageValue(final int percentage) {
        ChangeDBServerGUI.getInstance().setProgressBarValue(percentage);
    }
}

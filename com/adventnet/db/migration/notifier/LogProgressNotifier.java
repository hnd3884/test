package com.adventnet.db.migration.notifier;

import java.util.List;
import java.util.logging.Logger;

public class LogProgressNotifier implements ProgressNotifier
{
    private static final Logger LOGGER;
    
    @Override
    public void initialize(final List<String> tableNames, final int maxOperations) {
    }
    
    @Override
    public void migrationStarted() {
        this.printMessage("DBMigration process started");
    }
    
    @Override
    public void startedProcessingTable(final String tableName) {
    }
    
    @Override
    public void startedLevel1(final String tableName) {
    }
    
    @Override
    public void startedLevel2(final String tableName) {
    }
    
    @Override
    public void completedPercentageOfLevel1(final String tableName, final int percentage) {
    }
    
    @Override
    public void completedPercentageOfLevel2(final String tableName, final int percentage) {
    }
    
    @Override
    public void migratedRows(final String tableName, final long totalNoOfRows, final long migratedRows) {
    }
    
    @Override
    public void completedLevel1(final String tableName) {
    }
    
    @Override
    public void completedLevel2(final String tableName) {
    }
    
    @Override
    public void completedProcessingTable(final String tableName) {
    }
    
    @Override
    public void isFailed(final String tableName) {
        this.printMessage("Exception occured while migrating table [" + tableName + "].");
    }
    
    @Override
    public void migrationStopped() {
        this.printMessage("Migration process interrupted.");
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
        this.printMessage("Completed creating and migrating tables");
    }
    
    @Override
    public void completedLevel2Process() {
        this.printMessage("Completed creating fk constraints");
    }
    
    @Override
    public void printMessage(final String message) {
        LogProgressNotifier.LOGGER.info(message);
    }
    
    static {
        LOGGER = Logger.getLogger(LogProgressNotifier.class.getName());
    }
}

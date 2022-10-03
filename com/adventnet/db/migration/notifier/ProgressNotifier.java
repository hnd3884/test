package com.adventnet.db.migration.notifier;

import java.util.List;

public interface ProgressNotifier
{
    void initialize(final List<String> p0, final int p1);
    
    void migrationStarted();
    
    void startedProcessingTable(final String p0);
    
    void startedLevel1(final String p0);
    
    void startedLevel2(final String p0);
    
    @Deprecated
    void completedPercentageOfLevel1(final String p0, final int p1);
    
    @Deprecated
    void completedPercentageOfLevel2(final String p0, final int p1);
    
    void migratedRows(final String p0, final long p1, final long p2);
    
    void completedLevel1(final String p0);
    
    void completedLevel2(final String p0);
    
    void completedProcessingTable(final String p0);
    
    @Deprecated
    void isFailed(final String p0);
    
    void migrationStopped();
    
    void startedLevel1Process();
    
    void startedLevel2Process();
    
    void completedLevel1Process();
    
    void completedLevel2Process();
    
    void printMessage(final String p0);
}

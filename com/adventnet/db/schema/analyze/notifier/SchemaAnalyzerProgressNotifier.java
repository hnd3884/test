package com.adventnet.db.schema.analyze.notifier;

import java.util.List;

public interface SchemaAnalyzerProgressNotifier
{
    void initialize(final int p0, final List<String> p1);
    
    void updateProgress(final String p0);
    
    void printMessage(final String p0);
}

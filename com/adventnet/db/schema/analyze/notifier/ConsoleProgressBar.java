package com.adventnet.db.schema.analyze.notifier;

import com.adventnet.mfw.ConsoleOut;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;

public class ConsoleProgressBar implements SchemaAnalyzerProgressNotifier
{
    private StringBuilder progress;
    private int total;
    private Set<String> tableNameList;
    private List<String> tableNames;
    
    public ConsoleProgressBar() {
        this.tableNameList = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.tableNames = new ArrayList<String>();
    }
    
    @Override
    public void initialize(final int total, final List<String> tableNames) {
        this.total = total;
        this.progress = new StringBuilder(60);
        this.tableNames = tableNames;
    }
    
    @Override
    public void updateProgress(final String tableName) {
        if (!this.tableNameList.contains(tableName)) {
            this.tableNameList.add(tableName);
            this.updateProgressBar(this.tableNameList.size(), this.total);
        }
    }
    
    @Override
    public void printMessage(final String message) {
        ConsoleOut.println(message);
    }
    
    private void updateProgressBar(final int processed, final int total) {
        final char[] charcters = { '|', '/', '-', '\\' };
        final String format = "\r%3d%% %s %c";
        final int percent = processed * 100 / total;
        int remainingchars = percent / 2 - this.progress.length();
        while (remainingchars-- > 0) {
            this.progress.append('#');
        }
        ConsoleOut.print(String.format(format, percent, this.progress, charcters[processed % charcters.length]));
        if (processed == total) {
            ConsoleOut.println("");
        }
    }
}

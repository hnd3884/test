package org.eclipse.jdt.internal.compiler.impl;

public class CompilerStats implements Comparable
{
    public long startTime;
    public long endTime;
    public long lineCount;
    public long parseTime;
    public long resolveTime;
    public long analyzeTime;
    public long generateTime;
    
    public long elapsedTime() {
        return this.endTime - this.startTime;
    }
    
    @Override
    public int compareTo(final Object o) {
        final CompilerStats otherStats = (CompilerStats)o;
        final long time1 = this.elapsedTime();
        final long time2 = otherStats.elapsedTime();
        return (time1 < time2) ? -1 : ((time1 == time2) ? 0 : 1);
    }
}

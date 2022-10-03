package com.sun.org.glassfish.external.statistics;

public interface Stats
{
    Statistic getStatistic(final String p0);
    
    String[] getStatisticNames();
    
    Statistic[] getStatistics();
}

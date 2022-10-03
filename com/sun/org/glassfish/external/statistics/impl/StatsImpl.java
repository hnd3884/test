package com.sun.org.glassfish.external.statistics.impl;

import java.util.ArrayList;
import com.sun.org.glassfish.external.statistics.Statistic;
import com.sun.org.glassfish.external.statistics.Stats;

public final class StatsImpl implements Stats
{
    private final StatisticImpl[] statArray;
    
    protected StatsImpl(final StatisticImpl[] statisticArray) {
        this.statArray = statisticArray;
    }
    
    @Override
    public synchronized Statistic getStatistic(final String statisticName) {
        Statistic stat = null;
        for (final Statistic s : this.statArray) {
            if (s.getName().equals(statisticName)) {
                stat = s;
                break;
            }
        }
        return stat;
    }
    
    @Override
    public synchronized String[] getStatisticNames() {
        final ArrayList list = new ArrayList();
        for (final Statistic s : this.statArray) {
            list.add(s.getName());
        }
        final String[] strArray = new String[list.size()];
        return list.toArray(strArray);
    }
    
    @Override
    public synchronized Statistic[] getStatistics() {
        return this.statArray;
    }
    
    public synchronized void reset() {
        for (final StatisticImpl s : this.statArray) {
            s.reset();
        }
    }
}

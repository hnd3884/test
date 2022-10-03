package io.opencensus.stats;

public abstract class StatsComponent
{
    public abstract ViewManager getViewManager();
    
    public abstract StatsRecorder getStatsRecorder();
    
    public abstract StatsCollectionState getState();
    
    @Deprecated
    public abstract void setState(final StatsCollectionState p0);
}

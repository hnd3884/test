package io.opencensus.stats;

import java.util.logging.Level;
import io.opencensus.internal.Provider;
import javax.annotation.Nullable;
import java.util.logging.Logger;

public final class Stats
{
    private static final Logger logger;
    private static final StatsComponent statsComponent;
    
    public static StatsRecorder getStatsRecorder() {
        return Stats.statsComponent.getStatsRecorder();
    }
    
    public static ViewManager getViewManager() {
        return Stats.statsComponent.getViewManager();
    }
    
    public static StatsCollectionState getState() {
        return Stats.statsComponent.getState();
    }
    
    @Deprecated
    public static void setState(final StatsCollectionState state) {
        Stats.statsComponent.setState(state);
    }
    
    static StatsComponent loadStatsComponent(@Nullable final ClassLoader classLoader) {
        try {
            return Provider.createInstance(Class.forName("io.opencensus.impl.stats.StatsComponentImpl", true, classLoader), StatsComponent.class);
        }
        catch (final ClassNotFoundException e) {
            Stats.logger.log(Level.FINE, "Couldn't load full implementation for StatsComponent, now trying to load lite implementation.", e);
            try {
                return Provider.createInstance(Class.forName("io.opencensus.impllite.stats.StatsComponentImplLite", true, classLoader), StatsComponent.class);
            }
            catch (final ClassNotFoundException e) {
                Stats.logger.log(Level.FINE, "Couldn't load lite implementation for StatsComponent, now using default implementation for StatsComponent.", e);
                return NoopStats.newNoopStatsComponent();
            }
        }
    }
    
    private Stats() {
    }
    
    static {
        logger = Logger.getLogger(Stats.class.getName());
        statsComponent = loadStatsComponent(StatsComponent.class.getClassLoader());
    }
}

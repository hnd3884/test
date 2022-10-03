package org.slf4j;

import org.slf4j.helpers.Util;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.impl.StaticMarkerBinder;

public class MarkerFactory
{
    static IMarkerFactory markerFactory;
    
    private MarkerFactory() {
    }
    
    public static Marker getMarker(final String name) {
        return MarkerFactory.markerFactory.getMarker(name);
    }
    
    public static Marker getDetachedMarker(final String name) {
        return MarkerFactory.markerFactory.getDetachedMarker(name);
    }
    
    public static IMarkerFactory getIMarkerFactory() {
        return MarkerFactory.markerFactory;
    }
    
    static {
        try {
            MarkerFactory.markerFactory = StaticMarkerBinder.SINGLETON.getMarkerFactory();
        }
        catch (final NoClassDefFoundError e) {
            MarkerFactory.markerFactory = new BasicMarkerFactory();
        }
        catch (final Exception e2) {
            Util.report("Unexpected failure while binding MarkerFactory", e2);
        }
    }
}

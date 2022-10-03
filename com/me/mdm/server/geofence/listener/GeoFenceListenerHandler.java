package com.me.mdm.server.geofence.listener;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GeoFenceListenerHandler
{
    private static GeoFenceListenerHandler geoFenceListenerHandler;
    Logger logger;
    private List<GeoFenceListener> geoFenceListenerList;
    
    private GeoFenceListenerHandler() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
        this.geoFenceListenerList = null;
        this.geoFenceListenerList = new ArrayList<GeoFenceListener>();
    }
    
    public static GeoFenceListenerHandler getInstance() {
        if (GeoFenceListenerHandler.geoFenceListenerHandler == null) {
            GeoFenceListenerHandler.geoFenceListenerHandler = new GeoFenceListenerHandler();
        }
        return GeoFenceListenerHandler.geoFenceListenerHandler;
    }
    
    public void addGeoFenceListener(final GeoFenceListener geoFenceListener) {
        this.geoFenceListenerList.add(geoFenceListener);
    }
    
    public void invokeGeoFenceListener(final JSONObject geoFenceJSON, final int params) {
        final int listLength = this.geoFenceListenerList.size();
        switch (params) {
            case 1: {
                for (int i = 0; i < listLength; ++i) {
                    final GeoFenceListener listener = this.geoFenceListenerList.get(i);
                    listener.geoFenceModified(geoFenceJSON);
                }
                break;
            }
        }
    }
    
    static {
        GeoFenceListenerHandler.geoFenceListenerHandler = null;
    }
}

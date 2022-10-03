package com.zoho.security.policy;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GeoLocationRange
{
    private static final Logger LOGGER;
    GeoLocation from;
    GeoLocation to;
    
    public GeoLocationRange(final Double[] latitudes, final Double[] longitudes) {
        this.from = null;
        this.to = null;
        if (latitudes.length == 2 && longitudes.length == 2) {
            this.setCoordinates(latitudes, longitudes);
            return;
        }
        throw new SecurityPolicyException("INVALID_GEO_LOCATION_FORMAT");
    }
    
    public GeoLocationRange(final String location) {
        this.from = null;
        this.to = null;
        if (location.contains(":")) {
            final String[] components = location.split(":");
            if (components.length != 2) {
                throw new SecurityPolicyException("INVALID_GEO_LOCATION_FORMAT");
            }
            final String latComp = components[0];
            Double[] latitude = null;
            if (latComp.contains("-")) {
                latitude = this.getCoordinate(latComp);
            }
            final String longComp = components[1];
            Double[] longitude = null;
            if (longComp.contains("-")) {
                longitude = this.getCoordinate(longComp);
            }
            if (latitude == null || longitude == null) {
                throw new SecurityPolicyException("INVALID_GEO_LOCATION_FORMAT");
            }
            this.setCoordinates(latitude, longitude);
        }
    }
    
    private void setCoordinates(final Double[] latitudes, final Double[] longitudes) {
        Double lat0 = latitudes[0];
        Double lat2 = latitudes[1];
        Double lng0 = longitudes[0];
        Double lng2 = longitudes[1];
        if (lat0 > lat2) {
            final Double tmp = lat2;
            lat2 = lat0;
            lat0 = tmp;
        }
        if (lng0 > lng2) {
            final Double tmp = lng2;
            lng2 = lng0;
            lng0 = tmp;
        }
        this.from = new GeoLocation(lat0, lng0);
        this.to = new GeoLocation(lat2, lng2);
    }
    
    private Double[] getCoordinate(final String value) {
        final String[] components = value.split("-");
        if (components.length != 2) {
            return null;
        }
        final Double[] result = new Double[2];
        try {
            result[0] = Double.parseDouble(components[0]);
            result[1] = Double.parseDouble(components[1]);
        }
        catch (final NumberFormatException e) {
            GeoLocationRange.LOGGER.log(Level.SEVERE, "Number format exception for Latitude or Longitude : {0}", value);
            throw new SecurityPolicyException("INVALID_GEO_LOCATION_FORMAT");
        }
        return result;
    }
    
    public boolean isInRange(final Double latitude, final Double longitude) {
        return this.from.latitude < latitude && this.to.latitude > latitude && this.from.longitude < longitude && this.to.longitude > longitude;
    }
    
    public boolean isInRange(final GeoLocation location) {
        return this.from.latitude < location.latitude && this.to.latitude > location.latitude && this.from.longitude < location.longitude && this.to.longitude > location.longitude;
    }
    
    public boolean isInRange(final GeoLocationRange range) {
        return this.from.latitude < range.from.latitude && this.to.latitude > range.to.latitude && this.from.longitude < range.from.longitude && this.to.longitude > range.to.longitude;
    }
    
    static {
        LOGGER = Logger.getLogger(GeoLocationRange.class.getName());
    }
}

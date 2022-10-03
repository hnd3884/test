package com.zoho.security.policy;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeoLocation
{
    private static final Logger LOGGER;
    Double latitude;
    Double longitude;
    
    public GeoLocation(final String location) {
        this.latitude = null;
        this.longitude = null;
        final String[] components = location.split(":");
        if (components.length != 2) {
            throw new SecurityPolicyException("INVALID_GEO_LOCATION_FORMAT");
        }
        try {
            this.latitude = Double.parseDouble(components[0]);
            this.longitude = Double.parseDouble(components[1]);
        }
        catch (final NumberFormatException e) {
            GeoLocation.LOGGER.log(Level.SEVERE, "Number format exception for Latitude - Longitude : {0}", location);
            throw new SecurityPolicyException("INVALID_GEO_LOCATION_FORMAT");
        }
    }
    
    public GeoLocation(final Double latitude, final Double longitude) {
        this.latitude = null;
        this.longitude = null;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.latitude, this.longitude);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GeoLocation)) {
            return false;
        }
        final GeoLocation comp = (GeoLocation)obj;
        return this.latitude.equals(comp.latitude) && this.longitude.equals(comp.longitude);
    }
    
    static {
        LOGGER = Logger.getLogger(GeoLocation.class.getName());
    }
}

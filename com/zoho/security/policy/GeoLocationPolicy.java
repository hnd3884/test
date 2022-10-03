package com.zoho.security.policy;

import com.adventnet.iam.security.SecurityRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class GeoLocationPolicy extends SecurityPolicyHandler
{
    private List<GeoLocation> location;
    private List<GeoLocationRange> ranges;
    
    public GeoLocationPolicy(final String value) {
        super(POLICY.GEO_LOCATION.name());
        this.location = null;
        this.ranges = null;
        this.parseAndAddLocations(value);
    }
    
    public GeoLocationPolicy(final Double latitude, final Double longitude) {
        super(POLICY.GEO_LOCATION.name());
        this.location = null;
        this.ranges = null;
        final GeoLocation gLocation = new GeoLocation(latitude, longitude);
        this.addGeoLocation(gLocation);
    }
    
    public GeoLocationPolicy(final Double[] latitude, final Double[] longitude) {
        super(POLICY.GEO_LOCATION.name());
        this.location = null;
        this.ranges = null;
        final GeoLocationRange range = new GeoLocationRange(latitude, longitude);
        this.addGeoLocationRange(range);
    }
    
    private void parseAndAddLocations(final String value) {
        if (value.contains(",")) {
            final String[] split;
            final String[] geoLocations = split = value.split(",");
            for (final String geoLocation : split) {
                this.processSimpleLocation(geoLocation);
            }
        }
        else {
            this.processSimpleLocation(value);
        }
    }
    
    private void processSimpleLocation(final String value) {
        if (value.contains("-")) {
            this.addGeoLocationRange(value);
        }
        else {
            this.addGeoLocation(value);
        }
    }
    
    private void addGeoLocationRange(final String location) {
        final GeoLocationRange range = new GeoLocationRange(location);
        this.addGeoLocationRange(range);
    }
    
    private void addGeoLocationRange(final GeoLocationRange range) {
        if (this.ranges == null) {
            this.ranges = new ArrayList<GeoLocationRange>();
        }
        else {
            for (final GeoLocationRange geoLocationRange : this.ranges) {
                if (geoLocationRange.isInRange(range)) {
                    throw new SecurityPolicyException("LOCATION_ALREADY_EXIST");
                }
            }
        }
        this.ranges.add(range);
    }
    
    private void addGeoLocation(final String location) {
        final GeoLocation gLocation = new GeoLocation(location);
        this.addGeoLocation(gLocation);
    }
    
    private void addGeoLocation(final GeoLocation gLocation) {
        if (this.location == null) {
            this.location = new ArrayList<GeoLocation>();
        }
        else if (this.location.contains(gLocation)) {
            throw new SecurityPolicyException("LOCATION_ALREADY_EXIST");
        }
        if (this.ranges != null) {
            for (final GeoLocationRange geoLocationRange : this.ranges) {
                if (geoLocationRange.isInRange(gLocation)) {
                    throw new SecurityPolicyException("LOCATION_ALREADY_EXIST");
                }
            }
        }
        this.location.add(gLocation);
    }
    
    @Override
    public boolean isAccessAllowed(final HttpServletRequest request) {
        final SecurityRequestWrapper secureRequest = (SecurityRequestWrapper)request;
        final Double latitude = secureRequest.getDoubleParameter("_zlat");
        final Double longitude = secureRequest.getDoubleParameter("_zlng");
        if (latitude == null || longitude == null) {
            return false;
        }
        final GeoLocation reqLocation = new GeoLocation(latitude, longitude);
        if (this.location != null && this.location.contains(reqLocation)) {
            return true;
        }
        if (this.ranges != null) {
            for (final GeoLocationRange range : this.ranges) {
                if (range.isInRange(reqLocation)) {
                    return true;
                }
            }
        }
        return false;
    }
}

package com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.azul.crs.com.fasterxml.jackson.annotation.JsonValue;

public enum JsonValueFormat
{
    COLOR("color"), 
    DATE("date"), 
    DATE_TIME("date-time"), 
    EMAIL("email"), 
    HOST_NAME("host-name"), 
    IP_ADDRESS("ip-address"), 
    IPV6("ipv6"), 
    PHONE("phone"), 
    REGEX("regex"), 
    STYLE("style"), 
    TIME("time"), 
    URI("uri"), 
    UTC_MILLISEC("utc-millisec"), 
    UUID("uuid");
    
    private final String _desc;
    
    private JsonValueFormat(final String desc) {
        this._desc = desc;
    }
    
    @JsonValue
    @Override
    public String toString() {
        return this._desc;
    }
}

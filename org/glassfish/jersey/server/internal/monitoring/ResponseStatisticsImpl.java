package org.glassfish.jersey.server.internal.monitoring;

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ResponseStatistics;

final class ResponseStatisticsImpl implements ResponseStatistics
{
    private final Map<Integer, Long> responseCodes;
    private final Integer lastResponseCode;
    
    private ResponseStatisticsImpl(final Integer lastResponseCode, final Map<Integer, Long> responseCodes) {
        this.lastResponseCode = lastResponseCode;
        this.responseCodes = Collections.unmodifiableMap((Map<? extends Integer, ? extends Long>)responseCodes);
    }
    
    @Override
    public Integer getLastResponseCode() {
        return this.lastResponseCode;
    }
    
    @Override
    public Map<Integer, Long> getResponseCodes() {
        return this.responseCodes;
    }
    
    @Override
    public ResponseStatistics snapshot() {
        return this;
    }
    
    static class Builder
    {
        private final Map<Integer, Long> responseCodesMap;
        private Integer lastResponseCode;
        private ResponseStatisticsImpl cached;
        
        Builder() {
            this.responseCodesMap = new HashMap<Integer, Long>();
            this.lastResponseCode = null;
            this.cached = null;
        }
        
        void addResponseCode(final int responseCode) {
            this.cached = null;
            this.lastResponseCode = responseCode;
            Long currentValue = this.responseCodesMap.get(responseCode);
            if (currentValue == null) {
                currentValue = 0L;
            }
            this.responseCodesMap.put(responseCode, currentValue + 1L);
        }
        
        ResponseStatisticsImpl build() {
            if (this.cached == null) {
                this.cached = new ResponseStatisticsImpl(this.lastResponseCode, new HashMap(this.responseCodesMap), null);
            }
            return this.cached;
        }
    }
}

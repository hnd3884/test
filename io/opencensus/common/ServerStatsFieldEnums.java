package io.opencensus.common;

import javax.annotation.Nullable;
import java.util.TreeMap;

public final class ServerStatsFieldEnums
{
    private static final int TOTALSIZE;
    
    private ServerStatsFieldEnums() {
    }
    
    private static int computeTotalSize() {
        int sum = 0;
        for (final Size sizeValue : Size.values()) {
            sum += sizeValue.value();
            ++sum;
        }
        return sum;
    }
    
    public static int getTotalSize() {
        return ServerStatsFieldEnums.TOTALSIZE;
    }
    
    static {
        TOTALSIZE = computeTotalSize();
    }
    
    public enum Id
    {
        SERVER_STATS_LB_LATENCY_ID(0), 
        SERVER_STATS_SERVICE_LATENCY_ID(1), 
        SERVER_STATS_TRACE_OPTION_ID(2);
        
        private final int value;
        private static final TreeMap<Integer, Id> map;
        
        private Id(final int value) {
            this.value = value;
        }
        
        public int value() {
            return this.value;
        }
        
        @Nullable
        public static Id valueOf(final int value) {
            return Id.map.get(value);
        }
        
        static {
            map = new TreeMap<Integer, Id>();
            for (final Id id : values()) {
                Id.map.put(id.value, id);
            }
        }
    }
    
    public enum Size
    {
        SERVER_STATS_LB_LATENCY_SIZE(8), 
        SERVER_STATS_SERVICE_LATENCY_SIZE(8), 
        SERVER_STATS_TRACE_OPTION_SIZE(1);
        
        private final int value;
        
        private Size(final int value) {
            this.value = value;
        }
        
        public int value() {
            return this.value;
        }
    }
}

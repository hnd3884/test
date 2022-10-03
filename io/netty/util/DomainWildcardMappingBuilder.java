package io.netty.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import io.netty.util.internal.ObjectUtil;
import java.util.Map;

public class DomainWildcardMappingBuilder<V>
{
    private final V defaultValue;
    private final Map<String, V> map;
    
    public DomainWildcardMappingBuilder(final V defaultValue) {
        this(4, defaultValue);
    }
    
    public DomainWildcardMappingBuilder(final int initialCapacity, final V defaultValue) {
        this.defaultValue = ObjectUtil.checkNotNull(defaultValue, "defaultValue");
        this.map = new LinkedHashMap<String, V>(initialCapacity);
    }
    
    public DomainWildcardMappingBuilder<V> add(final String hostname, final V output) {
        this.map.put(this.normalizeHostName(hostname), ObjectUtil.checkNotNull(output, "output"));
        return this;
    }
    
    private String normalizeHostName(String hostname) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        if (hostname.isEmpty() || hostname.charAt(0) == '.') {
            throw new IllegalArgumentException("Hostname '" + hostname + "' not valid");
        }
        hostname = ImmutableDomainWildcardMapping.normalize(ObjectUtil.checkNotNull(hostname, "hostname"));
        if (hostname.charAt(0) != '*') {
            return hostname;
        }
        if (hostname.length() < 3 || hostname.charAt(1) != '.') {
            throw new IllegalArgumentException("Wildcard Hostname '" + hostname + "'not valid");
        }
        return hostname.substring(1);
    }
    
    public Mapping<String, V> build() {
        return new ImmutableDomainWildcardMapping<V>(this.defaultValue, this.map);
    }
    
    private static final class ImmutableDomainWildcardMapping<V> implements Mapping<String, V>
    {
        private static final String REPR_HEADER = "ImmutableDomainWildcardMapping(default: ";
        private static final String REPR_MAP_OPENING = ", map: ";
        private static final String REPR_MAP_CLOSING = ")";
        private final V defaultValue;
        private final Map<String, V> map;
        
        ImmutableDomainWildcardMapping(final V defaultValue, final Map<String, V> map) {
            this.defaultValue = defaultValue;
            this.map = new LinkedHashMap<String, V>((Map<? extends String, ? extends V>)map);
        }
        
        @Override
        public V map(String hostname) {
            if (hostname != null) {
                hostname = normalize(hostname);
                V value = this.map.get(hostname);
                if (value != null) {
                    return value;
                }
                final int idx = hostname.indexOf(46);
                if (idx != -1) {
                    value = this.map.get(hostname.substring(idx));
                    if (value != null) {
                        return value;
                    }
                }
            }
            return this.defaultValue;
        }
        
        static String normalize(final String hostname) {
            return DomainNameMapping.normalizeHostname(hostname);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("ImmutableDomainWildcardMapping(default: ").append(this.defaultValue).append(", map: ").append('{');
            for (final Map.Entry<String, V> entry : this.map.entrySet()) {
                String hostname = entry.getKey();
                if (hostname.charAt(0) == '.') {
                    hostname = '*' + hostname;
                }
                sb.append(hostname).append('=').append(entry.getValue()).append(", ");
            }
            sb.setLength(sb.length() - 2);
            return sb.append('}').append(")").toString();
        }
    }
}

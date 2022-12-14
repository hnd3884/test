package jdk.internal.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Map;

public class SimpleRemapper extends Remapper
{
    private final Map<String, String> mapping;
    
    public SimpleRemapper(final Map<String, String> mapping) {
        this.mapping = mapping;
    }
    
    public SimpleRemapper(final String s, final String s2) {
        this.mapping = Collections.singletonMap(s, s2);
    }
    
    @Override
    public String mapMethodName(final String s, final String s2, final String s3) {
        final String map = this.map(s + '.' + s2 + s3);
        return (map == null) ? s2 : map;
    }
    
    @Override
    public String mapFieldName(final String s, final String s2, final String s3) {
        final String map = this.map(s + '.' + s2);
        return (map == null) ? s2 : map;
    }
    
    @Override
    public String map(final String s) {
        return this.mapping.get(s);
    }
}

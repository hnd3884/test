package io.netty.handler.ssl;

import java.util.Iterator;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;

final class ApplicationProtocolUtil
{
    private static final int DEFAULT_LIST_SIZE = 2;
    
    private ApplicationProtocolUtil() {
    }
    
    static List<String> toList(final Iterable<String> protocols) {
        return toList(2, protocols);
    }
    
    static List<String> toList(final int initialListSize, final Iterable<String> protocols) {
        if (protocols == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(initialListSize);
        for (final String p : protocols) {
            result.add(ObjectUtil.checkNonEmpty(p, "p"));
        }
        return ObjectUtil.checkNonEmpty(result, "result");
    }
    
    static List<String> toList(final String... protocols) {
        return toList(2, protocols);
    }
    
    static List<String> toList(final int initialListSize, final String... protocols) {
        if (protocols == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(initialListSize);
        for (final String p : protocols) {
            result.add(ObjectUtil.checkNonEmpty(p, "p"));
        }
        return ObjectUtil.checkNonEmpty(result, "result");
    }
}

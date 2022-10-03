package io.opencensus.tags;

import java.util.Iterator;

public final class InternalUtils
{
    private InternalUtils() {
    }
    
    public static Iterator<Tag> getTags(final TagContext tags) {
        return tags.getIterator();
    }
}

package io.netty.util;

public interface AttributeMap
{
     <T> Attribute<T> attr(final AttributeKey<T> p0);
    
     <T> boolean hasAttr(final AttributeKey<T> p0);
}

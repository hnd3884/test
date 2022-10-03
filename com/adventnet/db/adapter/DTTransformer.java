package com.adventnet.db.adapter;

public interface DTTransformer
{
    Object transform(final String p0, final String p1, final Object p2, final String p3) throws Exception;
    
    Object unTransform(final String p0, final String p1, final Object p2, final String p3) throws Exception;
}

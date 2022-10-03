package com.me.ems.summaryserver.common;

import java.util.List;

public interface ProbeActionAPI
{
    void addToProbeActionQueue(final Long p0, final Integer p1, final Integer p2, final Integer p3, final Object p4) throws Exception;
    
    void addToProbeActionEventIDs(final List<Integer> p0);
    
    List<Integer> getProbeEventIDs();
}

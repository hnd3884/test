package com.me.mdm.api;

import java.util.List;

public interface APIActionsListener
{
    boolean addResourceServiceMapping(final List<Long> p0, final List<Long> p1, final Long p2);
    
    boolean removeResourceServiceMapping(final List<Long> p0, final Long p1);
    
    boolean addProfileServiceMapping(final List<Long> p0, final List<Long> p1, final Long p2);
    
    boolean removeProfileServiceMapping(final List<Long> p0, final Long p1);
    
    boolean addERIDServiceMapping(final List<Long> p0, final Long p1);
    
    boolean removeERIDServiceMapping(final List<Long> p0, final Long p1);
}

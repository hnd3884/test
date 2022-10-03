package com.me.mdm.api.controller;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public interface IDauthorizer
{
    void authorize(final String p0, final Long p1, final String p2, final List<Object> p3) throws Exception;
    
    default Long[] convertStringListToLongAr(final List<Object> idList) {
        final List<Long> lList = new ArrayList<Long>();
        for (final Object obj : idList) {
            lList.add(Long.parseLong(String.valueOf(obj)));
        }
        return lList.toArray(new Long[lList.size()]);
    }
}

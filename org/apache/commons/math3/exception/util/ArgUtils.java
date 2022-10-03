package org.apache.commons.math3.exception.util;

import java.util.List;
import java.util.ArrayList;

public class ArgUtils
{
    private ArgUtils() {
    }
    
    public static Object[] flatten(final Object[] array) {
        final List<Object> list = new ArrayList<Object>();
        if (array != null) {
            for (final Object o : array) {
                if (o instanceof Object[]) {
                    for (final Object oR : flatten((Object[])o)) {
                        list.add(oR);
                    }
                }
                else {
                    list.add(o);
                }
            }
        }
        return list.toArray();
    }
}

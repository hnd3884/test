package com.sun.corba.se.impl.orb;

import java.util.Hashtable;
import java.util.Iterator;
import java.lang.reflect.Array;
import com.sun.corba.se.spi.orb.StringPair;
import java.util.LinkedList;
import java.util.Properties;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class PrefixParserAction extends ParserActionBase
{
    private Class componentType;
    private ORBUtilSystemException wrapper;
    
    public PrefixParserAction(final String s, final Operation operation, final String s2, final Class componentType) {
        super(s, true, operation, s2);
        this.componentType = componentType;
        this.wrapper = ORBUtilSystemException.get("orb.lifecycle");
    }
    
    @Override
    public Object apply(final Properties properties) {
        String s = this.getPropertyName();
        int length = s.length();
        if (s.charAt(length - 1) != '.') {
            s += '.';
            ++length;
        }
        final LinkedList list = new LinkedList();
        for (final String s2 : ((Hashtable<String, V>)properties).keySet()) {
            if (s2.startsWith(s)) {
                list.add(this.getOperation().operate(new StringPair(s2.substring(length), properties.getProperty(s2))));
            }
        }
        final int size = list.size();
        if (size > 0) {
            Object instance;
            try {
                instance = Array.newInstance(this.componentType, size);
            }
            catch (final Throwable t) {
                throw this.wrapper.couldNotCreateArray(t, this.getPropertyName(), this.componentType, new Integer(size));
            }
            final Iterator iterator2 = list.iterator();
            int n = 0;
            while (iterator2.hasNext()) {
                final Object next = iterator2.next();
                try {
                    Array.set(instance, n, next);
                }
                catch (final Throwable t2) {
                    throw this.wrapper.couldNotSetArray(t2, this.getPropertyName(), new Integer(n), this.componentType, new Integer(size), next.toString());
                }
                ++n;
            }
            return instance;
        }
        return null;
    }
}

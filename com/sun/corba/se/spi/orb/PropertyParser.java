package com.sun.corba.se.spi.orb;

import java.util.Iterator;
import com.sun.corba.se.impl.orb.ParserAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.sun.corba.se.impl.orb.ParserActionFactory;
import java.util.LinkedList;
import java.util.List;

public class PropertyParser
{
    private List actions;
    
    public PropertyParser() {
        this.actions = new LinkedList();
    }
    
    public PropertyParser add(final String s, final Operation operation, final String s2) {
        this.actions.add(ParserActionFactory.makeNormalAction(s, operation, s2));
        return this;
    }
    
    public PropertyParser addPrefix(final String s, final Operation operation, final String s2, final Class clazz) {
        this.actions.add(ParserActionFactory.makePrefixAction(s, operation, s2, clazz));
        return this;
    }
    
    public Map parse(final Properties properties) {
        final HashMap hashMap = new HashMap();
        for (final ParserAction parserAction : this.actions) {
            final Object apply = parserAction.apply(properties);
            if (apply != null) {
                hashMap.put(parserAction.getFieldName(), apply);
            }
        }
        return hashMap;
    }
    
    public Iterator iterator() {
        return this.actions.iterator();
    }
}

package org.apache.commons.chain.impl;

import java.util.Iterator;
import org.apache.commons.chain.Command;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.chain.Catalog;

public class CatalogBase implements Catalog
{
    protected Map commands;
    
    public CatalogBase() {
        this.commands = Collections.synchronizedMap(new HashMap<Object, Object>());
    }
    
    public CatalogBase(final Map commands) {
        this.commands = Collections.synchronizedMap(new HashMap<Object, Object>());
        this.commands = Collections.synchronizedMap((Map<Object, Object>)commands);
    }
    
    public void addCommand(final String name, final Command command) {
        this.commands.put(name, command);
    }
    
    public Command getCommand(final String name) {
        return this.commands.get(name);
    }
    
    public Iterator getNames() {
        return this.commands.keySet().iterator();
    }
    
    public String toString() {
        final Iterator names = this.getNames();
        final StringBuffer str = new StringBuffer("[" + this.getClass().getName() + ": ");
        while (names.hasNext()) {
            str.append(names.next());
            if (names.hasNext()) {
                str.append(", ");
            }
        }
        str.append("]");
        return str.toString();
    }
}

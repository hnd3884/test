package com.sun.org.apache.xml.internal.resolver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Vector;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CatalogEntry
{
    protected static AtomicInteger nextEntry;
    protected static final Map<String, Integer> entryTypes;
    protected static Vector entryArgs;
    protected int entryType;
    protected Vector args;
    
    static int addEntryType(final String name, final int numArgs) {
        final int index = CatalogEntry.nextEntry.getAndIncrement();
        CatalogEntry.entryTypes.put(name, index);
        CatalogEntry.entryArgs.add(index, numArgs);
        return index;
    }
    
    public static int getEntryType(final String name) throws CatalogException {
        if (!CatalogEntry.entryTypes.containsKey(name)) {
            throw new CatalogException(3);
        }
        final Integer iType = CatalogEntry.entryTypes.get(name);
        if (iType == null) {
            throw new CatalogException(3);
        }
        return iType;
    }
    
    public static int getEntryArgCount(final String name) throws CatalogException {
        return getEntryArgCount(getEntryType(name));
    }
    
    public static int getEntryArgCount(final int type) throws CatalogException {
        try {
            final Integer iArgs = CatalogEntry.entryArgs.get(type);
            return iArgs;
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new CatalogException(3);
        }
    }
    
    public CatalogEntry() {
        this.entryType = 0;
        this.args = null;
    }
    
    public CatalogEntry(final String name, final Vector args) throws CatalogException {
        this.entryType = 0;
        this.args = null;
        final Integer iType = CatalogEntry.entryTypes.get(name);
        if (iType == null) {
            throw new CatalogException(3);
        }
        final int type = iType;
        try {
            final Integer iArgs = CatalogEntry.entryArgs.get(type);
            if (iArgs != args.size()) {
                throw new CatalogException(2);
            }
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new CatalogException(3);
        }
        this.entryType = type;
        this.args = args;
    }
    
    public CatalogEntry(final int type, final Vector args) throws CatalogException {
        this.entryType = 0;
        this.args = null;
        try {
            final Integer iArgs = CatalogEntry.entryArgs.get(type);
            if (iArgs != args.size()) {
                throw new CatalogException(2);
            }
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new CatalogException(3);
        }
        this.entryType = type;
        this.args = args;
    }
    
    public int getEntryType() {
        return this.entryType;
    }
    
    public String getEntryArg(final int argNum) {
        try {
            final String arg = this.args.get(argNum);
            return arg;
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    public void setEntryArg(final int argNum, final String newspec) throws ArrayIndexOutOfBoundsException {
        this.args.set(argNum, newspec);
    }
    
    static {
        CatalogEntry.nextEntry = new AtomicInteger(0);
        entryTypes = new ConcurrentHashMap<String, Integer>();
        CatalogEntry.entryArgs = new Vector();
    }
}

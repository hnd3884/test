package com.sun.corba.se.impl.orbutil;

public abstract class RepositoryIdFactory
{
    private static final RepIdDelegator currentDelegator;
    
    public static RepositoryIdStrings getRepIdStringsFactory() {
        return RepositoryIdFactory.currentDelegator;
    }
    
    public static RepositoryIdUtility getRepIdUtility() {
        return RepositoryIdFactory.currentDelegator;
    }
    
    static {
        currentDelegator = new RepIdDelegator();
    }
}

package com.sun.corba.se.impl.naming.namingutil;

public class INSURLHandler
{
    private static INSURLHandler insURLHandler;
    private static final int CORBALOC_PREFIX_LENGTH = 9;
    private static final int CORBANAME_PREFIX_LENGTH = 10;
    
    private INSURLHandler() {
    }
    
    public static synchronized INSURLHandler getINSURLHandler() {
        if (INSURLHandler.insURLHandler == null) {
            INSURLHandler.insURLHandler = new INSURLHandler();
        }
        return INSURLHandler.insURLHandler;
    }
    
    public INSURL parseURL(final String s) {
        if (s.startsWith("corbaloc:")) {
            return new CorbalocURL(s.substring(9));
        }
        if (s.startsWith("corbaname:")) {
            return new CorbanameURL(s.substring(10));
        }
        return null;
    }
    
    static {
        INSURLHandler.insURLHandler = null;
    }
}

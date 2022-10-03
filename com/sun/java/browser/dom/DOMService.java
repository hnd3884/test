package com.sun.java.browser.dom;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public abstract class DOMService
{
    public static DOMService getService(final Object o) throws DOMUnsupportedException {
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("com.sun.java.browser.dom.DOMServiceProvider"));
            return (DOMService)Class.forName("sun.plugin.dom.DOMService").newInstance();
        }
        catch (final Throwable t) {
            throw new DOMUnsupportedException(t.toString());
        }
    }
    
    public abstract Object invokeAndWait(final DOMAction p0) throws DOMAccessException;
    
    public abstract void invokeLater(final DOMAction p0);
}

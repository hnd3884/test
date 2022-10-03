package com.sun.mail.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.mail.internet.MimePart;
import java.lang.reflect.Method;

public class MimeUtil
{
    private static final Method cleanContentType;
    
    private MimeUtil() {
    }
    
    public static String cleanContentType(final MimePart mp, final String contentType) {
        if (MimeUtil.cleanContentType != null) {
            try {
                return (String)MimeUtil.cleanContentType.invoke(null, mp, contentType);
            }
            catch (final Exception ex) {
                return contentType;
            }
        }
        return contentType;
    }
    
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (final SecurityException ex) {}
                return cl;
            }
        });
    }
    
    static {
        Method meth = null;
        try {
            final String cth = System.getProperty("mail.mime.contenttypehandler");
            if (cth != null) {
                final ClassLoader cl = getContextClassLoader();
                Class<?> clsHandler = null;
                if (cl != null) {
                    try {
                        clsHandler = Class.forName(cth, false, cl);
                    }
                    catch (final ClassNotFoundException ex) {}
                }
                if (clsHandler == null) {
                    clsHandler = Class.forName(cth);
                }
                meth = clsHandler.getMethod("cleanContentType", MimePart.class, String.class);
            }
        }
        catch (final ClassNotFoundException ex2) {}
        catch (final NoSuchMethodException ex3) {}
        catch (final RuntimeException ex4) {}
        finally {
            cleanContentType = meth;
        }
    }
}

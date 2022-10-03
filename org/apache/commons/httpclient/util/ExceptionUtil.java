package org.apache.commons.httpclient.util;

import org.apache.commons.logging.LogFactory;
import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;

public class ExceptionUtil
{
    private static final Log LOG;
    private static final Method INIT_CAUSE_METHOD;
    private static final Class SOCKET_TIMEOUT_CLASS;
    
    private static Method getInitCauseMethod() {
        try {
            final Class[] paramsClasses = { Throwable.class };
            return Throwable.class.getMethod("initCause", (Class[])paramsClasses);
        }
        catch (final NoSuchMethodException e) {
            return null;
        }
    }
    
    private static Class SocketTimeoutExceptionClass() {
        try {
            return Class.forName("java.net.SocketTimeoutException");
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }
    
    public static void initCause(final Throwable throwable, final Throwable cause) {
        if (ExceptionUtil.INIT_CAUSE_METHOD != null) {
            try {
                ExceptionUtil.INIT_CAUSE_METHOD.invoke(throwable, cause);
            }
            catch (final Exception e) {
                ExceptionUtil.LOG.warn((Object)"Exception invoking Throwable.initCause", (Throwable)e);
            }
        }
    }
    
    public static boolean isSocketTimeoutException(final InterruptedIOException e) {
        return ExceptionUtil.SOCKET_TIMEOUT_CLASS == null || ExceptionUtil.SOCKET_TIMEOUT_CLASS.isInstance(e);
    }
    
    static {
        LOG = LogFactory.getLog(ExceptionUtil.class);
        INIT_CAUSE_METHOD = getInitCauseMethod();
        SOCKET_TIMEOUT_CLASS = SocketTimeoutExceptionClass();
    }
}

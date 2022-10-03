package org.apache.xmlbeans.impl.jam.internal;

import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;

public abstract class TigerDelegate
{
    private static final String SOME_TIGER_SPECIFIC_JAVADOC_CLASS = "com.sun.javadoc.AnnotationDesc";
    private static final String SOME_TIGER_SPECIFIC_REFLECT_CLASS = "java.lang.annotation.Annotation";
    protected JamLogger mLogger;
    @Deprecated
    protected ElementContext mContext;
    private static boolean m14RuntimeWarningDone;
    private static boolean m14BuildWarningDone;
    
    @Deprecated
    public void init(final ElementContext ctx) {
        this.mContext = ctx;
        this.init(ctx.getLogger());
    }
    
    public void init(final JamLogger log) {
        this.mLogger = log;
    }
    
    protected TigerDelegate() {
        this.mLogger = null;
        this.mContext = null;
    }
    
    protected JamLogger getLogger() {
        return this.mLogger;
    }
    
    protected static void issue14BuildWarning(final Throwable t, final JamLogger log) {
        if (!TigerDelegate.m14BuildWarningDone) {
            log.warning("This build of JAM was not made with JDK 1.5.Even though you are now running under JDK 1.5, JSR175-style annotations will not be available");
            if (log.isVerbose(TigerDelegate.class)) {
                log.verbose(t);
            }
            TigerDelegate.m14BuildWarningDone = true;
        }
    }
    
    protected static void issue14RuntimeWarning(final Throwable t, final JamLogger log) {
        if (!TigerDelegate.m14RuntimeWarningDone) {
            log.warning("You are running under a pre-1.5 JDK.  JSR175-style source annotations will not be available");
            if (log.isVerbose(TigerDelegate.class)) {
                log.verbose(t);
            }
            TigerDelegate.m14RuntimeWarningDone = true;
        }
    }
    
    protected static boolean isTigerJavadocAvailable(final JamLogger logger) {
        try {
            Class.forName("com.sun.javadoc.AnnotationDesc");
            return true;
        }
        catch (final ClassNotFoundException e) {
            issue14RuntimeWarning(e, logger);
            return false;
        }
    }
    
    protected static boolean isTigerReflectionAvailable(final JamLogger logger) {
        try {
            Class.forName("java.lang.annotation.Annotation");
            return true;
        }
        catch (final ClassNotFoundException e) {
            issue14RuntimeWarning(e, logger);
            return false;
        }
    }
    
    static {
        TigerDelegate.m14RuntimeWarningDone = false;
        TigerDelegate.m14BuildWarningDone = false;
    }
}

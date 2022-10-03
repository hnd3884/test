package org.apache.xmlbeans.impl.jam.internal.reflect;

import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import java.lang.reflect.Field;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;
import org.apache.xmlbeans.impl.jam.mutable.MMember;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import org.apache.xmlbeans.impl.jam.internal.TigerDelegate;

public abstract class ReflectTigerDelegate extends TigerDelegate
{
    private static final String IMPL_NAME = "org.apache.xmlbeans.impl.jam.internal.reflect.ReflectTigerDelegateImpl_150";
    
    public static ReflectTigerDelegate create(final JamLogger logger) {
        if (!TigerDelegate.isTigerReflectionAvailable(logger)) {
            return null;
        }
        try {
            final ReflectTigerDelegate out = (ReflectTigerDelegate)Class.forName("org.apache.xmlbeans.impl.jam.internal.reflect.ReflectTigerDelegateImpl_150").newInstance();
            out.init(logger);
            return out;
        }
        catch (final ClassNotFoundException e) {
            TigerDelegate.issue14BuildWarning(e, logger);
        }
        catch (final IllegalAccessException e2) {
            logger.error(e2);
        }
        catch (final InstantiationException e3) {
            logger.error(e3);
        }
        return null;
    }
    
    @Deprecated
    public static ReflectTigerDelegate create(final ElementContext ctx) {
        if (!TigerDelegate.isTigerReflectionAvailable(ctx.getLogger())) {
            return null;
        }
        try {
            final ReflectTigerDelegate out = (ReflectTigerDelegate)Class.forName("org.apache.xmlbeans.impl.jam.internal.reflect.ReflectTigerDelegateImpl_150").newInstance();
            out.init(ctx);
            return out;
        }
        catch (final ClassNotFoundException e) {
            TigerDelegate.issue14BuildWarning(e, ctx.getLogger());
        }
        catch (final IllegalAccessException e2) {
            ctx.getLogger().error(e2);
        }
        catch (final InstantiationException e3) {
            ctx.getLogger().error(e3);
        }
        return null;
    }
    
    protected ReflectTigerDelegate() {
    }
    
    public abstract void populateAnnotationTypeIfNecessary(final Class p0, final MClass p1, final ReflectClassBuilder p2);
    
    public abstract boolean isEnum(final Class p0);
    
    public abstract Constructor getEnclosingConstructor(final Class p0);
    
    public abstract Method getEnclosingMethod(final Class p0);
    
    public abstract void extractAnnotations(final MMember p0, final Method p1);
    
    public abstract void extractAnnotations(final MConstructor p0, final Constructor p1);
    
    public abstract void extractAnnotations(final MField p0, final Field p1);
    
    public abstract void extractAnnotations(final MClass p0, final Class p1);
    
    public abstract void extractAnnotations(final MParameter p0, final Method p1, final int p2);
    
    public abstract void extractAnnotations(final MParameter p0, final Constructor p1, final int p2);
}

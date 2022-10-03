package org.apache.xmlbeans.impl.jam.internal.javadoc;

import com.sun.javadoc.Parameter;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.ProgramElementDoc;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotatedElement;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import com.sun.javadoc.ClassDoc;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import org.apache.xmlbeans.impl.jam.internal.TigerDelegate;

public abstract class JavadocTigerDelegate extends TigerDelegate
{
    private static final String JAVADOC_DELEGATE_IMPL = "org.apache.xmlbeans.impl.jam.internal.javadoc.JavadocTigerDelegateImpl_150";
    public static final String ANNOTATION_DEFAULTS_DISABLED_PROPERTY = "ANNOTATION_DEFAULTS_DISABLED_PROPERTY";
    
    public static JavadocTigerDelegate create(final JamLogger logger) {
        if (!TigerDelegate.isTigerJavadocAvailable(logger)) {
            return null;
        }
        try {
            final JavadocTigerDelegate out = (JavadocTigerDelegate)Class.forName("org.apache.xmlbeans.impl.jam.internal.javadoc.JavadocTigerDelegateImpl_150").newInstance();
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
    public static JavadocTigerDelegate create(final ElementContext ctx) {
        if (!TigerDelegate.isTigerJavadocAvailable(ctx.getLogger())) {
            return null;
        }
        try {
            final JavadocTigerDelegate out = (JavadocTigerDelegate)Class.forName("org.apache.xmlbeans.impl.jam.internal.javadoc.JavadocTigerDelegateImpl_150").newInstance();
            out.init(ctx);
            return out;
        }
        catch (final ClassNotFoundException e) {
            ctx.getLogger().error(e);
        }
        catch (final IllegalAccessException e2) {
            ctx.getLogger().error(e2);
        }
        catch (final InstantiationException e3) {
            ctx.getLogger().error(e3);
        }
        return null;
    }
    
    public abstract boolean isEnum(final ClassDoc p0);
    
    @Override
    public abstract void init(final JamLogger p0);
    
    public abstract void populateAnnotationTypeIfNecessary(final ClassDoc p0, final MClass p1, final JavadocClassBuilder p2);
    
    @Deprecated
    public abstract void extractAnnotations(final MAnnotatedElement p0, final ProgramElementDoc p1);
    
    @Deprecated
    public abstract void extractAnnotations(final MAnnotatedElement p0, final ExecutableMemberDoc p1, final Parameter p2);
}

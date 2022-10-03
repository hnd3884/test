package org.apache.xmlbeans.impl.jam.provider;

import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;
import org.apache.xmlbeans.impl.jam.internal.parser.ParserClassBuilder;
import org.apache.xmlbeans.impl.jam.internal.javadoc.JavadocClassBuilder;
import org.apache.xmlbeans.impl.jam.JClass;
import java.util.List;
import org.apache.xmlbeans.impl.jam.internal.reflect.ReflectClassBuilder;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.jam.internal.JamClassLoaderImpl;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import java.io.IOException;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import org.apache.xmlbeans.impl.jam.internal.JamServiceImpl;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.JamService;
import org.apache.xmlbeans.impl.jam.internal.JamServiceContextImpl;
import org.apache.xmlbeans.impl.jam.JamServiceParams;
import org.apache.xmlbeans.impl.jam.JamServiceFactory;

public class JamServiceFactoryImpl extends JamServiceFactory
{
    public static final String USE_NEW_PARSER = "JamServiceFactoryImpl.use-new-parser";
    private static final String PREFIX = "[JamServiceFactoryImpl]";
    
    @Override
    public JamServiceParams createServiceParams() {
        return new JamServiceContextImpl();
    }
    
    @Override
    public JamService createService(final JamServiceParams jsps) throws IOException {
        if (!(jsps instanceof JamServiceContextImpl)) {
            throw new IllegalArgumentException("JamServiceParams must be instantiated by this JamServiceFactory.");
        }
        final JamClassLoader clToUse = this.createClassLoader((JamServiceContext)jsps);
        ((JamServiceContextImpl)jsps).setClassLoader(clToUse);
        return new JamServiceImpl((ElementContext)jsps, this.getSpecifiedClasses((JamServiceContext)jsps));
    }
    
    @Override
    public JamClassLoader createSystemJamClassLoader() {
        final JamServiceParams params = this.createServiceParams();
        params.setUseSystemClasspath(true);
        try {
            final JamService service = this.createService(params);
            return service.getClassLoader();
        }
        catch (final IOException reallyUnexpected) {
            reallyUnexpected.printStackTrace();
            throw new IllegalStateException(reallyUnexpected.getMessage());
        }
    }
    
    @Override
    public JamClassLoader createJamClassLoader(final ClassLoader cl) {
        final JamServiceParams params = this.createServiceParams();
        params.setUseSystemClasspath(false);
        params.setPropertyInitializer(null);
        params.addClassLoader(cl);
        try {
            final JamService service = this.createService(params);
            return service.getClassLoader();
        }
        catch (final IOException reallyUnexpected) {
            reallyUnexpected.printStackTrace();
            throw new IllegalStateException(reallyUnexpected.getMessage());
        }
    }
    
    protected String[] getSpecifiedClasses(final JamServiceContext params) throws IOException {
        return params.getAllClassnames();
    }
    
    protected JamClassLoader createClassLoader(final JamServiceContext ctx) throws IOException {
        final JamClassBuilder builder = this.createBuilder(ctx);
        return new JamClassLoaderImpl((ElementContext)ctx, builder, ctx.getInitializer());
    }
    
    protected JamClassBuilder createBuilder(final JamServiceContext ctx) throws IOException {
        final JamLogger log = ctx.getLogger();
        final List builders = new ArrayList();
        JamClassBuilder b = ctx.getBaseBuilder();
        if (b != null) {
            builders.add(b);
        }
        b = this.createSourceBuilder(ctx);
        if (log.isVerbose(this)) {
            log.verbose("added classbuilder for sources");
        }
        if (b != null) {
            builders.add(b);
        }
        b = this.createClassfileBuilder(ctx);
        if (log.isVerbose(this)) {
            log.verbose("added classbuilder for custom classpath");
        }
        if (b != null) {
            builders.add(b);
        }
        final ClassLoader[] cls = ctx.getReflectionClassLoaders();
        for (int i = 0; i < cls.length; ++i) {
            if (log.isVerbose(this)) {
                log.verbose("added classbuilder for classloader " + cls[i].getClass());
            }
            builders.add(new ReflectClassBuilder(cls[i]));
        }
        final JamClassBuilder[] barray = new JamClassBuilder[builders.size()];
        builders.toArray(barray);
        final JamClassBuilder out = new CompositeJamClassBuilder(barray);
        out.init((ElementContext)ctx);
        if (log.isVerbose(this)) {
            log.verbose("returning a composite of " + barray.length + " class builders.");
            JClass c = out.build("java.lang", "Object");
            c = out.build("javax.ejb", "SessionBean");
        }
        return out;
    }
    
    protected JamClassBuilder createSourceBuilder(final JamServiceContext ctx) throws IOException {
        final File[] sources = ctx.getSourceFiles();
        if (sources == null || sources.length == 0) {
            if (ctx.isVerbose(this)) {
                ctx.verbose("[JamServiceFactoryImpl]no source files present, skipping source ClassBuilder");
            }
            return null;
        }
        if (ctx.getProperty("JamServiceFactoryImpl.use-new-parser") == null) {
            return new JavadocClassBuilder();
        }
        return new ParserClassBuilder(ctx);
    }
    
    protected JamClassBuilder createClassfileBuilder(final JamServiceContext jp) throws IOException {
        final ResourcePath cp = jp.getInputClasspath();
        if (cp == null) {
            return null;
        }
        final URL[] urls = cp.toUrlPath();
        final ClassLoader cl = new URLClassLoader(urls);
        return new ReflectClassBuilder(cl);
    }
}

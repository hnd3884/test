package org.msgpack.template.builder;

import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import javassist.NotFoundException;
import javassist.CtClass;
import java.util.logging.Level;
import java.lang.reflect.Type;
import javassist.ClassPath;
import javassist.LoaderClassPath;
import org.msgpack.template.TemplateRegistry;
import javassist.ClassPool;
import java.util.logging.Logger;

public class JavassistTemplateBuilder extends AbstractTemplateBuilder
{
    private static Logger LOG;
    protected ClassPool pool;
    protected int seqId;
    
    public JavassistTemplateBuilder(final TemplateRegistry registry) {
        super(registry);
        this.seqId = 0;
        this.pool = new ClassPool();
        boolean appended = false;
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                this.pool.appendClassPath((ClassPath)new LoaderClassPath(cl));
                appended = true;
            }
        }
        catch (final SecurityException e) {
            JavassistTemplateBuilder.LOG.fine("Cannot append a search path of context classloader");
            e.printStackTrace();
        }
        try {
            final ClassLoader cl2 = this.getClass().getClassLoader();
            if (cl2 != null && cl2 != cl) {
                this.pool.appendClassPath((ClassPath)new LoaderClassPath(cl2));
                appended = true;
            }
        }
        catch (final SecurityException e) {
            JavassistTemplateBuilder.LOG.fine("Cannot append a search path of classloader");
            e.printStackTrace();
        }
        if (!appended) {
            this.pool.appendSystemPath();
        }
    }
    
    @Override
    public boolean matchType(final Type targetType, final boolean hasAnnotation) {
        final Class<?> targetClass = (Class<?>)targetType;
        final boolean matched = AbstractTemplateBuilder.matchAtClassTemplateBuilder(targetClass, hasAnnotation);
        if (matched && JavassistTemplateBuilder.LOG.isLoggable(Level.FINE)) {
            JavassistTemplateBuilder.LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }
    
    public void addClassLoader(final ClassLoader cl) {
        this.pool.appendClassPath((ClassPath)new LoaderClassPath(cl));
    }
    
    protected CtClass makeCtClass(final String className) {
        return this.pool.makeClass(className);
    }
    
    protected CtClass getCtClass(final String className) throws NotFoundException {
        return this.pool.get(className);
    }
    
    protected int nextSeqId() {
        return this.seqId++;
    }
    
    protected BuildContext createBuildContext() {
        return new DefaultBuildContext(this);
    }
    
    public <T> Template<T> buildTemplate(final Class<T> targetClass, final FieldEntry[] entries) {
        final Template<?>[] tmpls = this.toTemplate(entries);
        final BuildContext bc = this.createBuildContext();
        return bc.buildTemplate(targetClass, entries, tmpls);
    }
    
    private Template<?>[] toTemplate(final FieldEntry[] from) {
        final Template<?>[] tmpls = new Template[from.length];
        for (int i = 0; i < from.length; ++i) {
            final FieldEntry e = from[i];
            if (!e.isAvailable()) {
                tmpls[i] = null;
            }
            else {
                final Template<?> tmpl = this.registry.lookup(e.getGenericType());
                tmpls[i] = tmpl;
            }
        }
        return tmpls;
    }
    
    @Override
    public void writeTemplate(final Type targetType, final String directoryName) {
        final Class<?> targetClass = (Class<?>)targetType;
        this.checkClassValidation(targetClass);
        final FieldOption implicitOption = this.getFieldOption(targetClass);
        final FieldEntry[] entries = this.toFieldEntries(targetClass, implicitOption);
        this.writeTemplate(targetClass, entries, directoryName);
    }
    
    private void writeTemplate(final Class<?> targetClass, final FieldEntry[] entries, final String directoryName) {
        final Template[] tmpls = this.toTemplate(entries);
        final BuildContext bc = this.createBuildContext();
        bc.writeTemplate(targetClass, entries, tmpls, directoryName);
    }
    
    @Override
    public <T> Template<T> loadTemplate(final Type targetType) {
        final Class<T> targetClass = (Class<T>)targetType;
        try {
            final String tmplName = targetClass.getName() + "_$$_Template";
            final ClassLoader cl = targetClass.getClassLoader();
            if (cl == null) {
                return null;
            }
            cl.loadClass(tmplName);
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
        final FieldOption implicitOption = this.getFieldOption(targetClass);
        final FieldEntry[] entries = this.toFieldEntries(targetClass, implicitOption);
        final Template<?>[] tmpls = this.toTemplate(entries);
        final BuildContext bc = this.createBuildContext();
        return bc.loadTemplate(targetClass, entries, tmpls);
    }
    
    static {
        JavassistTemplateBuilder.LOG = Logger.getLogger(JavassistTemplateBuilder.class.getName());
    }
    
    public abstract static class JavassistTemplate<T> extends AbstractTemplate<T>
    {
        public Class<T> targetClass;
        public Template<?>[] templates;
        
        public JavassistTemplate(final Class<T> targetClass, final Template<?>[] templates) {
            this.targetClass = targetClass;
            this.templates = templates;
        }
    }
}

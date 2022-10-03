package org.glassfish.jersey.server.internal.scanning;

import org.glassfish.jersey.internal.OsgiRegistry;
import java.security.PrivilegedActionException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.security.PrivilegedExceptionAction;
import jersey.repackaged.org.objectweb.asm.MethodVisitor;
import jersey.repackaged.org.objectweb.asm.Attribute;
import jersey.repackaged.org.objectweb.asm.FieldVisitor;
import jersey.repackaged.org.objectweb.asm.AnnotationVisitor;
import java.io.IOException;
import jersey.repackaged.org.objectweb.asm.ClassVisitor;
import jersey.repackaged.org.objectweb.asm.ClassReader;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.annotation.Annotation;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Path;
import java.util.Set;

public final class AnnotationAcceptingListener implements ResourceProcessor
{
    private final ClassLoader classloader;
    private final Set<Class<?>> classes;
    private final Set<String> annotations;
    private final AnnotatedClassVisitor classVisitor;
    
    public static AnnotationAcceptingListener newJaxrsResourceAndProviderListener() {
        return new AnnotationAcceptingListener((Class<? extends Annotation>[])new Class[] { Path.class, Provider.class });
    }
    
    public static AnnotationAcceptingListener newJaxrsResourceAndProviderListener(final ClassLoader classLoader) {
        return new AnnotationAcceptingListener(classLoader, (Class<? extends Annotation>[])new Class[] { Path.class, Provider.class });
    }
    
    public AnnotationAcceptingListener(final Class<? extends Annotation>... annotations) {
        this(AccessController.doPrivileged((PrivilegedAction<ClassLoader>)ReflectionHelper.getContextClassLoaderPA()), annotations);
    }
    
    public AnnotationAcceptingListener(final ClassLoader classloader, final Class<? extends Annotation>... annotations) {
        this.classloader = classloader;
        this.classes = new LinkedHashSet<Class<?>>();
        this.annotations = this.getAnnotationSet(annotations);
        this.classVisitor = new AnnotatedClassVisitor();
    }
    
    public Set<Class<?>> getAnnotatedClasses() {
        return this.classes;
    }
    
    private Set<String> getAnnotationSet(final Class<? extends Annotation>... annotations) {
        final Set<String> a = new HashSet<String>();
        for (final Class c : annotations) {
            a.add("L" + c.getName().replaceAll("\\.", "/") + ";");
        }
        return a;
    }
    
    @Override
    public boolean accept(final String name) {
        return name != null && !name.isEmpty() && name.endsWith(".class");
    }
    
    @Override
    public void process(final String name, final InputStream in) throws IOException {
        new ClassReader(in).accept(this.classVisitor, 0);
    }
    
    private final class AnnotatedClassVisitor extends ClassVisitor
    {
        private String className;
        private boolean isScoped;
        private boolean isAnnotated;
        
        private AnnotatedClassVisitor() {
            super(327680);
        }
        
        @Override
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
            this.className = name;
            this.isScoped = ((access & 0x1) != 0x0);
            this.isAnnotated = false;
        }
        
        @Override
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            this.isAnnotated |= AnnotationAcceptingListener.this.annotations.contains(desc);
            return null;
        }
        
        @Override
        public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
            if (this.className.equals(name)) {
                this.isScoped = ((access & 0x1) != 0x0);
                this.isScoped &= ((access & 0x8) == 0x8);
            }
        }
        
        @Override
        public void visitEnd() {
            if (this.isScoped && this.isAnnotated) {
                AnnotationAcceptingListener.this.classes.add(this.getClassForName(this.className.replaceAll("/", ".")));
            }
        }
        
        @Override
        public void visitOuterClass(final String string, final String string0, final String string1) {
        }
        
        @Override
        public FieldVisitor visitField(final int i, final String string, final String string0, final String string1, final Object object) {
            return null;
        }
        
        @Override
        public void visitSource(final String string, final String string0) {
        }
        
        @Override
        public void visitAttribute(final Attribute attribute) {
        }
        
        @Override
        public MethodVisitor visitMethod(final int i, final String string, final String string0, final String string1, final String[] string2) {
            return null;
        }
        
        private Class getClassForName(final String className) {
            try {
                final OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
                if (osgiRegistry != null) {
                    return osgiRegistry.classForNameWithException(className);
                }
                return AccessController.doPrivileged((PrivilegedExceptionAction<Class>)ReflectionHelper.classForNameWithExceptionPEA(className, AnnotationAcceptingListener.this.classloader));
            }
            catch (final ClassNotFoundException ex) {
                throw new RuntimeException(LocalizationMessages.ERROR_SCANNING_CLASS_NOT_FOUND(className), ex);
            }
            catch (final PrivilegedActionException pae) {
                final Throwable cause = pae.getCause();
                if (cause instanceof ClassNotFoundException) {
                    throw new RuntimeException(LocalizationMessages.ERROR_SCANNING_CLASS_NOT_FOUND(className), cause);
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new RuntimeException(cause);
            }
        }
    }
}

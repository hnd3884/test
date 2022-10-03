package org.apache.xmlbeans.impl.jam.internal.reflect;

import org.apache.xmlbeans.impl.jam.mutable.MMember;
import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import org.apache.xmlbeans.impl.jam.mutable.MInvokable;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.provider.JamClassPopulator;
import org.apache.xmlbeans.impl.jam.provider.JamClassBuilder;

public class ReflectClassBuilder extends JamClassBuilder implements JamClassPopulator
{
    private ClassLoader mLoader;
    private ReflectTigerDelegate mTigerDelegate;
    
    public ReflectClassBuilder(final ClassLoader rcl) {
        this.mTigerDelegate = null;
        if (rcl == null) {
            throw new IllegalArgumentException("null rcl");
        }
        this.mLoader = rcl;
    }
    
    @Override
    public void init(final ElementContext ctx) {
        super.init(ctx);
        this.initDelegate(ctx);
    }
    
    @Override
    public MClass build(final String packageName, final String className) {
        this.assertInitialized();
        if (this.getLogger().isVerbose(this)) {
            this.getLogger().verbose("trying to build '" + packageName + "' '" + className + "'");
        }
        Class rclass;
        try {
            final String loadme = (packageName.trim().length() > 0) ? (packageName + '.' + className) : className;
            rclass = this.mLoader.loadClass(loadme);
        }
        catch (final ClassNotFoundException cnfe) {
            this.getLogger().verbose(cnfe, this);
            return null;
        }
        final MClass out = this.createClassToBuild(packageName, className, null, this);
        out.setArtifact(rclass);
        return out;
    }
    
    @Override
    public void populate(final MClass dest) {
        this.assertInitialized();
        final Class src = (Class)dest.getArtifact();
        dest.setModifiers(src.getModifiers());
        dest.setIsInterface(src.isInterface());
        if (this.mTigerDelegate != null) {
            dest.setIsEnumType(this.mTigerDelegate.isEnum(src));
        }
        final Class s = src.getSuperclass();
        if (s != null) {
            dest.setSuperclass(s.getName());
        }
        final Class[] ints = src.getInterfaces();
        for (int i = 0; i < ints.length; ++i) {
            dest.addInterface(ints[i].getName());
        }
        Field[] fields = null;
        try {
            fields = src.getFields();
        }
        catch (final Exception ex) {}
        if (fields != null) {
            for (int j = 0; j < fields.length; ++j) {
                this.populate(dest.addNewField(), fields[j]);
            }
        }
        final Method[] methods = src.getDeclaredMethods();
        for (int k = 0; k < methods.length; ++k) {
            this.populate(dest.addNewMethod(), methods[k]);
        }
        if (this.mTigerDelegate != null) {
            this.mTigerDelegate.populateAnnotationTypeIfNecessary(src, dest, this);
        }
        final Constructor[] ctors = src.getDeclaredConstructors();
        for (int l = 0; l < ctors.length; ++l) {
            this.populate(dest.addNewConstructor(), ctors[l]);
        }
        if (this.mTigerDelegate != null) {
            this.mTigerDelegate.extractAnnotations(dest, src);
        }
        final Class[] inners = src.getDeclaredClasses();
        if (inners != null) {
            for (int m = 0; m < inners.length; ++m) {
                if (this.mTigerDelegate != null) {
                    if (this.mTigerDelegate.getEnclosingConstructor(inners[m]) != null) {
                        continue;
                    }
                    if (this.mTigerDelegate.getEnclosingMethod(inners[m]) != null) {
                        continue;
                    }
                }
                String simpleName = inners[m].getName();
                final int lastDollar = simpleName.lastIndexOf(36);
                simpleName = simpleName.substring(lastDollar + 1);
                final char first = simpleName.charAt(0);
                if ('0' > first || first > '9') {
                    final MClass inner = dest.addNewInnerClass(simpleName);
                    inner.setArtifact(inners[m]);
                    this.populate(inner);
                }
            }
        }
    }
    
    private void initDelegate(final ElementContext ctx) {
        this.mTigerDelegate = ReflectTigerDelegate.create(ctx);
    }
    
    private void populate(final MField dest, final Field src) {
        dest.setArtifact(src);
        dest.setSimpleName(src.getName());
        dest.setType(src.getType().getName());
        dest.setModifiers(src.getModifiers());
        if (this.mTigerDelegate != null) {
            this.mTigerDelegate.extractAnnotations(dest, src);
        }
    }
    
    private void populate(final MConstructor dest, final Constructor src) {
        dest.setArtifact(src);
        dest.setSimpleName(src.getName());
        dest.setModifiers(src.getModifiers());
        final Class[] exceptions = src.getExceptionTypes();
        this.addThrows(dest, exceptions);
        final Class[] paramTypes = src.getParameterTypes();
        for (int i = 0; i < paramTypes.length; ++i) {
            final MParameter p = this.addParameter(dest, i, paramTypes[i]);
            if (this.mTigerDelegate != null) {
                this.mTigerDelegate.extractAnnotations(p, src, i);
            }
        }
        if (this.mTigerDelegate != null) {
            this.mTigerDelegate.extractAnnotations(dest, src);
        }
    }
    
    private void populate(final MMethod dest, final Method src) {
        dest.setArtifact(src);
        dest.setSimpleName(src.getName());
        dest.setModifiers(src.getModifiers());
        dest.setReturnType(src.getReturnType().getName());
        final Class[] exceptions = src.getExceptionTypes();
        this.addThrows(dest, exceptions);
        final Class[] paramTypes = src.getParameterTypes();
        for (int i = 0; i < paramTypes.length; ++i) {
            final MParameter p = this.addParameter(dest, i, paramTypes[i]);
            if (this.mTigerDelegate != null) {
                this.mTigerDelegate.extractAnnotations(p, src, i);
            }
        }
        if (this.mTigerDelegate != null) {
            this.mTigerDelegate.extractAnnotations(dest, src);
        }
    }
    
    private void addThrows(final MInvokable dest, final Class[] exceptionTypes) {
        for (int i = 0; i < exceptionTypes.length; ++i) {
            dest.addException(exceptionTypes[i].getName());
        }
    }
    
    private MParameter addParameter(final MInvokable dest, final int paramNum, final Class paramType) {
        final MParameter p = dest.addNewParameter();
        p.setSimpleName("param" + paramNum);
        p.setType(paramType.getName());
        return p;
    }
}

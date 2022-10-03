package org.apache.xmlbeans.impl.jam.internal;

import java.util.Collections;
import java.util.Collection;
import java.lang.ref.WeakReference;
import org.apache.xmlbeans.impl.jam.internal.elements.VoidClassImpl;
import org.apache.xmlbeans.impl.jam.internal.elements.PrimitiveClassImpl;
import org.apache.xmlbeans.impl.jam.internal.elements.PackageImpl;
import org.apache.xmlbeans.impl.jam.JPackage;
import org.apache.xmlbeans.impl.jam.internal.elements.UnresolvedClassImpl;
import org.apache.xmlbeans.impl.jam.internal.elements.ClassImpl;
import org.apache.xmlbeans.impl.jam.internal.elements.ArrayClassImpl;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.visitor.TraversingMVisitor;
import java.util.HashMap;
import java.util.Stack;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.provider.JamClassBuilder;
import java.util.Map;
import org.apache.xmlbeans.impl.jam.JamClassLoader;

public class JamClassLoaderImpl implements JamClassLoader
{
    private Map mName2Package;
    private Map mFd2ClassCache;
    private JamClassBuilder mBuilder;
    private MVisitor mInitializer;
    private ElementContext mContext;
    private Stack mInitializeStack;
    private boolean mAlreadyInitializing;
    
    public JamClassLoaderImpl(final ElementContext context, final JamClassBuilder builder, final MVisitor initializerOrNull) {
        this.mName2Package = new HashMap();
        this.mFd2ClassCache = new HashMap();
        this.mInitializer = null;
        this.mInitializeStack = new Stack();
        this.mAlreadyInitializing = false;
        if (builder == null) {
            throw new IllegalArgumentException("null builder");
        }
        if (context == null) {
            throw new IllegalArgumentException("null builder");
        }
        this.mBuilder = builder;
        this.mInitializer = ((initializerOrNull == null) ? null : new TraversingMVisitor(initializerOrNull));
        this.mContext = context;
        this.initCache();
    }
    
    @Override
    public final JClass loadClass(String fd) {
        fd = fd.trim();
        JClass out = this.cacheGet(fd);
        if (out != null) {
            return out;
        }
        if (fd.indexOf(91) != -1) {
            final String normalFd = ArrayClassImpl.normalizeArrayName(fd);
            out = this.cacheGet(normalFd);
            if (out == null) {
                out = ArrayClassImpl.createClassForFD(normalFd, this);
                this.cachePut(out, normalFd);
            }
            this.cachePut(out, fd);
            return out;
        }
        final int dollar = fd.indexOf(36);
        if (dollar != -1) {
            final String outerName = fd.substring(0, dollar);
            ((ClassImpl)this.loadClass(outerName)).ensureLoaded();
            out = this.cacheGet(fd);
            final int dot = fd.lastIndexOf(46);
            if (out == null) {
                String pkg;
                String name;
                if (dot == -1) {
                    pkg = "";
                    name = fd;
                }
                else {
                    pkg = fd.substring(0, dot);
                    name = fd.substring(dot + 1);
                }
                out = new UnresolvedClassImpl(pkg, name, this.mContext);
                this.mContext.warning("failed to resolve class " + fd);
                this.cachePut(out);
            }
            return out;
        }
        final int dot2 = fd.lastIndexOf(46);
        String pkg2;
        String name2;
        if (dot2 == -1) {
            pkg2 = "";
            name2 = fd;
        }
        else {
            pkg2 = fd.substring(0, dot2);
            name2 = fd.substring(dot2 + 1);
        }
        out = this.mBuilder.build(pkg2, name2);
        if (out == null) {
            out = new UnresolvedClassImpl(pkg2, name2, this.mContext);
            this.mContext.warning("failed to resolve class " + fd);
            this.cachePut(out);
            return out;
        }
        this.cachePut(out);
        return out;
    }
    
    @Override
    public JPackage getPackage(final String named) {
        JPackage out = this.mName2Package.get(named);
        if (out == null) {
            out = new PackageImpl(this.mContext, named);
            this.mName2Package.put(named, out);
        }
        return out;
    }
    
    private void initCache() {
        PrimitiveClassImpl.mapNameToPrimitive(this.mContext, this.mFd2ClassCache);
        this.mFd2ClassCache.put("void", new VoidClassImpl(this.mContext));
    }
    
    private void cachePut(final JClass clazz) {
        this.mFd2ClassCache.put(clazz.getFieldDescriptor().trim(), new WeakReference(clazz));
    }
    
    private void cachePut(final JClass clazz, final String cachedName) {
        this.mFd2ClassCache.put(cachedName, new WeakReference(clazz));
    }
    
    private JClass cacheGet(final String fd) {
        Object out = this.mFd2ClassCache.get(fd.trim());
        if (out == null) {
            return null;
        }
        if (out instanceof JClass) {
            return (JClass)out;
        }
        if (!(out instanceof WeakReference)) {
            throw new IllegalStateException();
        }
        out = ((WeakReference)out).get();
        if (out == null) {
            this.mFd2ClassCache.remove(fd.trim());
            return null;
        }
        return (JClass)out;
    }
    
    public void initialize(final ClassImpl out) {
        if (this.mInitializer != null) {
            if (this.mAlreadyInitializing) {
                this.mInitializeStack.push(out);
            }
            else {
                out.accept(this.mInitializer);
                while (!this.mInitializeStack.isEmpty()) {
                    final ClassImpl initme = this.mInitializeStack.pop();
                    initme.accept(this.mInitializer);
                }
                this.mAlreadyInitializing = false;
            }
        }
    }
    
    public Collection getResolvedClasses() {
        return Collections.unmodifiableCollection(this.mFd2ClassCache.values());
    }
    
    public void addToCache(final JClass c) {
        this.cachePut(c);
    }
}

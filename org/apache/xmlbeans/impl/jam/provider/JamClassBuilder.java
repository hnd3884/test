package org.apache.xmlbeans.impl.jam.provider;

import org.apache.xmlbeans.impl.jam.internal.elements.ClassImpl;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;

public abstract class JamClassBuilder
{
    private ElementContext mContext;
    
    public JamClassBuilder() {
        this.mContext = null;
    }
    
    public void init(final ElementContext ctx) {
        if (this.mContext != null) {
            throw new IllegalStateException("init called more than once");
        }
        if (ctx == null) {
            throw new IllegalArgumentException("null ctx");
        }
        this.mContext = ctx;
    }
    
    public abstract MClass build(final String p0, final String p1);
    
    protected MClass createClassToBuild(final String packageName, String className, final String[] importSpecs, final JamClassPopulator pop) {
        if (this.mContext == null) {
            throw new IllegalStateException("init not called");
        }
        if (packageName == null) {
            throw new IllegalArgumentException("null pkg");
        }
        if (className == null) {
            throw new IllegalArgumentException("null class");
        }
        if (pop == null) {
            throw new IllegalArgumentException("null pop");
        }
        this.assertInitialized();
        className = className.replace('.', '$');
        final ClassImpl out = new ClassImpl(packageName, className, this.mContext, importSpecs, pop);
        return out;
    }
    
    protected MClass createClassToBuild(final String packageName, String className, final String[] importSpecs) {
        if (this.mContext == null) {
            throw new IllegalStateException("init not called");
        }
        if (packageName == null) {
            throw new IllegalArgumentException("null pkg");
        }
        if (className == null) {
            throw new IllegalArgumentException("null class");
        }
        this.assertInitialized();
        className = className.replace('.', '$');
        final ClassImpl out = new ClassImpl(packageName, className, this.mContext, importSpecs);
        return out;
    }
    
    protected JamLogger getLogger() {
        return this.mContext;
    }
    
    protected final void assertInitialized() {
        if (this.mContext == null) {
            throw new IllegalStateException(this + " not yet initialized.");
        }
    }
}

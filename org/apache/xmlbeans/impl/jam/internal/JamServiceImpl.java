package org.apache.xmlbeans.impl.jam.internal;

import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JamClassIterator;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.JamService;

public class JamServiceImpl implements JamService
{
    private ElementContext mContext;
    private String[] mClassNames;
    
    public JamServiceImpl(final ElementContext ctx, final String[] classes) {
        if (ctx == null) {
            throw new IllegalArgumentException("null jcl");
        }
        if (classes == null) {
            throw new IllegalArgumentException("null classes");
        }
        this.mContext = ctx;
        this.mClassNames = classes;
    }
    
    @Override
    public JamClassLoader getClassLoader() {
        return this.mContext.getClassLoader();
    }
    
    @Override
    public String[] getClassNames() {
        return this.mClassNames;
    }
    
    @Override
    public JamClassIterator getClasses() {
        return new JamClassIterator(this.getClassLoader(), this.getClassNames());
    }
    
    @Override
    public JClass[] getAllClasses() {
        final JClass[] out = new JClass[this.mClassNames.length];
        for (int i = 0; i < out.length; ++i) {
            out[i] = this.getClassLoader().loadClass(this.mClassNames[i]);
        }
        return out;
    }
    
    public void setClassNames(final String[] names) {
        this.mClassNames = names;
    }
}

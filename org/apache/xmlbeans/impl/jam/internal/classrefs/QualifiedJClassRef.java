package org.apache.xmlbeans.impl.jam.internal.classrefs;

import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JamClassLoader;

public class QualifiedJClassRef implements JClassRef
{
    private String mQualifiedClassname;
    private JamClassLoader mClassLoader;
    
    public static JClassRef create(final JClass clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("null clazz");
        }
        return new QualifiedJClassRef(clazz.getFieldDescriptor(), clazz.getClassLoader());
    }
    
    public static JClassRef create(final String qcname, final JClassRefContext ctx) {
        if (qcname == null) {
            throw new IllegalArgumentException("null qcname");
        }
        if (ctx == null) {
            throw new IllegalArgumentException("null ctx");
        }
        return create(qcname, ctx.getClassLoader());
    }
    
    public static JClassRef create(final String qcname, final JamClassLoader cl) {
        if (qcname == null) {
            throw new IllegalArgumentException("null qcname");
        }
        if (cl == null) {
            throw new IllegalArgumentException("null classloader");
        }
        return new QualifiedJClassRef(qcname, cl);
    }
    
    private QualifiedJClassRef(final String qcname, final JamClassLoader cl) {
        this.mClassLoader = cl;
        this.mQualifiedClassname = qcname;
    }
    
    @Override
    public JClass getRefClass() {
        return this.mClassLoader.loadClass(this.mQualifiedClassname);
    }
    
    @Override
    public String getQualifiedName() {
        return this.mQualifiedClassname;
    }
    
    @Override
    public String toString() {
        return "(QualifiedJClassRef '" + this.mQualifiedClassname + "')";
    }
}

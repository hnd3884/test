package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JPackage;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.impl.jam.mutable.MPackage;

public class PackageImpl extends AnnotatedElementImpl implements MPackage
{
    private List mRootClasses;
    private String mName;
    
    public PackageImpl(final ElementContext ctx, final String name) {
        super(ctx);
        this.mRootClasses = new ArrayList();
        this.mName = name;
        final int lastDot = this.mName.lastIndexOf(46);
        this.setSimpleName((lastDot == -1) ? this.mName : this.mName.substring(lastDot + 1));
    }
    
    @Override
    public String getQualifiedName() {
        return this.mName;
    }
    
    @Override
    public void accept(final MVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void accept(final JVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public JClass[] getClasses() {
        final JClass[] out = new JClass[this.mRootClasses.size()];
        this.mRootClasses.toArray(out);
        return out;
    }
    
    @Override
    public MClass[] getMutableClasses() {
        final MClass[] out = new MClass[this.mRootClasses.size()];
        this.mRootClasses.toArray(out);
        return out;
    }
}

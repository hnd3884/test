package org.apache.xmlbeans.impl.jam.internal.elements;

import java.io.StringWriter;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRef;
import org.apache.xmlbeans.impl.jam.JParameter;
import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import org.apache.xmlbeans.impl.jam.internal.classrefs.UnqualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRefContext;
import org.apache.xmlbeans.impl.jam.internal.classrefs.QualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.DirectJClassRef;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.jam.JClass;
import java.util.List;
import org.apache.xmlbeans.impl.jam.mutable.MInvokable;

public abstract class InvokableImpl extends MemberImpl implements MInvokable
{
    private List mExceptionClassRefs;
    private List mParameters;
    
    protected InvokableImpl(final ClassImpl containingClass) {
        super(containingClass);
        this.mExceptionClassRefs = null;
        this.mParameters = null;
    }
    
    @Override
    public void addException(final JClass exceptionClass) {
        if (exceptionClass == null) {
            throw new IllegalArgumentException("null exception class");
        }
        if (this.mExceptionClassRefs == null) {
            this.mExceptionClassRefs = new ArrayList();
        }
        this.mExceptionClassRefs.add(DirectJClassRef.create(exceptionClass));
    }
    
    @Override
    public void addException(final String qcname) {
        if (qcname == null) {
            throw new IllegalArgumentException("null qcname");
        }
        if (this.mExceptionClassRefs == null) {
            this.mExceptionClassRefs = new ArrayList();
        }
        this.mExceptionClassRefs.add(QualifiedJClassRef.create(qcname, (JClassRefContext)this.getContainingClass()));
    }
    
    public void addUnqualifiedException(final String ucname) {
        if (ucname == null) {
            throw new IllegalArgumentException("null qcname");
        }
        if (this.mExceptionClassRefs == null) {
            this.mExceptionClassRefs = new ArrayList();
        }
        this.mExceptionClassRefs.add(UnqualifiedJClassRef.create(ucname, (JClassRefContext)this.getContainingClass()));
    }
    
    @Override
    public void removeException(final String exceptionClassName) {
        if (exceptionClassName == null) {
            throw new IllegalArgumentException("null classname");
        }
        if (this.mExceptionClassRefs != null) {
            this.mExceptionClassRefs.remove(exceptionClassName);
        }
    }
    
    @Override
    public void removeException(final JClass exceptionClass) {
        this.removeException(exceptionClass.getQualifiedName());
    }
    
    @Override
    public MParameter addNewParameter() {
        if (this.mParameters == null) {
            this.mParameters = new ArrayList();
        }
        final MParameter param = new ParameterImpl(ElementImpl.defaultName(this.mParameters.size()), this, "java.lang.Object");
        this.mParameters.add(param);
        return param;
    }
    
    @Override
    public void removeParameter(final MParameter parameter) {
        if (this.mParameters != null) {
            this.mParameters.remove(parameter);
        }
    }
    
    @Override
    public MParameter[] getMutableParameters() {
        if (this.mParameters == null || this.mParameters.size() == 0) {
            return new MParameter[0];
        }
        final MParameter[] out = new MParameter[this.mParameters.size()];
        this.mParameters.toArray(out);
        return out;
    }
    
    @Override
    public JParameter[] getParameters() {
        return this.getMutableParameters();
    }
    
    @Override
    public JClass[] getExceptionTypes() {
        if (this.mExceptionClassRefs == null || this.mExceptionClassRefs.size() == 0) {
            return new JClass[0];
        }
        final JClass[] out = new JClass[this.mExceptionClassRefs.size()];
        for (int i = 0; i < out.length; ++i) {
            out[i] = this.mExceptionClassRefs.get(i).getRefClass();
        }
        return out;
    }
    
    @Override
    public String getQualifiedName() {
        final StringWriter out = new StringWriter();
        out.write(this.getContainingClass().getQualifiedName());
        out.write(46);
        out.write(this.getSimpleName());
        out.write(40);
        final JParameter[] params = this.getParameters();
        for (int i = 0; i < params.length; ++i) {
            out.write(params[i].getType().getQualifiedName());
            if (i < params.length - 1) {
                out.write(", ");
            }
        }
        out.write(41);
        return out.toString();
    }
    
    public void setUnqualifiedThrows(final List classnames) {
        if (classnames == null || classnames.size() == 0) {
            this.mExceptionClassRefs = null;
            return;
        }
        this.mExceptionClassRefs = new ArrayList(classnames.size());
        for (int i = 0; i < classnames.size(); ++i) {
            this.mExceptionClassRefs.add(UnqualifiedJClassRef.create(classnames.get(i), (JClassRefContext)this.getContainingClass()));
        }
    }
}

package org.apache.el.parser;

import javax.el.ValueReference;
import javax.el.MethodInfo;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

public interface Node
{
    void jjtOpen();
    
    void jjtClose();
    
    void jjtSetParent(final Node p0);
    
    Node jjtGetParent();
    
    void jjtAddChild(final Node p0, final int p1);
    
    Node jjtGetChild(final int p0);
    
    int jjtGetNumChildren();
    
    String getImage();
    
    Object getValue(final EvaluationContext p0) throws ELException;
    
    void setValue(final EvaluationContext p0, final Object p1) throws ELException;
    
    Class<?> getType(final EvaluationContext p0) throws ELException;
    
    boolean isReadOnly(final EvaluationContext p0) throws ELException;
    
    void accept(final NodeVisitor p0) throws Exception;
    
    MethodInfo getMethodInfo(final EvaluationContext p0, final Class<?>[] p1) throws ELException;
    
    Object invoke(final EvaluationContext p0, final Class<?>[] p1, final Object[] p2) throws ELException;
    
    ValueReference getValueReference(final EvaluationContext p0);
    
    boolean isParametersProvided();
}

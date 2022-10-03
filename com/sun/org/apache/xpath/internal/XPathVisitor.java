package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.patterns.UnionPattern;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.org.apache.xpath.internal.operations.UnaryOperation;
import com.sun.org.apache.xpath.internal.operations.Operation;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import com.sun.org.apache.xpath.internal.axes.UnionPathIterator;
import com.sun.org.apache.xpath.internal.axes.LocPathIterator;

public class XPathVisitor
{
    public boolean visitLocationPath(final ExpressionOwner owner, final LocPathIterator path) {
        return true;
    }
    
    public boolean visitUnionPath(final ExpressionOwner owner, final UnionPathIterator path) {
        return true;
    }
    
    public boolean visitStep(final ExpressionOwner owner, final NodeTest step) {
        return true;
    }
    
    public boolean visitPredicate(final ExpressionOwner owner, final Expression pred) {
        return true;
    }
    
    public boolean visitBinaryOperation(final ExpressionOwner owner, final Operation op) {
        return true;
    }
    
    public boolean visitUnaryOperation(final ExpressionOwner owner, final UnaryOperation op) {
        return true;
    }
    
    public boolean visitVariableRef(final ExpressionOwner owner, final Variable var) {
        return true;
    }
    
    public boolean visitFunction(final ExpressionOwner owner, final Function func) {
        return true;
    }
    
    public boolean visitMatchPattern(final ExpressionOwner owner, final StepPattern pattern) {
        return true;
    }
    
    public boolean visitUnionPattern(final ExpressionOwner owner, final UnionPattern pattern) {
        return true;
    }
    
    public boolean visitStringLiteral(final ExpressionOwner owner, final XString str) {
        return true;
    }
    
    public boolean visitNumberLiteral(final ExpressionOwner owner, final XNumber num) {
        return true;
    }
}

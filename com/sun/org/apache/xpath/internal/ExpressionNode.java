package com.sun.org.apache.xpath.internal;

import javax.xml.transform.SourceLocator;

public interface ExpressionNode extends SourceLocator
{
    void exprSetParent(final ExpressionNode p0);
    
    ExpressionNode exprGetParent();
    
    void exprAddChild(final ExpressionNode p0, final int p1);
    
    ExpressionNode exprGetChild(final int p0);
    
    int exprGetNumChildren();
}

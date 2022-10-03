package com.sun.org.apache.xpath.internal;

public interface ExpressionOwner
{
    Expression getExpression();
    
    void setExpression(final Expression p0);
}

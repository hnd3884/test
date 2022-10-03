package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.Stack;

public final class StringStack extends Stack
{
    static final long serialVersionUID = -1506910875640317898L;
    
    public String peekString() {
        return super.peek();
    }
    
    public String popString() {
        return super.pop();
    }
    
    public String pushString(final String val) {
        return super.push(val);
    }
}

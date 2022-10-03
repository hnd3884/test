package com.sun.org.apache.xalan.internal.xsltc.compiler;

final class ArgumentList
{
    private final Expression _arg;
    private final ArgumentList _rest;
    
    public ArgumentList(final Expression arg, final ArgumentList rest) {
        this._arg = arg;
        this._rest = rest;
    }
    
    @Override
    public String toString() {
        return (this._rest == null) ? this._arg.toString() : (this._arg.toString() + ", " + this._rest.toString());
    }
}

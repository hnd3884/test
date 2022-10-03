package com.sun.org.apache.xalan.internal.xsltc.compiler;

public final class CompilerException extends Exception
{
    static final long serialVersionUID = 1732939618562742663L;
    private String _msg;
    
    public CompilerException() {
    }
    
    public CompilerException(final Exception e) {
        super(e.toString());
        this._msg = e.toString();
    }
    
    public CompilerException(final String message) {
        super(message);
        this._msg = message;
    }
    
    @Override
    public String getMessage() {
        final int col = this._msg.indexOf(58);
        if (col > -1) {
            return this._msg.substring(col);
        }
        return this._msg;
    }
}

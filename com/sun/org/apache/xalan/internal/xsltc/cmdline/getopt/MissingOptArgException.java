package com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt;

class MissingOptArgException extends GetOptsException
{
    static final long serialVersionUID = -1972471465394544822L;
    
    public MissingOptArgException(final String msg) {
        super(msg);
    }
}

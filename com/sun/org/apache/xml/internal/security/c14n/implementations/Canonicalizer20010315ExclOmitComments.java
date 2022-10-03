package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer20010315ExclOmitComments extends Canonicalizer20010315Excl
{
    public Canonicalizer20010315ExclOmitComments() {
        super(false);
    }
    
    @Override
    public final String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#";
    }
    
    @Override
    public final boolean engineGetIncludeComments() {
        return false;
    }
}

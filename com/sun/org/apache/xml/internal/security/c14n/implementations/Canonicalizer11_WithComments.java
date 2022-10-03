package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer11_WithComments extends Canonicalizer20010315
{
    public Canonicalizer11_WithComments() {
        super(true, true);
    }
    
    @Override
    public final String engineGetURI() {
        return "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    }
    
    @Override
    public final boolean engineGetIncludeComments() {
        return true;
    }
}

package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer20010315OmitComments extends Canonicalizer20010315
{
    public Canonicalizer20010315OmitComments() {
        super(false);
    }
    
    @Override
    public final String engineGetURI() {
        return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    }
    
    @Override
    public final boolean engineGetIncludeComments() {
        return false;
    }
}

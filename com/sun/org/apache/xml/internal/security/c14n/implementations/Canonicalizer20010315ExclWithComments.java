package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer20010315ExclWithComments extends Canonicalizer20010315Excl
{
    public Canonicalizer20010315ExclWithComments() {
        super(true);
    }
    
    @Override
    public final String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    }
    
    @Override
    public final boolean engineGetIncludeComments() {
        return true;
    }
}

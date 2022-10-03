package org.apache.xmlbeans.impl.soap;

public interface SOAPHeaderElement extends SOAPElement
{
    void setActor(final String p0);
    
    String getActor();
    
    void setMustUnderstand(final boolean p0);
    
    boolean getMustUnderstand();
}

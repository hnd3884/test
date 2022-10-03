package org.apache.axiom.ext.stax;

public interface DTDReader
{
    public static final String PROPERTY = DTDReader.class.getName();
    
    String getRootName();
    
    String getPublicId();
    
    String getSystemId();
}

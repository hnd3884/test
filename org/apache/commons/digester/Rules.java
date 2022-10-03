package org.apache.commons.digester;

import java.util.List;

public interface Rules
{
    Digester getDigester();
    
    void setDigester(final Digester p0);
    
    String getNamespaceURI();
    
    void setNamespaceURI(final String p0);
    
    void add(final String p0, final Rule p1);
    
    void clear();
    
    List match(final String p0);
    
    List match(final String p0, final String p1);
    
    List rules();
}

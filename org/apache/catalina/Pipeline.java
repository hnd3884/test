package org.apache.catalina;

import java.util.Set;

public interface Pipeline extends Contained
{
    Valve getBasic();
    
    void setBasic(final Valve p0);
    
    void addValve(final Valve p0);
    
    Valve[] getValves();
    
    void removeValve(final Valve p0);
    
    Valve getFirst();
    
    boolean isAsyncSupported();
    
    void findNonAsyncValves(final Set<String> p0);
}

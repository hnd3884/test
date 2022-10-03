package org.apache.tomcat.util.descriptor.web;

import java.util.List;

public interface Injectable
{
    String getName();
    
    void addInjectionTarget(final String p0, final String p1);
    
    List<InjectionTarget> getInjectionTargets();
}

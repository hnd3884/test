package com.adventnet.tools.update.installer;

import java.util.EventListener;

public interface ParameterChangeListener extends EventListener
{
    void parameterChanged(final ParameterObject p0);
    
    void setParameterObject(final ParameterObject p0);
    
    void destroy();
}

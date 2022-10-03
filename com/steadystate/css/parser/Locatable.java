package com.steadystate.css.parser;

import org.w3c.css.sac.Locator;

public interface Locatable
{
    Locator getLocator();
    
    void setLocator(final Locator p0);
}

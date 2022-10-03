package com.steadystate.css.parser;

import org.w3c.css.sac.Locator;

public class LocatableImpl implements Locatable
{
    private Locator locator_;
    
    public Locator getLocator() {
        return this.locator_;
    }
    
    public void setLocator(final Locator locator) {
        this.locator_ = locator;
    }
}

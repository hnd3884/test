package com.steadystate.css.parser;

import org.w3c.css.sac.Parser;

public interface SACParser extends Parser
{
    void setIeStarHackAccepted(final boolean p0);
    
    boolean isIeStarHackAccepted();
}

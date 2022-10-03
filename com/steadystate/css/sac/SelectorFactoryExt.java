package com.steadystate.css.sac;

import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.SelectorFactory;

public interface SelectorFactoryExt extends SelectorFactory
{
    ElementSelector createElementSelector(final String p0, final String p1, final Locator p2) throws CSSException;
    
    ElementSelector createPseudoElementSelector(final String p0, final String p1, final Locator p2, final boolean p3) throws CSSException;
    
    ElementSelector createSyntheticElementSelector() throws CSSException;
    
    SiblingSelector createGeneralAdjacentSelector(final short p0, final Selector p1, final SimpleSelector p2) throws CSSException;
}

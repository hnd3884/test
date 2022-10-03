package com.steadystate.css.sac;

import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.ConditionFactory;

public interface ConditionFactoryExt extends ConditionFactory
{
    AttributeCondition createClassCondition(final String p0, final String p1, final Locator p2) throws CSSException;
    
    AttributeCondition createIdCondition(final String p0, final Locator p1) throws CSSException;
    
    AttributeCondition createPseudoClassCondition(final String p0, final String p1, final Locator p2, final boolean p3) throws CSSException;
    
    LangCondition createLangCondition(final String p0, final Locator p1) throws CSSException;
    
    AttributeCondition createPrefixAttributeCondition(final String p0, final String p1, final boolean p2, final String p3) throws CSSException;
    
    AttributeCondition createSuffixAttributeCondition(final String p0, final String p1, final boolean p2, final String p3) throws CSSException;
    
    AttributeCondition createSubstringAttributeCondition(final String p0, final String p1, final boolean p2, final String p3) throws CSSException;
}

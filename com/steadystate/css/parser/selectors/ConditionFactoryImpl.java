package com.steadystate.css.parser.selectors;

import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.PositionalCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import com.steadystate.css.sac.ConditionFactoryExt;

public class ConditionFactoryImpl implements ConditionFactoryExt
{
    public CombinatorCondition createAndCondition(final Condition first, final Condition second) throws CSSException {
        return (CombinatorCondition)new AndConditionImpl(first, second);
    }
    
    public CombinatorCondition createOrCondition(final Condition first, final Condition second) throws CSSException {
        throw new CSSException((short)1);
    }
    
    public NegativeCondition createNegativeCondition(final Condition condition) throws CSSException {
        throw new CSSException((short)1);
    }
    
    public PositionalCondition createPositionalCondition(final int position, final boolean typeNode, final boolean type) throws CSSException {
        throw new CSSException((short)1);
    }
    
    public AttributeCondition createAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return (AttributeCondition)new AttributeConditionImpl(localName, value, specified);
    }
    
    public AttributeCondition createIdCondition(final String value) throws CSSException {
        return this.createIdCondition(value, null);
    }
    
    public AttributeCondition createIdCondition(final String value, final Locator locator) throws CSSException {
        final IdConditionImpl cond = new IdConditionImpl(value);
        cond.setLocator(locator);
        return (AttributeCondition)cond;
    }
    
    public LangCondition createLangCondition(final String lang) throws CSSException {
        return this.createLangCondition(lang, null);
    }
    
    public LangCondition createLangCondition(final String lang, final Locator locator) throws CSSException {
        final LangConditionImpl cond = new LangConditionImpl(lang);
        cond.setLocator(locator);
        return (LangCondition)cond;
    }
    
    public AttributeCondition createOneOfAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return (AttributeCondition)new OneOfAttributeConditionImpl(localName, value, specified);
    }
    
    public AttributeCondition createBeginHyphenAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return (AttributeCondition)new BeginHyphenAttributeConditionImpl(localName, value, specified);
    }
    
    public AttributeCondition createPrefixAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return (AttributeCondition)new PrefixAttributeConditionImpl(localName, value, specified);
    }
    
    public AttributeCondition createSuffixAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return (AttributeCondition)new SuffixAttributeConditionImpl(localName, value, specified);
    }
    
    public AttributeCondition createSubstringAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return (AttributeCondition)new SubstringAttributeConditionImpl(localName, value, specified);
    }
    
    public AttributeCondition createClassCondition(final String namespaceURI, final String value) throws CSSException {
        return this.createClassCondition(namespaceURI, value, null);
    }
    
    public AttributeCondition createClassCondition(final String namespaceURI, final String value, final Locator locator) throws CSSException {
        final ClassConditionImpl cond = new ClassConditionImpl(value);
        cond.setLocator(locator);
        return (AttributeCondition)cond;
    }
    
    public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value) throws CSSException {
        return this.createPseudoClassCondition(namespaceURI, value, null, false);
    }
    
    public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value, final Locator locator, final boolean doubleColon) throws CSSException {
        final PseudoClassConditionImpl cond = new PseudoClassConditionImpl(value);
        cond.setLocator(locator);
        if (doubleColon) {
            cond.prefixedWithDoubleColon();
        }
        return (AttributeCondition)cond;
    }
    
    public Condition createOnlyChildCondition() throws CSSException {
        throw new CSSException((short)1);
    }
    
    public Condition createOnlyTypeCondition() throws CSSException {
        throw new CSSException((short)1);
    }
    
    public ContentCondition createContentCondition(final String data) throws CSSException {
        throw new CSSException((short)1);
    }
}

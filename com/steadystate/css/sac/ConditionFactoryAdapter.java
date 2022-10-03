package com.steadystate.css.sac;

import com.steadystate.css.parser.selectors.SubstringAttributeConditionImpl;
import com.steadystate.css.parser.selectors.SuffixAttributeConditionImpl;
import com.steadystate.css.parser.selectors.PrefixAttributeConditionImpl;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.PositionalCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;

public class ConditionFactoryAdapter implements ConditionFactoryExt
{
    private final ConditionFactory conditionFactory_;
    
    public ConditionFactoryAdapter(final ConditionFactory selectorFactory) {
        this.conditionFactory_ = selectorFactory;
    }
    
    public CombinatorCondition createAndCondition(final Condition first, final Condition second) throws CSSException {
        return this.conditionFactory_.createAndCondition(first, second);
    }
    
    public CombinatorCondition createOrCondition(final Condition first, final Condition second) throws CSSException {
        return this.conditionFactory_.createOrCondition(first, second);
    }
    
    public NegativeCondition createNegativeCondition(final Condition condition) throws CSSException {
        return this.conditionFactory_.createNegativeCondition(condition);
    }
    
    public PositionalCondition createPositionalCondition(final int position, final boolean typeNode, final boolean type) throws CSSException {
        return this.conditionFactory_.createPositionalCondition(position, typeNode, type);
    }
    
    public AttributeCondition createAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return this.conditionFactory_.createAttributeCondition(localName, namespaceURI, specified, value);
    }
    
    public AttributeCondition createIdCondition(final String value) throws CSSException {
        return this.conditionFactory_.createIdCondition(value);
    }
    
    public AttributeCondition createIdCondition(final String value, final Locator locator) throws CSSException {
        return this.conditionFactory_.createIdCondition(value);
    }
    
    public LangCondition createLangCondition(final String lang) throws CSSException {
        return this.conditionFactory_.createLangCondition(lang);
    }
    
    public LangCondition createLangCondition(final String lang, final Locator locator) throws CSSException {
        return this.conditionFactory_.createLangCondition(lang);
    }
    
    public AttributeCondition createOneOfAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return this.conditionFactory_.createOneOfAttributeCondition(localName, namespaceURI, specified, value);
    }
    
    public AttributeCondition createBeginHyphenAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return this.conditionFactory_.createBeginHyphenAttributeCondition(localName, namespaceURI, specified, value);
    }
    
    public AttributeCondition createClassCondition(final String namespaceURI, final String value) throws CSSException {
        return this.conditionFactory_.createClassCondition(namespaceURI, value);
    }
    
    public AttributeCondition createClassCondition(final String namespaceURI, final String value, final Locator locator) throws CSSException {
        return this.conditionFactory_.createClassCondition(namespaceURI, value);
    }
    
    public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value) throws CSSException {
        return this.conditionFactory_.createPseudoClassCondition(namespaceURI, value);
    }
    
    public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value, final Locator locator, final boolean doubleColon) throws CSSException {
        return this.conditionFactory_.createPseudoClassCondition(namespaceURI, value);
    }
    
    public Condition createOnlyChildCondition() throws CSSException {
        return this.conditionFactory_.createOnlyChildCondition();
    }
    
    public Condition createOnlyTypeCondition() throws CSSException {
        return this.conditionFactory_.createOnlyTypeCondition();
    }
    
    public ContentCondition createContentCondition(final String data) throws CSSException {
        return this.conditionFactory_.createContentCondition(data);
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
}

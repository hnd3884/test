package com.steadystate.css.sac;

import org.w3c.css.sac.Locator;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.SelectorFactory;

public class SelectorFactoryAdapter implements SelectorFactoryExt
{
    private final SelectorFactory selectorFactory_;
    
    public SelectorFactoryAdapter(final SelectorFactory selectorFactory) {
        this.selectorFactory_ = selectorFactory;
    }
    
    public ConditionalSelector createConditionalSelector(final SimpleSelector selector, final Condition condition) throws CSSException {
        return this.selectorFactory_.createConditionalSelector(selector, condition);
    }
    
    public SimpleSelector createAnyNodeSelector() throws CSSException {
        return this.selectorFactory_.createAnyNodeSelector();
    }
    
    public SimpleSelector createRootNodeSelector() throws CSSException {
        return this.selectorFactory_.createRootNodeSelector();
    }
    
    public NegativeSelector createNegativeSelector(final SimpleSelector selector) throws CSSException {
        return this.selectorFactory_.createNegativeSelector(selector);
    }
    
    public ElementSelector createElementSelector(final String namespaceURI, final String tagName) throws CSSException {
        return this.selectorFactory_.createElementSelector(namespaceURI, tagName);
    }
    
    public CharacterDataSelector createTextNodeSelector(final String data) throws CSSException {
        return this.selectorFactory_.createTextNodeSelector(data);
    }
    
    public CharacterDataSelector createCDataSectionSelector(final String data) throws CSSException {
        return this.selectorFactory_.createCDataSectionSelector(data);
    }
    
    public ProcessingInstructionSelector createProcessingInstructionSelector(final String target, final String data) throws CSSException {
        return this.selectorFactory_.createProcessingInstructionSelector(target, data);
    }
    
    public CharacterDataSelector createCommentSelector(final String data) throws CSSException {
        return this.selectorFactory_.createCommentSelector(data);
    }
    
    public ElementSelector createPseudoElementSelector(final String namespaceURI, final String pseudoName) throws CSSException {
        return this.selectorFactory_.createPseudoElementSelector(namespaceURI, pseudoName);
    }
    
    public DescendantSelector createDescendantSelector(final Selector parent, final SimpleSelector descendant) throws CSSException {
        return this.selectorFactory_.createDescendantSelector(parent, descendant);
    }
    
    public DescendantSelector createChildSelector(final Selector parent, final SimpleSelector child) throws CSSException {
        return this.selectorFactory_.createChildSelector(parent, child);
    }
    
    public SiblingSelector createDirectAdjacentSelector(final short nodeType, final Selector child, final SimpleSelector directAdjacent) throws CSSException {
        return this.selectorFactory_.createDirectAdjacentSelector(nodeType, child, directAdjacent);
    }
    
    public ElementSelector createElementSelector(final String namespaceURI, final String tagName, final Locator locator) throws CSSException {
        return this.selectorFactory_.createElementSelector(namespaceURI, tagName);
    }
    
    public ElementSelector createPseudoElementSelector(final String namespaceURI, final String pseudoName, final Locator locator, final boolean doubleColon) throws CSSException {
        return this.selectorFactory_.createPseudoElementSelector(namespaceURI, pseudoName);
    }
    
    public ElementSelector createSyntheticElementSelector() throws CSSException {
        return null;
    }
    
    public SiblingSelector createGeneralAdjacentSelector(final short nodeType, final Selector child, final SimpleSelector directAdjacent) throws CSSException {
        return null;
    }
}

package com.steadystate.css.parser.selectors;

import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.SimpleSelector;
import com.steadystate.css.sac.SelectorFactoryExt;

public class SelectorFactoryImpl implements SelectorFactoryExt
{
    public ConditionalSelector createConditionalSelector(final SimpleSelector selector, final Condition condition) throws CSSException {
        return (ConditionalSelector)new ConditionalSelectorImpl(selector, condition);
    }
    
    public SimpleSelector createAnyNodeSelector() throws CSSException {
        throw new CSSException((short)1);
    }
    
    public SimpleSelector createRootNodeSelector() throws CSSException {
        throw new CSSException((short)1);
    }
    
    public NegativeSelector createNegativeSelector(final SimpleSelector selector) throws CSSException {
        throw new CSSException((short)1);
    }
    
    public ElementSelector createElementSelector(final String namespaceURI, final String localName) throws CSSException {
        return this.createElementSelector(namespaceURI, localName, null);
    }
    
    public ElementSelector createElementSelector(final String namespaceURI, final String localName, final Locator locator) throws CSSException {
        if (namespaceURI != null) {
            throw new CSSException((short)1);
        }
        final ElementSelectorImpl sel = new ElementSelectorImpl(localName);
        sel.setLocator(locator);
        return (ElementSelector)sel;
    }
    
    public ElementSelector createSyntheticElementSelector() throws CSSException {
        return (ElementSelector)new SyntheticElementSelectorImpl();
    }
    
    public CharacterDataSelector createTextNodeSelector(final String data) throws CSSException {
        throw new CSSException((short)1);
    }
    
    public CharacterDataSelector createCDataSectionSelector(final String data) throws CSSException {
        throw new CSSException((short)1);
    }
    
    public ProcessingInstructionSelector createProcessingInstructionSelector(final String target, final String data) throws CSSException {
        throw new CSSException((short)1);
    }
    
    public CharacterDataSelector createCommentSelector(final String data) throws CSSException {
        throw new CSSException((short)1);
    }
    
    public ElementSelector createPseudoElementSelector(final String namespaceURI, final String pseudoName) throws CSSException {
        return this.createPseudoElementSelector(namespaceURI, pseudoName, null, false);
    }
    
    public ElementSelector createPseudoElementSelector(final String namespaceURI, final String pseudoName, final Locator locator, final boolean doubleColon) throws CSSException {
        if (namespaceURI != null) {
            throw new CSSException((short)1);
        }
        final PseudoElementSelectorImpl sel = new PseudoElementSelectorImpl(pseudoName);
        sel.setLocator(locator);
        if (doubleColon) {
            sel.prefixedWithDoubleColon();
        }
        return (ElementSelector)sel;
    }
    
    public DescendantSelector createDescendantSelector(final Selector parent, final SimpleSelector descendant) throws CSSException {
        return (DescendantSelector)new DescendantSelectorImpl(parent, descendant);
    }
    
    public DescendantSelector createChildSelector(final Selector parent, final SimpleSelector child) throws CSSException {
        return (DescendantSelector)new ChildSelectorImpl(parent, child);
    }
    
    public SiblingSelector createDirectAdjacentSelector(final short nodeType, final Selector child, final SimpleSelector directAdjacent) throws CSSException {
        return (SiblingSelector)new DirectAdjacentSelectorImpl(nodeType, child, directAdjacent);
    }
    
    public SiblingSelector createGeneralAdjacentSelector(final short nodeType, final Selector child, final SimpleSelector directAdjacent) throws CSSException {
        return (SiblingSelector)new GeneralAdjacentSelectorImpl(nodeType, child, directAdjacent);
    }
}

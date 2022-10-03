package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import org.w3c.css.sac.Locator;
import com.steadystate.css.parser.Locatable;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.SiblingSelector;
import com.steadystate.css.parser.LocatableImpl;

public class DirectAdjacentSelectorImpl extends LocatableImpl implements SiblingSelector, CSSFormatable, Serializable
{
    private static final long serialVersionUID = -7328602345833826516L;
    private short nodeType_;
    private Selector selector_;
    private SimpleSelector siblingSelector_;
    
    public void setNodeType(final short nodeType) {
        this.nodeType_ = nodeType;
    }
    
    public void setSelector(final Selector child) {
        this.selector_ = child;
        if (child instanceof Locatable) {
            this.setLocator(((Locatable)child).getLocator());
        }
        else if (child == null) {
            this.setLocator(null);
        }
    }
    
    public void setSiblingSelector(final SimpleSelector directAdjacent) {
        this.siblingSelector_ = directAdjacent;
    }
    
    public DirectAdjacentSelectorImpl(final short nodeType, final Selector child, final SimpleSelector directAdjacent) {
        this.setNodeType(nodeType);
        this.setSelector(child);
        this.setSiblingSelector(directAdjacent);
    }
    
    public short getNodeType() {
        return this.nodeType_;
    }
    
    public short getSelectorType() {
        return 12;
    }
    
    public Selector getSelector() {
        return this.selector_;
    }
    
    public SimpleSelector getSiblingSelector() {
        return this.siblingSelector_;
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        if (null != this.selector_) {
            sb.append(((CSSFormatable)this.selector_).getCssText(format));
        }
        sb.append(" + ");
        if (null != this.siblingSelector_) {
            sb.append(((CSSFormatable)this.siblingSelector_).getCssText(format));
        }
        return sb.toString();
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}

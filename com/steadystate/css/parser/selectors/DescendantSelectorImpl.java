package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import org.w3c.css.sac.Locator;
import com.steadystate.css.parser.Locatable;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Selector;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.DescendantSelector;
import com.steadystate.css.parser.LocatableImpl;

public class DescendantSelectorImpl extends LocatableImpl implements DescendantSelector, CSSFormatable, Serializable
{
    private static final long serialVersionUID = -3620467847449531232L;
    private Selector ancestorSelector_;
    private SimpleSelector simpleSelector_;
    
    public void setAncestorSelector(final Selector ancestorSelector) {
        this.ancestorSelector_ = ancestorSelector;
        if (ancestorSelector instanceof Locatable) {
            this.setLocator(((Locatable)ancestorSelector).getLocator());
        }
        else if (ancestorSelector == null) {
            this.setLocator(null);
        }
    }
    
    public void setSimpleSelector(final SimpleSelector simpleSelector) {
        this.simpleSelector_ = simpleSelector;
    }
    
    public DescendantSelectorImpl(final Selector parent, final SimpleSelector simpleSelector) {
        this.setAncestorSelector(parent);
        this.setSimpleSelector(simpleSelector);
    }
    
    public short getSelectorType() {
        return 10;
    }
    
    public Selector getAncestorSelector() {
        return this.ancestorSelector_;
    }
    
    public SimpleSelector getSimpleSelector() {
        return this.simpleSelector_;
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        if (null != this.ancestorSelector_) {
            sb.append(((CSSFormatable)this.ancestorSelector_).getCssText(format));
        }
        if (9 != this.getSimpleSelector().getSelectorType()) {
            sb.append(' ');
        }
        if (null != this.simpleSelector_) {
            sb.append(((CSSFormatable)this.simpleSelector_).getCssText(format));
        }
        return sb.toString();
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}

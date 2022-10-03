package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import org.w3c.css.sac.Locator;
import com.steadystate.css.parser.Locatable;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.SimpleSelector;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.ConditionalSelector;
import com.steadystate.css.parser.LocatableImpl;

public class ConditionalSelectorImpl extends LocatableImpl implements ConditionalSelector, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 7217145899707580586L;
    private SimpleSelector simpleSelector_;
    private Condition condition_;
    
    public void setSimpleSelector(final SimpleSelector simpleSelector) {
        this.simpleSelector_ = simpleSelector;
        if (simpleSelector instanceof Locatable) {
            this.setLocator(((Locatable)simpleSelector).getLocator());
        }
        else if (simpleSelector == null) {
            this.setLocator(null);
        }
    }
    
    public void setCondition(final Condition condition) {
        this.condition_ = condition;
        if (this.getLocator() == null) {
            if (condition instanceof Locatable) {
                this.setLocator(((Locatable)condition).getLocator());
            }
            else if (condition == null) {
                this.setLocator(null);
            }
        }
    }
    
    public ConditionalSelectorImpl(final SimpleSelector simpleSelector, final Condition condition) {
        this.setSimpleSelector(simpleSelector);
        this.setCondition(condition);
    }
    
    public short getSelectorType() {
        return 0;
    }
    
    public SimpleSelector getSimpleSelector() {
        return this.simpleSelector_;
    }
    
    public Condition getCondition() {
        return this.condition_;
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        if (null != this.simpleSelector_) {
            sb.append(((CSSFormatable)this.simpleSelector_).getCssText(format));
        }
        if (null != this.condition_) {
            sb.append(((CSSFormatable)this.condition_).getCssText(format));
        }
        return sb.toString();
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}

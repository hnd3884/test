package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import org.w3c.css.sac.Locator;
import com.steadystate.css.parser.Locatable;
import org.w3c.css.sac.Condition;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.CombinatorCondition;
import com.steadystate.css.parser.LocatableImpl;

public class AndConditionImpl extends LocatableImpl implements CombinatorCondition, CSSFormatable, Serializable
{
    private static final long serialVersionUID = -3180583860092672742L;
    private Condition firstCondition_;
    private Condition secondCondition_;
    
    public void setFirstCondition(final Condition c1) {
        this.firstCondition_ = c1;
        if (c1 instanceof Locatable) {
            this.setLocator(((Locatable)c1).getLocator());
        }
        else if (c1 == null) {
            this.setLocator(null);
        }
    }
    
    public void setSecondCondition(final Condition c2) {
        this.secondCondition_ = c2;
    }
    
    public AndConditionImpl(final Condition c1, final Condition c2) {
        this.setFirstCondition(c1);
        this.setSecondCondition(c2);
    }
    
    public short getConditionType() {
        return 0;
    }
    
    public Condition getFirstCondition() {
        return this.firstCondition_;
    }
    
    public Condition getSecondCondition() {
        return this.secondCondition_;
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        Condition cond = this.getFirstCondition();
        if (null != cond) {
            sb.append(((CSSFormatable)cond).getCssText(format));
        }
        cond = this.getSecondCondition();
        if (null != cond) {
            sb.append(((CSSFormatable)cond).getCssText(format));
        }
        return sb.toString();
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}

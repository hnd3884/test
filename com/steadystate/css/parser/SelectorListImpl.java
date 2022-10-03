package com.steadystate.css.parser;

import com.steadystate.css.format.CSSFormat;
import java.util.ArrayList;
import org.w3c.css.sac.Selector;
import java.util.List;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.SelectorList;

public class SelectorListImpl extends LocatableImpl implements SelectorList, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 7313376916207026333L;
    private List<Selector> selectors_;
    
    public SelectorListImpl() {
        this.selectors_ = new ArrayList<Selector>(10);
    }
    
    public List<Selector> getSelectors() {
        return this.selectors_;
    }
    
    public void setSelectors(final List<Selector> selectors) {
        this.selectors_ = selectors;
    }
    
    public int getLength() {
        return this.selectors_.size();
    }
    
    public Selector item(final int index) {
        return this.selectors_.get(index);
    }
    
    public void add(final Selector sel) {
        this.selectors_.add(sel);
    }
    
    public String getCssText(final CSSFormat format) {
        final int len = this.getLength();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            final CSSFormatable sel = (CSSFormatable)this.item(i);
            sb.append(sel.getCssText(format));
            if (i < len - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}

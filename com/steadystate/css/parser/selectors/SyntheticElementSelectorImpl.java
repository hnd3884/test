package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;

public class SyntheticElementSelectorImpl extends ElementSelectorImpl
{
    private static final long serialVersionUID = 3426191759125755798L;
    
    public SyntheticElementSelectorImpl() {
        super(null);
    }
    
    @Override
    public void setLocalName(final String localName) {
        throw new RuntimeException("Method setLocalName is not supported for SyntheticElementSelectorImpl.");
    }
    
    @Override
    public String getCssText(final CSSFormat format) {
        return "";
    }
}

package com.oracle.webservices.internal.api;

import javax.xml.ws.WebServiceFeature;

public class EnvelopeStyleFeature extends WebServiceFeature
{
    private EnvelopeStyle.Style[] styles;
    
    public EnvelopeStyleFeature(final EnvelopeStyle.Style... s) {
        this.styles = s;
    }
    
    public EnvelopeStyle.Style[] getStyles() {
        return this.styles;
    }
    
    @Override
    public String getID() {
        return EnvelopeStyleFeature.class.getName();
    }
}

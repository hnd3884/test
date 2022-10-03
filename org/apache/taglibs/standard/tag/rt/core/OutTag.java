package org.apache.taglibs.standard.tag.rt.core;

import org.apache.taglibs.standard.tag.common.core.OutSupport;

public class OutTag extends OutSupport
{
    private Object value;
    private String def;
    private boolean escapeXml;
    
    public OutTag() {
        this.escapeXml = true;
    }
    
    @Override
    public void release() {
        this.value = null;
        this.def = null;
        this.escapeXml = false;
        super.release();
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    public void setDefault(final String def) {
        this.def = def;
    }
    
    public void setEscapeXml(final boolean escapeXml) {
        this.escapeXml = escapeXml;
    }
    
    @Override
    protected Object evalValue() {
        return this.value;
    }
    
    @Override
    protected String evalDefault() {
        return this.def;
    }
    
    @Override
    protected boolean evalEscapeXml() {
        return this.escapeXml;
    }
}

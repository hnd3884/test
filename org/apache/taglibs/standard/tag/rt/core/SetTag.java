package org.apache.taglibs.standard.tag.rt.core;

import org.apache.taglibs.standard.tag.common.core.SetSupport;

public class SetTag extends SetSupport
{
    private boolean valueSpecified;
    private Object value;
    private Object target;
    private String property;
    
    public void setValue(final Object value) {
        this.value = value;
        this.valueSpecified = true;
    }
    
    public void setTarget(final Object target) {
        this.target = target;
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
    
    @Override
    public void release() {
        this.value = null;
        this.target = null;
        this.property = null;
        this.valueSpecified = false;
        super.release();
    }
    
    @Override
    protected boolean isValueSpecified() {
        return this.valueSpecified;
    }
    
    @Override
    protected Object evalValue() {
        return this.value;
    }
    
    @Override
    protected Object evalTarget() {
        return this.target;
    }
    
    @Override
    protected String evalProperty() {
        return this.property;
    }
}

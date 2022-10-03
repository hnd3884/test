package com.sun.media.sound;

public final class ModelIdentifier
{
    private String object;
    private String variable;
    private int instance;
    
    public ModelIdentifier(final String object) {
        this.object = null;
        this.variable = null;
        this.instance = 0;
        this.object = object;
    }
    
    public ModelIdentifier(final String object, final int instance) {
        this.object = null;
        this.variable = null;
        this.instance = 0;
        this.object = object;
        this.instance = instance;
    }
    
    public ModelIdentifier(final String object, final String variable) {
        this.object = null;
        this.variable = null;
        this.instance = 0;
        this.object = object;
        this.variable = variable;
    }
    
    public ModelIdentifier(final String object, final String variable, final int instance) {
        this.object = null;
        this.variable = null;
        this.instance = 0;
        this.object = object;
        this.variable = variable;
        this.instance = instance;
    }
    
    public int getInstance() {
        return this.instance;
    }
    
    public void setInstance(final int instance) {
        this.instance = instance;
    }
    
    public String getObject() {
        return this.object;
    }
    
    public void setObject(final String object) {
        this.object = object;
    }
    
    public String getVariable() {
        return this.variable;
    }
    
    public void setVariable(final String variable) {
        this.variable = variable;
    }
    
    @Override
    public int hashCode() {
        int instance = this.instance;
        if (this.object != null) {
            instance |= this.object.hashCode();
        }
        if (this.variable != null) {
            instance |= this.variable.hashCode();
        }
        return instance;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ModelIdentifier)) {
            return false;
        }
        final ModelIdentifier modelIdentifier = (ModelIdentifier)o;
        return this.object == null == (modelIdentifier.object == null) && this.variable == null == (modelIdentifier.variable == null) && modelIdentifier.getInstance() == this.getInstance() && (this.object == null || this.object.equals(modelIdentifier.object)) && (this.variable == null || this.variable.equals(modelIdentifier.variable));
    }
    
    @Override
    public String toString() {
        if (this.variable == null) {
            return this.object + "[" + this.instance + "]";
        }
        return this.object + "[" + this.instance + "]." + this.variable;
    }
}

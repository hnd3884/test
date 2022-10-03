package com.sun.org.apache.xerces.internal.util;

public final class SecurityManager
{
    private static final int DEFAULT_ENTITY_EXPANSION_LIMIT = 64000;
    private static final int DEFAULT_MAX_OCCUR_NODE_LIMIT = 5000;
    private static final int DEFAULT_ELEMENT_ATTRIBUTE_LIMIT = 10000;
    private int entityExpansionLimit;
    private int maxOccurLimit;
    private int fElementAttributeLimit;
    
    public SecurityManager() {
        this.entityExpansionLimit = 64000;
        this.maxOccurLimit = 5000;
        this.fElementAttributeLimit = 10000;
        this.readSystemProperties();
    }
    
    public void setEntityExpansionLimit(final int limit) {
        this.entityExpansionLimit = limit;
    }
    
    public int getEntityExpansionLimit() {
        return this.entityExpansionLimit;
    }
    
    public void setMaxOccurNodeLimit(final int limit) {
        this.maxOccurLimit = limit;
    }
    
    public int getMaxOccurNodeLimit() {
        return this.maxOccurLimit;
    }
    
    public int getElementAttrLimit() {
        return this.fElementAttributeLimit;
    }
    
    public void setElementAttrLimit(final int limit) {
        this.fElementAttributeLimit = limit;
    }
    
    private void readSystemProperties() {
        try {
            final String value = System.getProperty("entityExpansionLimit");
            if (value != null && !value.equals("")) {
                this.entityExpansionLimit = Integer.parseInt(value);
                if (this.entityExpansionLimit < 0) {
                    this.entityExpansionLimit = 64000;
                }
            }
            else {
                this.entityExpansionLimit = 64000;
            }
        }
        catch (final Exception ex) {}
        try {
            final String value = System.getProperty("maxOccurLimit");
            if (value != null && !value.equals("")) {
                this.maxOccurLimit = Integer.parseInt(value);
                if (this.maxOccurLimit < 0) {
                    this.maxOccurLimit = 5000;
                }
            }
            else {
                this.maxOccurLimit = 5000;
            }
        }
        catch (final Exception ex2) {}
        try {
            final String value = System.getProperty("elementAttributeLimit");
            if (value != null && !value.equals("")) {
                this.fElementAttributeLimit = Integer.parseInt(value);
                if (this.fElementAttributeLimit < 0) {
                    this.fElementAttributeLimit = 10000;
                }
            }
            else {
                this.fElementAttributeLimit = 10000;
            }
        }
        catch (final Exception ex3) {}
    }
}

package org.apache.xerces.util;

public final class SecurityManager
{
    private static final int DEFAULT_ENTITY_EXPANSION_LIMIT = 100000;
    private static final int DEFAULT_MAX_OCCUR_NODE_LIMIT = 3000;
    private int entityExpansionLimit;
    private int maxOccurLimit;
    
    public SecurityManager() {
        this.entityExpansionLimit = 100000;
        this.maxOccurLimit = 3000;
    }
    
    public void setEntityExpansionLimit(final int entityExpansionLimit) {
        this.entityExpansionLimit = entityExpansionLimit;
    }
    
    public int getEntityExpansionLimit() {
        return this.entityExpansionLimit;
    }
    
    public void setMaxOccurNodeLimit(final int maxOccurLimit) {
        this.maxOccurLimit = maxOccurLimit;
    }
    
    public int getMaxOccurNodeLimit() {
        return this.maxOccurLimit;
    }
}

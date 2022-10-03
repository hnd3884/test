package com.adventnet.client.view.dynamiccontentarea.web;

public class ContentAreaItem
{
    private String uniqueId;
    
    public ContentAreaItem(final String uniqueIdArg) {
        this.uniqueId = uniqueIdArg;
    }
    
    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        final boolean result = obj instanceof ContentAreaItem && ((ContentAreaItem)obj).uniqueId.equals(this.uniqueId);
        return result;
    }
    
    public String getUniqueId() {
        return this.uniqueId;
    }
    
    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    @Override
    public String toString() {
        return this.uniqueId;
    }
}

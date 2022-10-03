package com.sun.xml.internal.stream.buffer.stax;

import java.util.ArrayList;
import java.util.List;
import com.sun.xml.internal.stream.buffer.AbstractCreator;

abstract class StreamBufferCreator extends AbstractCreator
{
    private boolean checkAttributeValue;
    protected List<String> attributeValuePrefixes;
    
    StreamBufferCreator() {
        this.checkAttributeValue = false;
        this.attributeValuePrefixes = new ArrayList<String>();
    }
    
    protected void storeQualifiedName(int item, final String prefix, final String uri, final String localName) {
        if (uri != null && uri.length() > 0) {
            if (prefix != null && prefix.length() > 0) {
                item |= 0x1;
                this.storeStructureString(prefix);
            }
            item |= 0x2;
            this.storeStructureString(uri);
        }
        this.storeStructureString(localName);
        this.storeStructure(item);
    }
    
    protected final void storeNamespaceAttribute(final String prefix, final String uri) {
        int item = 64;
        if (prefix != null && prefix.length() > 0) {
            item |= 0x1;
            this.storeStructureString(prefix);
        }
        if (uri != null && uri.length() > 0) {
            item |= 0x2;
            this.storeStructureString(uri);
        }
        this.storeStructure(item);
    }
    
    protected final void storeAttribute(final String prefix, final String uri, final String localName, final String type, final String value) {
        this.storeQualifiedName(48, prefix, uri, localName);
        this.storeStructureString(type);
        this.storeContentString(value);
        if (this.checkAttributeValue && value.indexOf("://") == -1) {
            final int firstIndex = value.indexOf(":");
            final int lastIndex = value.lastIndexOf(":");
            if (firstIndex != -1 && lastIndex == firstIndex) {
                final String valuePrefix = value.substring(0, firstIndex);
                if (!this.attributeValuePrefixes.contains(valuePrefix)) {
                    this.attributeValuePrefixes.add(valuePrefix);
                }
            }
        }
    }
    
    public final List getAttributeValuePrefixes() {
        return this.attributeValuePrefixes;
    }
    
    protected final void storeProcessingInstruction(final String target, final String data) {
        this.storeStructure(112);
        this.storeStructureString(target);
        this.storeStructureString(data);
    }
    
    public final boolean isCheckAttributeValue() {
        return this.checkAttributeValue;
    }
    
    public final void setCheckAttributeValue(final boolean value) {
        this.checkAttributeValue = value;
    }
}

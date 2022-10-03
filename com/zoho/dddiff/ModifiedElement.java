package com.zoho.dddiff;

import java.util.List;
import org.w3c.dom.Element;

public class ModifiedElement
{
    private Element oldElement;
    private Element newElement;
    private String tableName;
    private String ddName;
    private DataDictionaryDiff.ElementType type;
    private List<String> changedAttributes;
    private List<Element> oldChangedAttributeSpecificElements;
    private List<Element> newChangedAttributeSpecificElements;
    
    ModifiedElement(final Element oldObj, final Element newObj, final String tableName, final String ddName, final DataDictionaryDiff.ElementType type, final List<String> changes) {
        this.oldElement = oldObj;
        this.newElement = newObj;
        this.tableName = tableName;
        this.ddName = ddName;
        this.type = type;
        this.changedAttributes = changes;
    }
    
    ModifiedElement(final String ddName, final List<String> changedAttributes, final List<Element> oldChangedAttributeSpecificElements, final List<Element> newChangedAttributeSpecificElements) {
        this.ddName = ddName;
        this.type = DataDictionaryDiff.ElementType.DD;
        this.changedAttributes = changedAttributes;
        this.oldChangedAttributeSpecificElements = oldChangedAttributeSpecificElements;
        this.newChangedAttributeSpecificElements = newChangedAttributeSpecificElements;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getDDName() {
        return this.ddName;
    }
    
    public Element getOldElement() {
        return this.oldElement;
    }
    
    public Element getNewElement() {
        return this.newElement;
    }
    
    public DataDictionaryDiff.ElementType getType() {
        return this.type;
    }
    
    public List<String> getChangedAttributes() {
        return this.changedAttributes;
    }
    
    public Element getOldElementForChangedAttribute(final String attributeName) {
        if (this.type != DataDictionaryDiff.ElementType.DD) {
            throw new UnsupportedOperationException("This API can be used, when modified type is DD. Else, use getOldElement() API.");
        }
        return this.oldChangedAttributeSpecificElements.get(this.changedAttributes.indexOf(attributeName));
    }
    
    public Element getNewElementForChangedAttribute(final String attributeName) {
        if (this.type != DataDictionaryDiff.ElementType.DD) {
            throw new UnsupportedOperationException("This API can be used, when modified type is DD. Else, use getNewElement() API.");
        }
        return this.newChangedAttributeSpecificElements.get(this.changedAttributes.indexOf(attributeName));
    }
}

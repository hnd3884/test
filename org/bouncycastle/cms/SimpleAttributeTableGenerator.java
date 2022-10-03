package org.bouncycastle.cms;

import java.util.Map;
import org.bouncycastle.asn1.cms.AttributeTable;

public class SimpleAttributeTableGenerator implements CMSAttributeTableGenerator
{
    private final AttributeTable attributes;
    
    public SimpleAttributeTableGenerator(final AttributeTable attributes) {
        this.attributes = attributes;
    }
    
    public AttributeTable getAttributes(final Map map) {
        return this.attributes;
    }
}

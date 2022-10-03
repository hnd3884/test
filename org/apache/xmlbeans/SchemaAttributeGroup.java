package org.apache.xmlbeans;

import javax.xml.namespace.QName;

public interface SchemaAttributeGroup extends SchemaComponent, SchemaAnnotated
{
    int getComponentType();
    
    QName getName();
    
    Object getUserData();
    
    public static final class Ref extends SchemaComponent.Ref
    {
        public Ref(final SchemaAttributeGroup attributeGroup) {
            super(attributeGroup);
        }
        
        public Ref(final SchemaTypeSystem system, final String handle) {
            super(system, handle);
        }
        
        @Override
        public final int getComponentType() {
            return 4;
        }
        
        public final SchemaAttributeGroup get() {
            return (SchemaAttributeGroup)this.getComponent();
        }
    }
}

package org.apache.xmlbeans;

import javax.xml.namespace.QName;

public interface SchemaModelGroup extends SchemaComponent, SchemaAnnotated
{
    int getComponentType();
    
    QName getName();
    
    Object getUserData();
    
    public static final class Ref extends SchemaComponent.Ref
    {
        public Ref(final SchemaModelGroup modelGroup) {
            super(modelGroup);
        }
        
        public Ref(final SchemaTypeSystem system, final String handle) {
            super(system, handle);
        }
        
        @Override
        public final int getComponentType() {
            return 6;
        }
        
        public final SchemaModelGroup get() {
            return (SchemaModelGroup)this.getComponent();
        }
    }
}

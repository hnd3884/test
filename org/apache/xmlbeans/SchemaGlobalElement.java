package org.apache.xmlbeans;

import javax.xml.namespace.QName;

public interface SchemaGlobalElement extends SchemaLocalElement, SchemaComponent
{
    QName[] substitutionGroupMembers();
    
    SchemaGlobalElement substitutionGroup();
    
    boolean finalExtension();
    
    boolean finalRestriction();
    
    Ref getRef();
    
    public static final class Ref extends SchemaComponent.Ref
    {
        public Ref(final SchemaGlobalElement element) {
            super(element);
        }
        
        public Ref(final SchemaTypeSystem system, final String handle) {
            super(system, handle);
        }
        
        @Override
        public final int getComponentType() {
            return 1;
        }
        
        public final SchemaGlobalElement get() {
            return (SchemaGlobalElement)this.getComponent();
        }
    }
}

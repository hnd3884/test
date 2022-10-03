package org.apache.xmlbeans;

import java.util.Map;

public interface SchemaIdentityConstraint extends SchemaComponent, SchemaAnnotated
{
    public static final int CC_KEY = 1;
    public static final int CC_KEYREF = 2;
    public static final int CC_UNIQUE = 3;
    
    String getSelector();
    
    Object getSelectorPath();
    
    String[] getFields();
    
    Object getFieldPath(final int p0);
    
    Map getNSMap();
    
    int getConstraintCategory();
    
    SchemaIdentityConstraint getReferencedKey();
    
    Object getUserData();
    
    public static final class Ref extends SchemaComponent.Ref
    {
        public Ref(final SchemaIdentityConstraint idc) {
            super(idc);
        }
        
        public Ref(final SchemaTypeSystem system, final String handle) {
            super(system, handle);
        }
        
        @Override
        public final int getComponentType() {
            return 5;
        }
        
        public final SchemaIdentityConstraint get() {
            return (SchemaIdentityConstraint)this.getComponent();
        }
    }
}

package org.apache.xmlbeans;

public interface SchemaGlobalAttribute extends SchemaLocalAttribute, SchemaComponent
{
    Ref getRef();
    
    public static final class Ref extends SchemaComponent.Ref
    {
        public Ref(final SchemaGlobalAttribute element) {
            super(element);
        }
        
        public Ref(final SchemaTypeSystem system, final String handle) {
            super(system, handle);
        }
        
        @Override
        public final int getComponentType() {
            return 3;
        }
        
        public final SchemaGlobalAttribute get() {
            return (SchemaGlobalAttribute)this.getComponent();
        }
    }
}

package org.apache.xmlbeans;

import javax.xml.namespace.QName;

public interface SchemaComponent
{
    public static final int TYPE = 0;
    public static final int ELEMENT = 1;
    public static final int ATTRIBUTE = 3;
    public static final int ATTRIBUTE_GROUP = 4;
    public static final int IDENTITY_CONSTRAINT = 5;
    public static final int MODEL_GROUP = 6;
    public static final int NOTATION = 7;
    public static final int ANNOTATION = 8;
    
    int getComponentType();
    
    SchemaTypeSystem getTypeSystem();
    
    QName getName();
    
    String getSourceName();
    
    Ref getComponentRef();
    
    public abstract static class Ref
    {
        private volatile SchemaComponent _schemaComponent;
        private SchemaTypeSystem _schemaTypeSystem;
        public String _handle;
        
        protected Ref(final SchemaComponent schemaComponent) {
            this._schemaComponent = schemaComponent;
        }
        
        protected Ref(final SchemaTypeSystem schemaTypeSystem, final String handle) {
            assert handle != null;
            this._schemaTypeSystem = schemaTypeSystem;
            this._handle = handle;
        }
        
        public abstract int getComponentType();
        
        public final SchemaTypeSystem getTypeSystem() {
            return this._schemaTypeSystem;
        }
        
        public final SchemaComponent getComponent() {
            if (this._schemaComponent == null && this._handle != null) {
                synchronized (this) {
                    if (this._schemaComponent == null && this._handle != null) {
                        this._schemaComponent = this._schemaTypeSystem.resolveHandle(this._handle);
                        this._schemaTypeSystem = null;
                    }
                }
            }
            return this._schemaComponent;
        }
    }
}

package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SchemaGlobalAttribute;

public class SchemaGlobalAttributeImpl extends SchemaLocalAttributeImpl implements SchemaGlobalAttribute
{
    SchemaContainer _container;
    String _filename;
    private String _parseTNS;
    private boolean _chameleon;
    private Ref _selfref;
    
    public SchemaGlobalAttributeImpl(final SchemaContainer container) {
        this._selfref = new Ref(this);
        this._container = container;
    }
    
    @Override
    public SchemaTypeSystem getTypeSystem() {
        return this._container.getTypeSystem();
    }
    
    SchemaContainer getContainer() {
        return this._container;
    }
    
    @Override
    public int getComponentType() {
        return 3;
    }
    
    @Override
    public String getSourceName() {
        return this._filename;
    }
    
    public void setFilename(final String filename) {
        this._filename = filename;
    }
    
    public void setParseContext(final XmlObject parseObject, final String targetNamespace, final boolean chameleon) {
        this._parseObject = parseObject;
        this._parseTNS = targetNamespace;
        this._chameleon = chameleon;
    }
    
    public XmlObject getParseObject() {
        return this._parseObject;
    }
    
    public String getTargetNamespace() {
        return this._parseTNS;
    }
    
    public String getChameleonNamespace() {
        return this._chameleon ? this._parseTNS : null;
    }
    
    @Override
    public Ref getRef() {
        return this._selfref;
    }
    
    @Override
    public SchemaComponent.Ref getComponentRef() {
        return this.getRef();
    }
}

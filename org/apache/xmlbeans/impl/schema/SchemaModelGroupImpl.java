package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.XmlObject;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaModelGroup;

public class SchemaModelGroupImpl implements SchemaModelGroup
{
    private SchemaContainer _container;
    private QName _name;
    private XmlObject _parseObject;
    private Object _userData;
    private String _parseTNS;
    private boolean _chameleon;
    private String _elemFormDefault;
    private String _attFormDefault;
    private boolean _redefinition;
    private SchemaAnnotation _annotation;
    private String _filename;
    private Ref _selfref;
    
    public SchemaModelGroupImpl(final SchemaContainer container) {
        this._selfref = new Ref(this);
        this._container = container;
    }
    
    public SchemaModelGroupImpl(final SchemaContainer container, final QName name) {
        this._selfref = new Ref(this);
        this._container = container;
        this._name = name;
    }
    
    public void init(final QName name, final String targetNamespace, final boolean chameleon, final String elemFormDefault, final String attFormDefault, final boolean redefinition, final XmlObject x, final SchemaAnnotation a, final Object userData) {
        assert !(!name.equals(this._name));
        this._name = name;
        this._parseTNS = targetNamespace;
        this._chameleon = chameleon;
        this._elemFormDefault = elemFormDefault;
        this._attFormDefault = attFormDefault;
        this._redefinition = redefinition;
        this._parseObject = x;
        this._annotation = a;
        this._userData = userData;
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
        return 6;
    }
    
    public void setFilename(final String filename) {
        this._filename = filename;
    }
    
    @Override
    public String getSourceName() {
        return this._filename;
    }
    
    @Override
    public QName getName() {
        return this._name;
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
    
    public String getElemFormDefault() {
        return this._elemFormDefault;
    }
    
    public String getAttFormDefault() {
        return this._attFormDefault;
    }
    
    public boolean isRedefinition() {
        return this._redefinition;
    }
    
    @Override
    public SchemaAnnotation getAnnotation() {
        return this._annotation;
    }
    
    public Ref getRef() {
        return this._selfref;
    }
    
    @Override
    public SchemaComponent.Ref getComponentRef() {
        return this.getRef();
    }
    
    @Override
    public Object getUserData() {
        return this._userData;
    }
}

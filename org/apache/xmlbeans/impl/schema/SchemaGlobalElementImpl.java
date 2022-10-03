package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaTypeSystem;
import java.util.LinkedHashSet;
import javax.xml.namespace.QName;
import java.util.Set;
import org.apache.xmlbeans.SchemaGlobalElement;

public class SchemaGlobalElementImpl extends SchemaLocalElementImpl implements SchemaGlobalElement
{
    private Set _sgMembers;
    private static final QName[] _namearray;
    private boolean _finalExt;
    private boolean _finalRest;
    private SchemaContainer _container;
    private String _filename;
    private String _parseTNS;
    private boolean _chameleon;
    private Ref _sg;
    private Ref _selfref;
    
    public SchemaGlobalElementImpl(final SchemaContainer container) {
        this._sgMembers = new LinkedHashSet();
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
    public String getSourceName() {
        return this._filename;
    }
    
    public void setFilename(final String filename) {
        this._filename = filename;
    }
    
    void setFinal(final boolean finalExt, final boolean finalRest) {
        this.mutate();
        this._finalExt = finalExt;
        this._finalRest = finalRest;
    }
    
    @Override
    public int getComponentType() {
        return 1;
    }
    
    @Override
    public SchemaGlobalElement substitutionGroup() {
        return (this._sg == null) ? null : this._sg.get();
    }
    
    public void setSubstitutionGroup(final Ref sg) {
        this._sg = sg;
    }
    
    @Override
    public QName[] substitutionGroupMembers() {
        return this._sgMembers.toArray(SchemaGlobalElementImpl._namearray);
    }
    
    public void addSubstitutionGroupMember(final QName name) {
        this.mutate();
        this._sgMembers.add(name);
    }
    
    @Override
    public boolean finalExtension() {
        return this._finalExt;
    }
    
    @Override
    public boolean finalRestriction() {
        return this._finalRest;
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
    
    static {
        _namearray = new QName[0];
    }
}
